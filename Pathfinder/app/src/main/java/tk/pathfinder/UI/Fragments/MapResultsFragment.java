package tk.pathfinder.UI.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.Networking.Api;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.UI.Alert;

/**
 * A fragment for listing the results of a map search.
 */
public class MapResultsFragment extends Fragment {

    private String keywords;
    private ProgressDialog d;

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
        if (getArguments() != null)
            keywords = getArguments().getString("keywords");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_results, container, false);
        d = new ProgressDialog(getActivity());
        d.setMessage("Searching...");
        d.setIndeterminate(false);
        d.setCancelable(false);
        d.show();
        new MapSearchTask().execute(keywords, getActivity().getApplicationContext(), this);
        return v;
    }

    private void addResults(Api.MapQueryResult[] result){
        FragmentTransaction t = getFragmentManager().beginTransaction();
        for(Api.MapQueryResult r : result){
            MapResult f = MapResult.newInstance(r);
            t.add(R.id.map_search_results_content, f);
        }
        t.commit();
    }

    private void noResults(){
        FragmentTransaction t = getFragmentManager().beginTransaction();
        t.add(R.id.map_search_results_content, new NoResultsFragment());
        t.commit();
    }

    // static to avoid memory leaks
    // takes (string)keywords, (AppStatus)context, (FragmentManager)mgr as arguments
    private static class MapSearchTask extends AsyncTask<Object, String, Api.MapQueryResult[]>{
        AppStatus ctx;
        MapResultsFragment f;

        @Override
        protected Api.MapQueryResult[] doInBackground(Object... args){
            Api.MapQueryResult[] res = null;
            ctx = (AppStatus)args[1];
            f = (MapResultsFragment)args[2];

            try{
                res = Api.findMaps((String)args[0]);
            }
            catch(IOException e){
                //f.d.dismiss();
                //new Alert("Error", e.getMessage(), ctx.getCurrentActivity()).show();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Api.MapQueryResult[] result){
            super.onPostExecute(result);
            if(result == null)
                return;
            if(result.length == 0)
                f.noResults();
            else
                f.addResults(result);
            f.d.dismiss();
        }
    }
}
