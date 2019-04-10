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
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Navigation;
import tk.pathfinder.Map.Point;
import tk.pathfinder.UI.AppStatus;

/**
 * A Broadcast receiver that looks for Pathfinder beacons and updates the app state accordingly.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class BeaconReceiver extends BroadcastReceiver implements Iterable<Beacon> {
    private List<Beacon> beacons;
    private WifiManager wifiManager;
    private WifiRttManager rttManager;
    private WifiManager.WifiLock wifiLock;
    private List<ScanResult> lastResults;
    Thread t = null; // the rtt thread

    public BeaconReceiver(Context app) {
        beacons = new ArrayList<>();
        wifiManager = (WifiManager) app.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // make sure the radios don't go to sleep
        wifiLock = wifiManager
                .createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF,
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

    /**
     * @return The active wifi lock.
     */
    public WifiManager.WifiLock getWifiLock() {
        return wifiLock;
    }

    /**
     * This function is run whenever a new scan result is available.
     * It updates the beacon table, updates the signal strength,
     * pulls the relevant map if a new building has been entered,
     * and sorts the beacons by strength.
     * This function is atomic (it can only be run once at a time).
     * It will run ~4 times/2 minutes for API >= 28 and every half second otherwise.
     * @param context The context
     * @param intent The intent
     */
    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        AppStatus ctx = (AppStatus) context.getApplicationContext();
        Map currentMap = ctx.getCurrentMap();
        lastResults = wifiManager.getScanResults();

        Log.d("BeaconReceiver", "Received " + lastResults.size() + " signals");

        if(currentMap == null){
            // pull from the results to get the right map.
            int building = getCurrentMapId();

            new Thread() {
                @Override
                public void run(){
                    ctx.pullMap(building);
                    processResults(ctx);
                }
            }.start();
        }
        else
            processResults(ctx);
    }

    // process the last received set of results from the WifiManager.
    private void processResults(AppStatus ctx){
        List<Beacon> current = new ArrayList<>();

        Map currentMap = ctx.getCurrentMap();

        if(lastResults == null || currentMap == null)
            return;

        for(ScanResult i : lastResults) {
            // not ours, continue
            if(Beacon.parseSsid(i.SSID).length == 0)
                continue;

            Log.d("BeaconReceiver", "Discovered access point " + i.SSID);

            Beacon b = findBeacon(i.SSID);
            if(b == null){ // if we couldn't find it in our beacon set, pull it from the map
                b = currentMap.getBeacon(i.SSID);
                if(b == null) // if it's not in the map, make a new object.
                    b = new Beacon(i.SSID, Point.getDefault());
            }
            // we have the beacon but don't know the location.
            else if(b.getLocation().equals(Point.getDefault())){
                Beacon fromMap = currentMap.getBeacon(i.SSID);
                if(fromMap != null) // we found the beacon in our map
                    b = fromMap;
            }
            // otherwise, we have the beacon and it shows the correct location

            // update our values from the pull
            b.setLevel(i.level);
            current.add(b);
        }

        Collections.sort(current);
        beacons = current; // update our collection [probably not be thread safe]

        // no beacons.. don't show a map.
        if(beacons.size() == 0){
            ctx.setCurrentMap(null);
            return;
        }

        // if we are in a different map, pull it and
        // update the beacon references.
        int currentMapId = getCurrentMapId();
        if(!currentMap.getId().equals(currentMapId)){
            new Thread(){
                @Override
                public void run(){
                    ctx.pullMap(currentMapId);
                    changeMap(ctx);
                    setLocation(ctx);
                }
            }.start();
        }
        else
            // set the current location of the user based on the closest nodes.
            setLocation(ctx);

        // trigger RTT loop for >= 28 if not already done
        if (Build.VERSION.SDK_INT >= 28 && t == null){
            t = new RttThread();
            t.start();
        }
    }

    /**
     * @return The top three closest beacons on the same floor based on signal strength.
     */
    public List<Beacon> closestBeacons(){
        List<Beacon> ret = new ArrayList<>();
        // this is always going to be sorted!
        for(int i = 0; i < beacons.size(); i++) {
            if (ret.size() == 3)
                break;
            // make sure they are all on the same floor.
            if(ret.size() > 1 &&
                    ret.get(0).getLocation().getY() != beacons.get(i).getLocation().getY())
                continue;
            ret.add(beacons.get(i));
        }
        return ret;
    }

    // set the location of the user wrt to the current map.
    private void setLocation(AppStatus ctx){
        List<Beacon> closest = closestBeacons();

        int size = closest.size();
        Point loc;
        if(size == 1)
            loc = Navigation.triangulate(closest.get(0), null, null);
        else if(size == 2)
            loc = Navigation.triangulate(closest.get(0), closest.get(1), null);
        else
            loc = Navigation.triangulate(closest.get(0), closest.get(1), closest.get(2));
        ctx.setCurrentLocation(loc);
    }

    // find a beacon with a given ssid
    private Beacon findBeacon(String ssid){
        for(Beacon b : beacons)
            if(b.getSSID().equals(ssid))
                return b;
        return null;
    }

    /* Get the beacon consensus on the current map.
     * If there are multiple maps, the one with the largest number of beacons will be selected. */
    private int getCurrentMapId(){
        if(lastResults == null || lastResults.size() == 0)
            return -1;

        // each node gets a vote
        SparseIntArray votes = new SparseIntArray();

        for(ScanResult r : lastResults){
            int[] ids = Beacon.parseSsid(r.SSID);

            if(ids.length == 0)
                continue;
            int building = ids[0];

            int numVotes = votes.get(building, 0);
            votes.put(building, numVotes + 1);
        }

        int max = -1;
        int max_key = -1;
        for(int k = 0; k < votes.size(); k++){
            int key = votes.keyAt(k);
            int i = votes.get(key);
            if(i > max){
                max = i;
                max_key = key;
            }
        }
        return max_key;
    }

    // change the beacon references to mach the current app-wide map.
    private void changeMap(AppStatus app){

        for(Iterator<Beacon> i = app.getCurrentMap().getBeacons(); i.hasNext(); ){
            Beacon b = i.next();

            Beacon fromList = findBeacon(b.getSSID());
            if(fromList == null)
                continue;
            int index = beacons.indexOf(fromList);
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

    /**
     * A callback class for managing the RTT ranging results and updating the beacon signal strengths accordingly.
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    class BeaconRangingResultCallback extends RangingResultCallback {
        @Override
        public void onRangingResults(List<RangingResult> results){
            Log.d("BeaconRangingResult", "Ranging request received.");
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
            Log.e("BeaconRangingResult", "Ranging failed with code " + code);
        }
    }

    /**
     * A thread that periodically initiates a wifi scan.
     * The number of times per minute varies based on whether the user is on or above API 28.
     */
    class ScanThread extends Thread{
        private final int delay;

        ScanThread(){
            super();
            // if we are above 28 we will rely more on the RTT api.
            if(Build.VERSION.SDK_INT >= 28)
                delay = 32000;
            else delay = 1000;
        }
        public void run(){
            while(true){
                Log.d("BeaconReceiver", "Starting scan.");
                wifiManager.startScan();
                try{
                    sleep(delay);
                }
                catch(InterruptedException ignored){ }

            }
        }
    }

    /**
     * A thread that periodically sends RTT ranging requests to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    class RttThread extends Thread {
        public void run(){
            while(true){
                try{
                    sleep(1000);
                }
                catch(InterruptedException ignored) {}

                if(lastResults == null || rttManager == null)
                    continue;

                RangingRequest r = new RangingRequest.Builder().addAccessPoints(lastResults).build();
                try{
                    rttManager.startRanging(r, AsyncTask.THREAD_POOL_EXECUTOR, new BeaconRangingResultCallback());
                }
                catch(SecurityException e){
                    Log.e("BeaconReceiver", "Ranging permission denied..");
                }
            }
        }
    }
}
