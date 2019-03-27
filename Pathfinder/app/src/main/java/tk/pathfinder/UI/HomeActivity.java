package tk.pathfinder.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class HomeActivity extends AppCompatActivity
        implements MapFragment.MapFragmentInteractionListener {

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppStatus status = (AppStatus)getApplicationContext();
        status.setHomeActivity(this);
        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();

        MapReceiver mr = new MapReceiver();
        status.setMapReceiver(mr);
        // make sure it's called at least once
        mr.onReceive(this, new Intent());
    }

    private void switchFragment(Fragment fragment){
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.home_content, fragment);
        trans.commit();
    }

    public void setNoMap(){
        switchFragment(new NoMapFragment());
    }


    @Override
    public void onDestinationSearch(String keywords) {
        Bundle b = new Bundle();
        b.putString("keywords", keywords);
        Intent intent = new Intent(HomeActivity.this, NavigationSearchActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        AppStatus status = (AppStatus)getApplicationContext();
        status.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        AppStatus status = (AppStatus)getApplicationContext();
        status.setHomeActivity(null);
        if(status.getCurrentActivity() == this)
            status.setCurrentActivity(null);
    }

    public void onSearch(String keywords) {
        Bundle b = new Bundle();
        b.putString("keywords", keywords);
        Intent i = new Intent(this, MapSearchActivity.class);
        i.putExtras(b);
        startActivity(i);
    }


    public void onClick(View view) {
        EditText text = findViewById(R.id.noMap_search);
        onSearch(text.getText().toString());
    }
}
