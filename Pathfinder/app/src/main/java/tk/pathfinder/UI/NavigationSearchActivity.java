package tk.pathfinder.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import tk.pathfinder.R;

/**
 * An activity for searching for destinations on a map.
 */
public class NavigationSearchActivity extends AppCompatActivity implements NavigationResult.NavigationResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    }

    // we have selected a room, start navigation activity.
    public void onRoomSelected(int roomId){
        Bundle b = new Bundle();
        b.putInt("roomId", roomId);
        Intent i = new Intent(this, NavigationActivity.class);
        i.putExtras(b);
        startActivity(i);
    }
}
