package tk.pathfinder.Networking;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import tk.pathfinder.Map.*;
import tk.pathfinder.UI.HomeActivity;
import tk.pathfinder.UI.MapReceiver;
import tk.pathfinder.UI.MapSearchActivity;
import tk.pathfinder.UI.NavigationActivity;
import tk.pathfinder.UI.NavigationSearchActivity;
import tk.pathfinder.UI.ViewMapActivity;

/**
 * Shows current stats for the running app.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class AppStatus extends Application {
    private Map currentMap;
    private Point location;
    private BeaconReceiver receiver;
    private HomeActivity home;
    private NavigationActivity navigation;
    private NavigationSearchActivity navSearch;
    private MapSearchActivity mapSearch;
    private ViewMapActivity viewMap;
    private Activity current;
    private MapReceiver mapReceiver;

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

    public void setCurrentMap(Map map){
        currentMap = map;

        // tell the main activity that the map has changed!
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent("tk.pathfinder.MAP_CHANGED")
        );
    }

    public void pullMap(int map_id){
        try {
            Map map = Api.getMap(map_id);
            setCurrentMap(map);
            setCurrentLocation(Point.getDefault());
        } catch (IOException e) {
            Log.d("API", e.getMessage(), e);
            setCurrentMap(null);
        }
    }

    public Point getCurrentLocation(){
        return location;
    }

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

    public BeaconReceiver getBeaconReceiver(){
        return receiver;
    }

    public void setBeaconReceiver(BeaconReceiver value){
        receiver = value;

        if(Build.VERSION.SDK_INT >= 28){
            IntentFilter rttIntent = new IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED);
            registerReceiver(value, rttIntent);

        }
    }

    public MapReceiver getMapReceiver() {
        return mapReceiver;
    }

    public void setMapReceiver(MapReceiver value){
        mapReceiver = value;
        IntentFilter mf = new IntentFilter("tk.pathfinder.MAP_CHANGED");
        registerReceiver(value, mf);
    }

    public AppStatus() {}
    
    @Override
    public void onCreate(){
        super.onCreate();
        location = Point.getDefault();
    }
}
