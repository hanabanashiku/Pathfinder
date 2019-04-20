package tk.pathfinder.UI.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import tk.pathfinder.R;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.UI.NavigationView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapLandingFragment extends Fragment {


    public MapLandingFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_landing, container, false);

        EditText text = v.findViewById(R.id.dest_search_box);
        ImageButton submit = v.findViewById(R.id.dest_search_submit);

        text.setOnEditorActionListener((textView, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                submit.performClick();
                return true;
            }
            return false;
        });

        NavigationView nav = v.findViewById(R.id.navigationView);
        nav.setMap(((AppStatus)getActivity().getApplicationContext()).getCurrentMap());

        return v;
    }

}
