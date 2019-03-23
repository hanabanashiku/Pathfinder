package tk.pathfinder.Networking;

import android.util.Log;

import java.io.IOException;

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

    public static Map getCurrentMap(){
        return currentMap;
    }

    public static void setCurrentMap(Map map){
        currentMap = map;
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
}
