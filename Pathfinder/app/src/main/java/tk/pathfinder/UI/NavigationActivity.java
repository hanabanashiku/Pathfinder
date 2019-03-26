package tk.pathfinder.UI;

import android.content.Intent;
import android.os.Bundle;


import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import tk.pathfinder.Map.Room;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.R;

public class NavigationActivity extends AppCompatActivity {

    private Room destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();

        int roomId = i.getIntExtra("roomId", -1);
        // an argument was provided
        if(roomId != -1){
            for(Iterator<Room> it = AppStatus.getCurrentMap().getRooms(); it.hasNext(); ){
                Room room = it.next();
                if(room.getId() == roomId){
                    destination = room;
                    try{

                    }
                    break;
                }

            }
        }
    }

}
