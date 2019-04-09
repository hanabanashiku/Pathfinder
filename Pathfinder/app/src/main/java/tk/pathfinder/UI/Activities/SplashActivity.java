package tk.pathfinder.UI.Activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(PermissionChecker.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 69);
        if(PermissionChecker.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 69);
        if(PermissionChecker.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) != PermissionChecker.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 69);
        if(PermissionChecker.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CHANGE_WIFI_STATE) != PermissionChecker.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 69);


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

