package tk.pathfinder.UI;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.pathfinder.R;

/**
 * A fragment for listing the results of a map search.
 */
public class MapResultsFragment extends Fragment implements MapResult.MapResultListener {

    private String keywords;

    private MapResultsListener mListener;

    public MapResultsFragment() {
        // Required empty public constructor
    }

    public static MapResultsFragment newInstance(String keywords) {
        MapResultsFragment fragment = new MapResultsFragment();
        Bundle args = new Bundle();
        args.putString("keywords", keywords);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keywords = getArguments().getString("keywords");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_results, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapResultsListener) {
            mListener = (MapResultsListener)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MapResultsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapSelected(int id) {
        mListener.onMapSelected(id);
    }


    public interface MapResultsListener {
        void onMapSelected(int mapId);
    }
}
