package tk.pathfinder.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.R;

import android.content.Intent;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity
        implements MapFragment.MapFragmentInteractionListener {

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();
        switchFragment(new NoMapFragment());
    }

    private void switchFragment(Fragment fragment){
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.replace(R.id.home_content, fragment);
        trans.commit();
    }


    @Override
    public void onDestinationSearch(String keywords) {
        Bundle b = new Bundle();
        b.putString("keywords", keywords);
        Intent intent = new Intent(HomeActivity.this, NavigationSearchActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }
}
