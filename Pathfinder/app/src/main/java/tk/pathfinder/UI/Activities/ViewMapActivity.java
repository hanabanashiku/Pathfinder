package tk.pathfinder.UI.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.UI.Alert;
import tk.pathfinder.UI.Fragments.MapViewFragment;

public class ViewMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // display the map widget

        int mapId = getIntent().getIntExtra("mapId", -1);
        if(mapId == -1){
            new Alert("Error", "An unknown error has occurred.", this).show();
            return;
        }
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
