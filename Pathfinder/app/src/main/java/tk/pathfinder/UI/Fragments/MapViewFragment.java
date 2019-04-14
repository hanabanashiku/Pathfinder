package tk.pathfinder.UI.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import tk.pathfinder.Networking.Api;
import tk.pathfinder.Map.Map;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.UI.Alert;


public class MapViewFragment extends Fragment {
    private Map map;
    private MapControllerFragment mapController;
    private int mapId;
    private ProgressDialog d;

    private SectionsPagerAdapter mPageAdapter;
    private ViewPager mViewPager;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static MapViewFragment newInstance(int mapId) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        args.putInt("mapId", mapId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mapId = getArguments().getInt("mapId");
        }

        AppStatus status = (AppStatus)getContext().getApplicationContext();
        if(status == null || status.getCurrentBuildingId() != mapId){
            d = new ProgressDialog(getActivity());
            d.setMessage("Getting your map...");
            d.show();
            new MapDetailsTask().execute(mapId, getActivity().getApplicationContext(), this);
        }
        else{
            map = status.getCurrentMap();
        }

        mapController = new MapControllerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);

        if(map != null)
            setMapView();

        mPageAdapter = new SectionsPagerAdapter(this.getFragmentManager());
        mViewPager = v.findViewById(R.id.map_container);
        mViewPager.setAdapter(mPageAdapter);
        return v;
    }

    private void setMapView(){
        /*int[] floors = map.getFloorRange();
        List<MapControllerFragment> frags = new ArrayList<>();
        for(int i = floors[0]; i <= floors[1]; i++) {
            MapControllerFragment frag = new MapControllerFragment();
            frag.setMapView(map, i);
            frags.add(frag);
        }
        mPageAdapter.fragments = frags;
        mPageAdapter.notifyDataSetChanged();*/
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int smallestFloor = -1;
        int count = -1;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            MapControllerFragment fragment = new MapControllerFragment();
            fragment.setMapView(map, smallestFloor + position);
            return fragment;
        }

        @Override
        public int getCount() {
            if(map == null)
                return 0;

            if(count == -1){
                int[] floors = map.getFloorRange();
                smallestFloor = floors[0];
                if(floors[0] <= 0)
                    count = floors[1] + -1*floors[1];
                else count = floors[1] - floors[0];
                count += 1;
            }
            return count;
        }
    }

    private static class MapDetailsTask extends AsyncTask<Object, String, Map> {
        private AppStatus ctx;
        private MapViewFragment f;

        @Override
        protected Map doInBackground(Object... args) {
            ctx = (AppStatus)args[1];
            f = (MapViewFragment)args[2];

            Map m = null;
            try{
                m = Api.getMap((Integer)args[0]);
            }
            catch(IOException e){
                Looper.prepare();
                new Alert("Error", e.getMessage(), ctx.getCurrentActivity());
                e.printStackTrace();
            }

            //d.hide();
            return m;
        }

        @Override
        protected void onPostExecute(Map result){
            f.map = result;
            f.setMapView();
           f.d.dismiss();
        }
    }
}
