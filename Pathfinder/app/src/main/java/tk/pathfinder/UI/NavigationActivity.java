package tk.pathfinder.UI;

import android.content.Intent;
import android.os.Bundle;


import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import tk.pathfinder.Map.Edge;
import tk.pathfinder.Map.Map;
import tk.pathfinder.Map.Navigation;
import tk.pathfinder.Map.Node;
import tk.pathfinder.Map.Path;
import tk.pathfinder.Map.Room;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.exceptions.NoValidPathException;

public class NavigationActivity extends AppCompatActivity {

    private Room destination;
    private Path path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        int roomId = i.getIntExtra("roomId", -1);
        // an argument was provided
        if(roomId != -1){
            for(Iterator<Room> it = AppStatus.getCurrentMap().getRooms(); it.hasNext(); ){
                Room room = it.next();
                if(room.getId() == roomId){
                    destination = room;
                    recalculatePath();
                }
            }
        }
    }

    public boolean recalculatePath(){
        try{
            if(AppStatus.getCurrentMap() == null){
                path = null;
                return false;
            }
            Map map = AppStatus.getCurrentMap();
            Node current = map.closestNode(AppStatus.getCurrentLocation());
            path = Navigation.NavigatePath(map, current, destination);
            return true;
        }
        catch(NoValidPathException e){
            path = null;
            return false;
        }
    }

}
