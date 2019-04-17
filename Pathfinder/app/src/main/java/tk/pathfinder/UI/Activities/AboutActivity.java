package tk.pathfinder.UI.Activities;

import tk.pathfinder.BuildConfig;
import tk.pathfinder.R;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About Pathfinder");

        TextView version = findViewById(R.id.version_text);
        version.setText(String.format("Version %s", BuildConfig.VERSION_NAME));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == 16908332){
            finish();
            return true;
        }
        else if(item.getItemId() == R.id.app_about_menu_item)
            return true;
        return super.onOptionsItemSelected(item);
    }
}
