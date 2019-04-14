package tk.pathfinder.UI.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import tk.pathfinder.Map.Map;
import tk.pathfinder.R;
import tk.pathfinder.UI.MapView;

public class MapControllerFragment extends Fragment {

    private Map map;
    private int floor;
    private MapView view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_controller, container, false);
        view = v.findViewById(R.id.map_view_widget);
        view.setMap(map);
        view.setCurrentFloor(floor);
        FrameLayout layout = v.findViewById(R.id.map_view_display);
        TextView text = new TextView(this.getContext());
        text.setText(String.format("Floor %d", floor));
        layout.addView(text);
        return v;
    }

    public void setMapView(Map map, int floor){
        this.map = map;
        this.floor = floor;
    }
}