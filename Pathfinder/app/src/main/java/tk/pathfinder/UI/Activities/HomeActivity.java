package tk.pathfinder.UI.Activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.UI.Fragments.MapLandingFragment;
import tk.pathfinder.UI.Fragments.NoMapFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class HomeActivity extends MenuActivity{

    private FragmentManager fragmentManager;
    private boolean visible;
    private View loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppStatus status = (AppStatus)getApplicationContext();
        status.setHomeActivity(this);
        setContentView(R.layout.activity_home);
        loadingBar = findViewById(R.id.progressBar);

        fragmentManager = getSupportFragmentManager();

        visible = true;
        status.getMapReceiver().onReceive(this, new Intent());
    }

    private void switchFragment(Fragment fragment){
        if(!visible)
            return;
        loadingBar.setVisibility(View.GONE);
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.home_content, fragment);
        trans.commit();
    }

    public void setNoMap(){
        switchFragment(new NoMapFragment());
    }

    public void setFoundMap(){
        switchFragment(new MapLandingFragment());
    }


   /* @Override
    public void onDestinationSearch(String keywords) {
        Bundle b = new Bundle();
        b.putString("keywords", keywords);
        Intent intent = new Intent(HomeActivity.this, NavigationSearchActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }*/

    @Override
    protected void onResume(){
        super.onResume();
        visible = true;
        ((AppStatus)getApplicationContext()).getBeaconReceiver().onReceive(this, new Intent());
    }

    @Override
    protected void onPause(){
        super.onPause();
        visible = false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        AppStatus status = (AppStatus)getApplicationContext();
        status.setHomeActivity(null);
    }

    public void onMapSearch(String keywords) {
        Bundle b = new Bundle();
        b.putString("keywords", keywords);
        Intent i = new Intent(this, MapSearchActivity.class);
        i.putExtras(b);
        startActivity(i);
    }

    public void onDestinationSearch(String keywords){
        Bundle b = new Bundle();
        b.putString("keywords", keywords);
        Intent i = new Intent(this, NavigationSearchActivity.class);
        i.putExtras(b);
        startActivity(i);
    }


    public void onMapSubmit(View view) {
        EditText text = findViewById(R.id.noMap_search);
        String keywords = text.getText().toString();
        if(!keywords.isEmpty())
            onMapSearch(text.getText().toString());
    }

    public void onDestinationSubmit(View view) {
        EditText text = findViewById(R.id.dest_search_box);
        String keywords = text.getText().toString();
        if(!keywords.isEmpty())
            onDestinationSearch(text.getText().toString());
    }
}
