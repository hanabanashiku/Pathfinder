package tk.pathfinder.UI.Activities;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.view.View;

import java.io.IOException;

import tk.pathfinder.Map.Map;
import tk.pathfinder.Networking.Api;
import tk.pathfinder.R;
import tk.pathfinder.UI.Alert;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.UI.MapView;

public class ViewMapActivity extends AppCompatActivity {
    int mapId;
    MapView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapId = getIntent().getIntExtra("mapId", 73);
        view = findViewById(R.id.map_view_window);
        new MapDetailsTask().execute(mapId, getApplicationContext(), this);
    }

    public void onCenterClick(View v){
        view.resetPosition();
    }

    private static class MapDetailsTask extends AsyncTask<Object, String, Map> {
        private AppStatus ctx;
        private ViewMapActivity f;

        @Override
        protected Map doInBackground(Object... args) {
            ctx = (AppStatus)args[1];
            f = (ViewMapActivity) args[2];

            Map m = null;
            try{
                m = Api.getMap((Integer)args[0]);
            }
            catch(IOException e){
                Looper.prepare();
                new Alert("Error", e.getMessage(), ctx.getCurrentActivity());
                e.printStackTrace();
            }

            //d.hide();
            return m;
        }

        @Override
        protected void onPostExecute(Map result){
            f.getSupportActionBar().setTitle(result.getName());
            f.view.setMap(result);
        }
    }
}
