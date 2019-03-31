package tk.pathfinder.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import androidx.fragment.app.FragmentTransaction;
import tk.pathfinder.Networking.Api;
import tk.pathfinder.R;

/**
 * A fragment for listing the results of a map search.
 */
public class MapResultsFragment extends Fragment {

    private String keywords;

    private MapSearchActivity ctx;

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
        ctx = (MapSearchActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_results, container, false);
        new MapSearchTask().execute(keywords);
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

    }

    private class MapSearchTask extends AsyncTask<String, String, Api.MapQueryResult[]>{
        ProgressDialog d;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            d = new ProgressDialog(getActivity());
            d.setMessage("Searching...");
            d.setIndeterminate(false);
            d.setCancelable(false);
            d.show();
        }

        @Override
        protected Api.MapQueryResult[] doInBackground(String... args){
            Api.MapQueryResult[] res = null;
            try{
                res = Api.findMaps(args[0]);
            }
            catch(IOException e){
                d.hide();
                new Alert("Error", e.getMessage(), getActivity()).show();
            }
            return res;
        }

        @Override
        protected void onPostExecute(Api.MapQueryResult[] result){
            super.onPostExecute(result);
            d.hide();
            if(result == null)
                return;

            FragmentTransaction t = ctx.getSupportFragmentManager().beginTransaction();
            for(Api.MapQueryResult r : result){
                MapResult f = MapResult.newInstance(r);
                t.add(R.id.map_search_results_content, f);
            }
            t.commit();
        }
    }
}
