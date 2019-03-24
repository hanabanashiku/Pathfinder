package tk.pathfinder.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.Networking.BeaconReceiver;
import tk.pathfinder.R;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity
        implements MapFragment.MapFragmentInteractionListener {

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // register our beacon receiver
        BeaconReceiver br = new BeaconReceiver(this.getApplicationContext());
        IntentFilter bf = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(br, bf);

        // keep the radios awake
        br.getWifiLock().acquire();
        AppStatus.setAppContext(getApplicationContext());
        AppStatus.setBeaconReceiver(br);

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

    }
}
