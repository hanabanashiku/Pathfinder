package tk.pathfinder.UI.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import tk.pathfinder.R;


public class NoMapFragment extends Fragment {


    public NoMapFragment() {}


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_no_map, container, false);
        EditText text = v.findViewById(R.id.noMap_search);
        Button submit = v.findViewById(R.id.search_submit);

        text.setOnEditorActionListener((textView, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                submit.performClick();
                return true;
            }
            return false;
        });

        return v;
    }
}
