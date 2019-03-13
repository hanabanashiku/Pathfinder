package tk.pathfinder.Networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import tk.pathfinder.Map.Map;

public class Wifi extends BroadcastReceiver implements Iterable<Beacon> {
    private List<Beacon> beacons;
    private Map map;

    public Wifi(Map map){
        beacons = new ArrayList<>();
    }

    public int getBuildingId(){
        if(beacons.size() == 0)
            return -1;
        return beacons.get(0).getBuildingIndex();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager mgr = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> res = mgr.getScanResults();
        List<Beacon> topThree = new ArrayList<>();

        for(ScanResult i : res){
            Beacon b = map.getBeacon(i.SSID);

            // this beacon is not in our map!
            if(b == null)
                continue;

            b.setLevel(i.level);
            if(topThree.size() < 3){
                topThree.add(b);
                continue;
            }

            Beacon lowest = getLowestRssi(topThree);
            if(i.level > lowest.getLevel()){
                topThree.remove(lowest);
                topThree.add(b);
            }
        }

        beacons = new ArrayList<>();
        beacons.addAll(topThree);
    }

    private Beacon getLowestRssi(List<Beacon> results){
        Beacon lowest = null;
        for(Beacon i : results){
            if(lowest == null || i.getLevel() < lowest.getLevel())
                lowest = i;
        }
        return lowest;
    }

    @NonNull
    @Override
    public Iterator<Beacon> iterator() {
        return beacons.iterator();
    }
}
