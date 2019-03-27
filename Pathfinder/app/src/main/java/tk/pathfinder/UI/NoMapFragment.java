package tk.pathfinder.UI;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import tk.pathfinder.R;


public class NoMapFragment extends Fragment {

    private NoMapListener mListener;

    public NoMapFragment() {}


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_map, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NoMapListener) {
            mListener = (NoMapListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NoMapListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onClick(View v){
        EditText text = v.findViewById(R.id.search_submit);
        mListener.onSearch(text.getText().toString());
    }


    public interface NoMapListener {
        void onSearch(String keywords);
    }
}
