package tk.pathfinder.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;


import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Navigation;
import tk.pathfinder.Map.Node;
import tk.pathfinder.Map.Path;
import tk.pathfinder.Map.Room;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.UI.NavigationView;
import tk.pathfinder.exceptions.NoValidPathException;

public class NavigationActivity extends AppCompatActivity {

    private Room destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppStatus status = (AppStatus) getApplicationContext();
        status.setNavigationActivity(this);

        Intent i = getIntent();

        int roomId = i.getIntExtra("roomId", -1);
        // an argument was provided
        if (roomId == -1)
            return;
        for (Iterator<Room> it = status.getCurrentMap().getRooms(); it.hasNext(); ) {
            Room room = it.next();
            if (room.getId() == roomId)
                destination = room;
        }

        // define the text on the top
        TextView text = findViewById(R.id.nav_label);
        text.setText(String.format("Navigating to %s", destination.getName()));

        // insert navigation view
        NavigationView v = new NavigationView(this);
        v.setDestination(destination);
        v.setMap(((AppStatus) getApplicationContext()).getCurrentMap());
        FrameLayout l = findViewById(R.id.navigation_view_container);
        l.addView(v);
    }

}
