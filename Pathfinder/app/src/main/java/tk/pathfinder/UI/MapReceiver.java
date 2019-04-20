package tk.pathfinder.UI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Navigation;
import tk.pathfinder.R;
import tk.pathfinder.UI.Activities.HomeActivity;
import tk.pathfinder.UI.Activities.MapSearchActivity;
import tk.pathfinder.UI.Activities.NavigationActivity;
import tk.pathfinder.UI.Activities.NavigationSearchActivity;

/**
 * A broadcast receiver for updating the app status when a new map is discovered.
 */
public class MapReceiver extends BroadcastReceiver {

    // manage the activities to ensure we are on the correct one when the map is changed.
    @Override
    public void onReceive(Context context, Intent intent) {
        AppStatus status = (AppStatus)context.getApplicationContext();
        if(status.getHomeActivity() == null)
            return;
        Map currentMap = status.getCurrentMap();

        Activity activity = status.getCurrentActivity();


        // only switch when using the navigation-related activities.
        if(activity == null || activity.getClass() == NavigationActivity.class || activity.getClass() == NavigationSearchActivity.class){
            Intent i = new Intent(context, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(i);
        }

        if(currentMap == null)
            status.getHomeActivity().setNoMap();

        // we have a map
        else{
            // send a push notification if we are outside the app
            if(status.getCurrentActivity() == null)
                sendNotification(context, currentMap.getName());
            status.getHomeActivity().setFoundMap();
        }
    }

    // send a push notification inviting the user to open the map
    private void sendNotification(Context ctx, String mapName){
        Intent i = new Intent(ctx, HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent p = PendingIntent.getActivity(ctx, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "pathfinder")
                .setSmallIcon(R.drawable.ic_launcher_icon)
                .setContentTitle("Welcome to " + mapName + "!")
                .setContentText("If you're feeling lost, try opening up Pathfinder!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(p)
                .setAutoCancel(true);
        NotificationManagerCompat.from(ctx).notify(1, builder.build());
    }
}
