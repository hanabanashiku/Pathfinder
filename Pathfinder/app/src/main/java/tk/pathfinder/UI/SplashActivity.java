package tk.pathfinder.UI;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.Networking.BeaconReceiver;
import tk.pathfinder.R;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // register our beacon receiver
        BeaconReceiver br = new BeaconReceiver(this.getApplicationContext());
        IntentFilter bf = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(br, bf);

        // keep the radios awake
        br.getWifiLock().acquire();
        AppStatus.setAppContext(getApplicationContext());
        AppStatus.setBeaconReceiver(br);

        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        },SPLASH_TIME_OUT);
    }
}

