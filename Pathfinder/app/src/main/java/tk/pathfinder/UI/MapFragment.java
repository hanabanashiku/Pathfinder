package tk.pathfinder.UI;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;


import tk.pathfinder.Map.Map;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.R;

public class MapFragment extends Fragment {

    private MapFragmentInteractionListener mListener;

    private MapView mapView;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView = new MapView(getContext());
        mapView.setMap(AppStatus.getCurrentMap());
        mapView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        // add the map
        LinearLayout l = v.findViewById(R.id.map_content);
        l.addView(mapView);

        // press submit button automatically when hitting done
        EditText searchBox = v.findViewById(R.id.map_search);
        Button searchButton = v.findViewById(R.id.map_submit);
        searchBox.setOnEditorActionListener((v1, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                searchButton.performClick();
                return true;
            }
            return false;
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapFragmentInteractionListener) {
            mListener = (MapFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Activity callbacks, implemented in HomeActivity.
     */
    public interface MapFragmentInteractionListener {
        // called when the user is searching for a destination
        void onDestinationSearch(String keywords);
    }

    public void setMap(Map map){
        mapView.setMap(map);
    }

    public void onSearchSubmit(View v){
        EditText searchBox = getView().findViewById(R.id.map_search);
        mListener.onDestinationSearch(searchBox.getText().toString());
    }
}
