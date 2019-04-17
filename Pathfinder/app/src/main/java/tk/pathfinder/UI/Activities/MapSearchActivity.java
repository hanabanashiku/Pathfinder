package tk.pathfinder.UI.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.UI.Fragments.MapResult;
import tk.pathfinder.UI.Fragments.MapResultsFragment;

public class MapSearchActivity extends MenuActivity implements MapResult.MapResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String keywords = i.getStringExtra("keywords");
        EditText search = findViewById(R.id.map_search_box);
        ImageButton submit = findViewById(R.id.map_search_submit);

        // submit our search
        if(keywords != null){
            search.setText(keywords);
            submit.performClick();
        }

        search.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                submit.performClick();
                return true;
            }
            return false;
        });
    }

    public void onSubmitClick(View v){
        String keywords = ((EditText)findViewById(R.id.map_search_box)).getText().toString();
        if(keywords.isEmpty()) // don't submit for an empty string
            return;

        FragmentManager fm = getSupportFragmentManager();
        MapResultsFragment f = MapResultsFragment.newInstance(keywords);
        FragmentTransaction t = fm.beginTransaction();
        t.replace(R.id.map_results_content, f);
        t.commit();
        getSupportActionBar().setTitle("Search Results");
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
