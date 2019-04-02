package tk.pathfinder.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.R;

public class MapSearchActivity extends AppCompatActivity implements MapResult.MapResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String keywords = i.getStringExtra("keywords");

        // submit our search
        if(keywords != null){
            EditText search = findViewById(R.id.map_search_box);
            ImageButton submit = findViewById(R.id.map_search_submit);
            search.setText(keywords);
            submit.performClick();
        }
    }

    public void onSubmitClick(View v){
        Log.d("MapSearchActivity", "submitting..");
        String keywords = ((EditText)findViewById(R.id.map_search_box)).getText().toString();
        if(keywords.isEmpty()) // don't submit for an empty string
            return;

        FragmentManager fm = getSupportFragmentManager();
        MapResultsFragment f = MapResultsFragment.newInstance(keywords);
        FragmentTransaction t = fm.beginTransaction();
        t.replace(R.id.map_results_content, f);
        t.commit();
    }

    // display the map
    @Override
    public void onMapSelected(int mapId) {
        AppStatus status = (AppStatus)getApplicationContext();
        Intent i = new Intent(status.getCurrentActivity(), ViewMapActivity.class);
        i.putExtra("mapId", mapId);
        startActivity(i);
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
        status.setMapSearchActivity(null);
        if(status.getCurrentActivity() == this)
            status.setCurrentActivity(null);
    }
}
