package tk.pathfinder.UI.Activities;

import androidx.appcompat.app.AppCompatActivity;
import tk.pathfinder.R;
import tk.pathfinder.UI.AppStatus;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public abstract class MenuActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.app_about_menu_item:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.map_search_menu_item:
                i = new Intent(this, MapSearchActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
