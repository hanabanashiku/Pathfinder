package tk.pathfinder.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.R;

public class ViewMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CoordinatorLayout container = findViewById(R.id.map_container);

        // display the map widget
        int mapId = savedInstanceState.getInt("mapId");
        MapViewFragment f = MapViewFragment.newInstance(mapId);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.add(R.id.map_container, f);
        t.commit();
    }

    @Override
    protected void onResume(){
        super.onResume();
        ((AppStatus)getApplicationContext()).setCurrentActivity(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        AppStatus status = (AppStatus)getApplicationContext();
        if(status.getCurrentActivity() == this)
            status.setCurrentActivity(null);
    }

}
