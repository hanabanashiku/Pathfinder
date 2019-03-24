package tk.pathfinder.Networking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import tk.pathfinder.Map.*;

/**
 * Shows current stats for the running app.
 * @author Michael MacLean
 * @version 1.0
 * @since 1.0
 */
public class AppStatus {
    private static Map currentMap;
    private static Point location;
    private static BeaconReceiver receiver;
    private static Context appContext;

    public static Map getCurrentMap(){
        return currentMap;
    }

    public static void setCurrentMap(Map map){
        currentMap = map;

        // tell the main activity that the map has changed!
        LocalBroadcastManager.getInstance(appContext).sendBroadcast(
                new Intent("tk.pathfinder.MAP_CHANGED")
        );
    }

    public static void pullMap(int map_id){
        try {
            Map map = Api.GetMap(map_id);
            setCurrentMap(map);
            setCurrentLocation(Point.getDefault());
        } catch (IOException e) {
            Log.d("API", e.getMessage(), e);
            setCurrentMap(null);
        }
    }

    public static Point getCurrentLocation(){
        return location;
    }

    public static void setCurrentLocation(Point p){
        location = p;
    }

    /**
     * @return the ID of the current building, or -1 on failure.
     */
    public static int getCurrentBuildingId(){
        if(currentMap == null)
            return -1;
        return currentMap.getId();
    }

    public static BeaconReceiver getBeaconReceiver(){
        return receiver;
    }

    public static void setBeaconReceiver(BeaconReceiver value){
        receiver = value;
    }

    public static void setAppContext(Context ctx){
        appContext = ctx;
    }

    private AppStatus() {}
}
