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

        // register our receiver
        BeaconReceiver br = new BeaconReceiver(this.getApplicationContext());

        // keep the radios awake
        br.getWifiLock().acquire();
        // set and register our receiver
        ((AppStatus)getApplicationContext()).setBeaconReceiver(br);

        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        },SPLASH_TIME_OUT);
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
}

