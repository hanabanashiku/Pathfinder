package tk.pathfinder.Networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Point;

public class BeaconReceiver extends BroadcastReceiver implements Iterable<Beacon> {
    private List<Beacon> beacons;
    private WifiManager wifiMananger;
    private WifiManager.WifiLock wifiLock;

    public BeaconReceiver(Context app){
        beacons = new ArrayList<>();
        wifiMananger = (WifiManager)app.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // make sure the radios don't go to sleep
        wifiLock = wifiMananger
                .createWifiLock((android.os.Build.VERSION.SDK_INT>=19?WifiManager.WIFI_MODE_FULL_HIGH_PERF:WifiManager.WIFI_MODE_FULL),
                        "pathfinder_wifi_lock");

        // start wifi scanning
        // note, triggering a scan is deprecated
        new ScanThread().run();
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
    public synchronized void onReceive(Context context, Intent intent){
        List<ScanResult> res = wifiMananger.getScanResults();

        List<Beacon> current = new ArrayList<>();
        Map currentMap = AppStatus.getCurrentMap();
        HashMap<Integer, Integer> beaconStrength = new HashMap<>();

        for(ScanResult i : res){
            String[] parts = i.SSID.split("_");

            // this is one of ours! let's add it
            if(parts[0].equals("PF")){
                Beacon b = findBeaconBySSID(beacons, i.SSID);
                if(b == null) { // unseen node
                    try {
                        b = new Beacon(i.SSID, Point.getDefault());
                    }
                    catch(Exception e){
                        continue; // incorrect format...
                    }

                    // we have a map node but no map! Pull the map!
                    if(currentMap == null)
                        AppStatus.pullMap(b.getBuildingIndex());

                }
                b.setFrequency(i.frequency);
                // add our node. This is either the reference we already knew,
                // or it is the new placeholder node we have created.
                current.add(b);

                // take note of the signal strength
                beaconStrength.put(b.getIndex(), i.level);
            }
        }

        beacons = current; // update the collection.

        // we are out of range of a map!
        if(beacons.size() == 0){
            AppStatus.setCurrentMap(null);
            return;
        }

        // make sure we have the correct map
        int curr_map = getCurrentMapId();
        if(curr_map != AppStatus.getCurrentBuildingId())
            AppStatus.pullMap(curr_map);

        // make sure we have the correct references.
        correctBeaconReferences(AppStatus.getCurrentMap());

        // update the signal strength of the nodes
        updateSignalStrength(beaconStrength);

        // sort by signal strength
        Collections.sort(beacons);
    }

    /**
     * @return The top three closest beacons based on signal strength.
     */
    public List<Beacon> closestBeacons(){
        List<Beacon> ret = new ArrayList<>();
        // this is always going to be sorted!
        for(int i = 0; i < beacons.size(); i++) {
            if (i == 3)
                break;
            ret.add(beacons.get(i));
        }
        return ret;
    }

    private Beacon findBeaconBySSID(Collection<Beacon> beacons, String ssid){
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

    // let's make sure the references reference the the beacon with the correct location info.
    private void correctBeaconReferences(Map map){
        if(map == null)
            return;

        for(int i = 0; i < beacons.size(); i++){
            Beacon b = beacons.get(i);
            Beacon mb = map.getBeacon(b.getSSID());
            // check the reference!
            if(b != mb) {
                beacons.set(i, mb);
                mb.setFrequency(b.getFrequency());
            }
        }
    }

    private void updateSignalStrength(HashMap<Integer, Integer> strengths){
        for(Beacon b : beacons){
            Integer level = strengths.get(b.getIndex());
            if(level == null)
                level = 0;
            b.setLevel(level);
        }

        if(AppStatus.getCurrentMap() != null)
            for(Iterator<Beacon> i = AppStatus.getCurrentMap().getBeacons(); i.hasNext(); ){
                Beacon b = i.next();
                if(beacons.contains(b))
                    continue;
                try{
                    int strength = strengths.get(b.getIndex());
                    b.setLevel(strength);
                }
                catch(Exception e){
                    b.setLevel(-128);
                }
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

    class ScanThread extends Thread{
        public void run(){
            while(true){
                // note: this is currently deprecated;
                // it will have to be removed at some point
                wifiMananger.startScan();
                try{
                    sleep(500);
                }
                catch(InterruptedException ignored){ }

            }
        }
    }
}
