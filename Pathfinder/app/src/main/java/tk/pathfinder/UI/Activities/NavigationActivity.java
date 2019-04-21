package tk.pathfinder.UI.Activities;

import tk.pathfinder.Map.Room;
import tk.pathfinder.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import java.util.Iterator;

import tk.pathfinder.UI.Alert;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.UI.NavigationView;
import tk.pathfinder.exceptions.NoValidPathException;

public class NavigationActivity extends MenuActivity {

    public NavigationView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        AppStatus status = (AppStatus)getApplicationContext();
        status.setNavigationActivity(this);

        Intent i = getIntent();

        int roomId = i.getIntExtra("roomId", -1);
        Room destination = null;

        for(Iterator<Room> it = status.getCurrentMap().getRooms(); it.hasNext(); ){
            Room room = it.next();
            if(room.getId() == roomId)
                destination = room;
        }

        // check for errors
        if(roomId == -1 || destination == null){
            new Alert("Error", "Invalid destination.", this).show();
            finish();
            return;
        }

        view = findViewById(R.id.navigation_view_window);
        view.setCallbackListener(new CallbackListener());
        view.setMap(status.getCurrentMap());
        view.setDestination(destination);

        getSupportActionBar().setTitle(destination.getName());
    }

    public void onLocationClick(View v){
        view.resetPosition();
    }

    @Override
    public void onResume(){
        super.onResume();
        AppStatus status = (AppStatus)getApplicationContext();
        status.setNavigationActivity(this);
        status.setCurrentActivity(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        AppStatus status = (AppStatus)getApplicationContext();
        if(status.getCurrentActivity() == this)
            status.setCurrentActivity(null);
        status.setNavigationActivity(null);
    }

    private class CallbackListener implements NavigationView.NavigationListener {
        @Override
        public void onNoPath(NoValidPathException e){
            new Alert("Could not find route",
                    "There is no way to get from your current location to " + e.getDestination().getName() + ".", getApplicationContext())
                    .show();
            finish();
        }

        @Override
        public void onArrival(){
           // Looper.prepare();
            new Alert("", "You have arrived at your destination", getApplicationContext());
            // go back home
            Intent i = new Intent(NavigationActivity.this, HomeActivity.class);
            startActivity(i);
        }
    }
}
