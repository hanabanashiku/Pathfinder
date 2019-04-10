package tk.pathfinder.UI;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import tk.pathfinder.Map.*;
import tk.pathfinder.Networking.Api;
import tk.pathfinder.Networking.BeaconReceiver;
import tk.pathfinder.UI.Activities.HomeActivity;
import tk.pathfinder.UI.Activities.MapSearchActivity;
import tk.pathfinder.UI.Activities.NavigationActivity;
import tk.pathfinder.UI.Activities.NavigationSearchActivity;
import tk.pathfinder.UI.Activities.ViewMapActivity;

/**
 * Shows current stats for the running app.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class AppStatus extends Application {
    private Map currentMap;
    private Point location;
    private HomeActivity home;
    private NavigationActivity navigation;
    private NavigationSearchActivity navSearch;
    private MapSearchActivity mapSearch;
    private ViewMapActivity viewMap;
    private Activity current;

    public HomeActivity getHomeActivity() { return home; }
    public void setHomeActivity(HomeActivity value) { home = value;}

    public NavigationActivity getNavigationActivity() { return navigation; }
    public void setNavigationActivity(NavigationActivity value) { navigation = value; }

    public NavigationSearchActivity getNavigationSearchActivity() { return navSearch; }
    public void setNavigationSearchActivity(NavigationSearchActivity value) { navSearch = value; }

    public MapSearchActivity getMapSearchActivity() { return mapSearch; }
    public void setMapSearchActivity(MapSearchActivity value) { mapSearch = value; }

    public ViewMapActivity getViewMapActivity() { return viewMap; }
    public void setViewMapActivity(ViewMapActivity value) { viewMap = value; }

    public Activity getCurrentActivity() { return current; }
    public void setCurrentActivity(Activity value) { current = value; }


    public Map getCurrentMap(){
        return currentMap;
    }

    /**
     * Set the current map and send a tk.pathfinder.MAP_CHANGED action.
     * @param map The map to set.
     */
    public void setCurrentMap(Map map){
        currentMap = map;

        // tell the main activity that the map has changed!
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent("tk.pathfinder.MAP_CHANGED")
        );
    }

    /**
     * Pull a map from the database and set it to the current map.
     * @param map_id The index of the map to pull.
     */
    public void pullMap(int map_id){
        try {
            Map map = Api.getMap(map_id);
            setCurrentMap(map);
            setCurrentLocation(Point.getDefault());
        } catch (IOException e) {
            Log.e("API", e.getMessage(), e);
            setCurrentMap(null);
        }
    }

    /**
     * @return The last calculated user location on the map.
     */
    public Point getCurrentLocation(){
        return location;
    }

    /**
     * @param p The user's current location on the map.
     */
    public void setCurrentLocation(Point p){
        location = p;
    }

    /**
     * @return the ID of the current building, or -1 on failure.
     */
    public int getCurrentBuildingId(){
        if(currentMap == null)
            return -1;
        return currentMap.getId();
    }

    public AppStatus() {}
    
    @Override
    public void onCreate(){
        super.onCreate();
        location = Point.getDefault();

        // register our receivers
        BeaconReceiver beaconReceiver = new BeaconReceiver(this);
        IntentFilter bf = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(beaconReceiver, bf);

        MapReceiver mapReceiver = new MapReceiver();
        IntentFilter mf = new IntentFilter("tk.pathfinder.MAP_CHANGED");
        registerReceiver(mapReceiver, mf);

        // keep the radios awake
        beaconReceiver.getWifiLock().acquire();
    }
}
