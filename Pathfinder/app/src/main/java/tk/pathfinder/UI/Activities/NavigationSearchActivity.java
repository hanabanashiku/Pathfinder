package tk.pathfinder.UI.Activities;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import androidx.appcompat.widget.Toolbar;

import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.UI.Fragments.NavigationResult;
import tk.pathfinder.UI.Fragments.NavigationResultsFragment;

/**
 * An activity for searching for destinations on a map.
 */
public class NavigationSearchActivity extends MenuActivity implements NavigationResult.NavigationResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppStatus)getApplicationContext()).setNavigationSearchActivity(this);

        Intent i = getIntent();

        String keywords = i.getStringExtra("keywords");

        // let's submit our search!
        if(keywords != null){
            EditText search = findViewById(R.id.dest_search_box);
            ImageButton submit = findViewById(R.id.dest_search_submit);
            search.setText(keywords);
            submit.performClick();
        }
    }

    public void onSubmitClick(View v){
        EditText search = findViewById(R.id.dest_search_box);
        String keywords = search.getText().toString();
        FragmentManager fm = this.getSupportFragmentManager();
        NavigationResultsFragment f = NavigationResultsFragment.newInstance(keywords);
        FragmentTransaction t = fm.beginTransaction();
        t.replace(R.id.dest_results_content, f);

    }

    // we have selected a room, start navigation activity.
    public void onRoomSelected(int roomId){
        Bundle b = new Bundle();
        b.putInt("roomId", roomId);
        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtras(b);
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
        status.setNavigationSearchActivity(null);
        if(status.getCurrentActivity() == this)
            status.setCurrentActivity(null);
    }
}
