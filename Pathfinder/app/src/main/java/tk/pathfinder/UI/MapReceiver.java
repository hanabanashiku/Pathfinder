package tk.pathfinder.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import tk.pathfinder.Map.Map;
import tk.pathfinder.UI.Activities.HomeActivity;
import tk.pathfinder.UI.Activities.NavigationSearchActivity;

public class MapReceiver extends BroadcastReceiver {

    // manage the activities to ensure we are on the correct one when the map is changed.
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MapReceiver", "Changing maps!");
        AppStatus status = (AppStatus)context.getApplicationContext();
        if(status.getHomeActivity() == null)
            return;
        Map current = status.getCurrentMap();
        if(current == null){
            // switch to home activity if we aren't already there
            if(status.getCurrentActivity() != status.getHomeActivity()){
                Intent i = new Intent(status.getCurrentActivity(), HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                status.getCurrentActivity().startActivityIfNeeded(i, 0);
            }

            // show the no map fragment
            status.getHomeActivity().setNoMap();
        }

        // we have a map, so show the destination search
        else{
            NavigationSearchActivity nav = null;
            if(status.getCurrentActivity() == status.getNavigationSearchActivity())
                nav = status.getNavigationSearchActivity();

            Intent i = new Intent(status.getCurrentActivity(), NavigationSearchActivity.class);
            status.getCurrentActivity().startActivity(i);

            // don't keep two references!
            if(nav != null)
                nav.finish();
        }
    }
}
