package tk.pathfinder.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.Networking.Api;
import tk.pathfinder.Networking.AppStatus;
import tk.pathfinder.R;

/**
 * A fragment for listing the results of a map search.
 */
public class MapResultsFragment extends Fragment {

    private String keywords;

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
        new MapSearchTask().execute(keywords, getActivity().getApplicationContext(), getFragmentManager());
        return v;
    }

    // static to avoid memory leaks
    // takes (string)keywords, (AppStatus)context, (FragmentManager)mgr as arguments
    private static class MapSearchTask extends AsyncTask<Object, String, Api.MapQueryResult[]>{
        ProgressDialog d;
        AppStatus ctx;
        FragmentManager mgr;

        @Override
        protected Api.MapQueryResult[] doInBackground(Object... args){
            Api.MapQueryResult[] res = null;
            ctx = (AppStatus)args[1];
            mgr = (FragmentManager)args[2];
            Looper.prepare();

            d = new ProgressDialog(ctx.getCurrentActivity());
            d.setMessage("Searching...");
            d.setIndeterminate(false);
            d.setCancelable(false);
            d.show();

            try{
                res = Api.findMaps((String)args[0]);
            }
            catch(IOException e){
                d.hide();
                new Alert("Error", e.getMessage(), ctx).show();
            }
            d.hide();
            return res;
        }

        @Override
        protected void onPostExecute(Api.MapQueryResult[] result){
            super.onPostExecute(result);
            d = null;
            if(result == null)
                return;

            FragmentTransaction t = mgr.beginTransaction();
            for(Api.MapQueryResult r : result){
                MapResult f = MapResult.newInstance(r);
                t.add(R.id.map_search_results_content, f);
            }
            t.commit();
        }
    }
}
