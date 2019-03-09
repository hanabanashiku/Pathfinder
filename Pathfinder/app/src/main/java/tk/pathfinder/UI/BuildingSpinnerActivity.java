package tk.pathfinder.UI;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tk.pathfinder.Map.Api;
import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Room;
import tk.pathfinder.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BuildingSpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private HashMap<String, Integer> map_ids;
    private Map map = null;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_home);

        Spinner spinner = findViewById(R.id.mapSpinner);
        spinner.setOnItemSelectedListener(this);

        List<String> cats = new ArrayList<>();
        map_ids = new HashMap<>();
        JSONObject maps;

        maps = new GetMaps().doInBackground(this);
        if(maps == null)
            return;
        try {
            JSONArray arr = maps.getJSONArray("buildings");
            for (int i = 0; i < maps.getInt("total"); i++) {
                JSONObject obj = arr.getJSONObject(i);
                map_ids.put(obj.getString("name"), obj.getInt("id"));
                cats.add(obj.getString("name"));
            }
        }
        catch(JSONException e){
                new Alert("Error", "Invalid data received: \n" + e.getMessage(), this).show();
            }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cats);
        spinner.setAdapter(adapter);

    }

    /**
     * @return The map image from the API request.
     */
    public Map getMap() { return map; }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(map_ids == null)
            return;

        String name = parent.getItemAtPosition(position).toString();
        int index = map_ids.get(name);

        try{
            Map m = Api.GetMap(index);
            Spinner destSpin = findViewById(R.id.destSpinner);
            List<String> cats = new ArrayList<>();
            for(Iterator<Room> i = m.getRooms(); i.hasNext(); ){
                Room r = i.next();
                cats.add(r.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cats);
            destSpin.setAdapter(adapter);
        }
        catch(IOException e){
            new Alert("Error", "Could not find map: \n" + e.getMessage(), this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class GetMaps extends AsyncTask<Context, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(Context... c) {
            try{
                return Api.GetMaps();
            }
            catch (IOException e){
                new Alert("Error", "Could not access server \n" + e.getMessage(), c[0]).show();
            }
            return null;
        }
    }
}
