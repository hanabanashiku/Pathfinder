package tk.pathfinder.Networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Point;

public class BeaconReceiver extends BroadcastReceiver implements Iterable<Beacon> {
    private List<Beacon> beacons;
    private WifiManager wifiManager;
    private WifiRttManager rttManager;
    private WifiManager.WifiLock wifiLock;
    private List<ScanResult> lastResults;

    public BeaconReceiver(Context app) {
        beacons = new ArrayList<>();
        wifiManager = (WifiManager) app.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // make sure the radios don't go to sleep
        wifiLock = wifiManager
                .createWifiLock((android.os.Build.VERSION.SDK_INT >= 19 ? WifiManager.WIFI_MODE_FULL_HIGH_PERF : WifiManager.WIFI_MODE_FULL),
                        "pathfinder_wifi_lock");

        // start a ranging thread to make up for
        // delayed start time
        if(Build.VERSION.SDK_INT >= 28){
            setRttManager(app);
            new RttThread().start();
        }

        // start wifi scanning
        // note, triggering a scan is deprecated
        new ScanThread().start();
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setRttManager(Context app){
        rttManager = (WifiRttManager)app.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);
    }

    public WifiManager.WifiLock getWifiLock() {
        return wifiLock;
    }

    /**
     * This function is run whenever a new signal is received.
     * It updates the beacon table, updates the signal strength,
     * pulls the relevant map if a new building has been entered,
     * and sorts the beacons by strength.
     * This function is atomic (it can only be run once at a time)
     * @param context The context
     * @param intent The intent
     */
    // TODO Update location as well
    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        AppStatus ctx = (AppStatus) context.getApplicationContext();
        Map currentMap = ctx.getCurrentMap();


        // this is a Wifi scan result
        // this will be ran every half second for < 28 and ~4 times/2 minutes for >= 28
        lastResults = wifiManager.getScanResults();
        List<Beacon> current = new ArrayList<>();

        // let's check the results
        for (ScanResult i : lastResults) {
            String[] parts = i.SSID.split("_");

            // likely to be one of ours
            if (parts[0].equals("PF")) {
                int buildingId;
                int nodeId;
                try {
                    buildingId = Integer.parseInt(parts[1]);
                    nodeId = Integer.parseInt(parts[2]);
                }
                // maybe not
                catch (Exception e) {
                    continue;
                }

                // new map found
                if (currentMap == null) {
                    ctx.pullMap(buildingId);
                    currentMap = ctx.getCurrentMap();
                }

                Beacon b = findBeacon(i.SSID);
                // we don't have the beacon listed
                if (b == null) {
                    b = currentMap.getBeacon(i.SSID);
                    if (b == null)
                        b = new Beacon(i.SSID, Point.getDefault());
                }
                // we do but we don't know the location
                else if (b.getLocation().equals(Point.getDefault())) {
                    Beacon fromMap = currentMap.getBeacon(i.SSID);
                    if (fromMap != null) // found the location
                        b = fromMap;
                }

                // update our values from the pull
                b.setFrequency(i.frequency);
                b.setLevel(i.level);
                current.add(b); // make a note of our beacon.
            }
        }

        beacons = current; // update the collection.


        // no beacons..
        if (beacons.size() == 0) {
            ctx.setCurrentMap(null);
            return;
        }

        // if we are in a different map, pull it and
        // update the beacon references.
        int currentMapId = getCurrentMapId();
        if (!currentMap.getId().equals(currentMapId))
            changeMap(currentMapId, ctx);

        // trigger RTT call
        if (Build.VERSION.SDK_INT >= 28) {

        }

        // sort by strength
        Collections.sort(beacons);
    }

    /**
     * @return The top three closest beacons based on signal strength.
     */
    public List<Beacon> closestBeacons(){
        List<Beacon> ret = new ArrayList<>();
        // this is always going to be sorted!
        for(int i = 0; i < beacons.size(); i++) {
            if (ret.size() == 3)
                break;
            ret.add(beacons.get(i));
        }
        return ret;
    }

    private Beacon findBeacon(String ssid){
        for(Beacon b : beacons)
            if(b.getSSID().equals(ssid))
                return b;
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    /* Get the beacon consensus on the current map.
     * If there are multiple maps, the one with the largest number of beacons will be selected. */
    private int getCurrentMapId(){
        if(beacons.size() == 0)
            return -1;

        // each node gets a vote
        HashMap<Integer, Integer> votes = new HashMap<>();

        for(Beacon b : beacons){
            int numVotes = 0;

            if(votes.containsKey(b.getBuildingIndex())){
                numVotes = votes.get(b.getBuildingIndex());
            }
            votes.put(b.getBuildingIndex(), numVotes + 1);
        }

        int max = -1;
        int max_key = -1;
        for(int key : votes.keySet()){
            int i = votes.get(key);
            if(i > max){
                max = i;
                max_key = key;
            }
        }
        return max_key;
    }

    private void changeMap(int mapId, AppStatus app){
        app.pullMap(mapId);

        for(Iterator<Beacon> i = app.getCurrentMap().getBeacons(); i.hasNext(); ){
            Beacon b = i.next();

            Beacon fromList = findBeacon(b.getSSID());
            if(fromList == null)
                continue;
            int index = beacons.indexOf(fromList);
            b.setFrequency(fromList.getFrequency());
            b.setLevel(fromList.getLevel());
            beacons.set(index, b);
        }
    }

    /**
     * @return an iterator containing all of the beacons that are in range.
     */
    @NonNull
    @Override
    public Iterator<Beacon> iterator() {
        return beacons.iterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    class BeaconRangingResultCallback extends RangingResultCallback {
        @Override
        public void onRangingResults(List<RangingResult> results){
            for(RangingResult r : results){
                if(r.getStatus() != RangingResult.STATUS_SUCCESS)
                    continue;

                String ssid = null;
                if(r.getMacAddress() == null)
                    continue;

                // find SSID (the hard way)
                for(ScanResult b : lastResults){
                    if(b.BSSID.equals(r.getMacAddress().toString()))
                        ssid = b.SSID;
                }
                if(ssid == null)
                    continue;

                // found a beacon
                Beacon b = findBeacon(ssid);
                if(b == null)
                    continue;
                b.setLevel(r.getRssi());
            }
        }

        @Override
        public void onRangingFailure(int code){
            Log.d("BeaconReceiver", "Ranging failed with code " + code);
        }
    }

    class ScanThread extends Thread{
        private final int delay;

        public ScanThread(){
            super();
            // if we are above 28 we will rely more on the RTT api.
            if(Build.VERSION.SDK_INT >= 28)
                delay = 32000;
            else delay = 500;
        }
        public void run(){
            while(true){
                wifiManager.startScan();
                try{
                    sleep(delay);
                }
                catch(InterruptedException ignored){ }

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    class RttThread extends Thread {
        public void run(){
            while(true){
                try{
                    sleep(500);
                }
                catch(InterruptedException ignored) {}

                if(lastResults == null)
                    continue;

                RangingRequest r = new RangingRequest.Builder().addAccessPoints(lastResults).build();
                try{
                    rttManager.startRanging(r, AsyncTask.THREAD_POOL_EXECUTOR, new BeaconRangingResultCallback());
                }
                catch(SecurityException e){
                    Log.d("BeaconReceiver", "Ranging permission denied..");
                }
            }
        }
    }
}
