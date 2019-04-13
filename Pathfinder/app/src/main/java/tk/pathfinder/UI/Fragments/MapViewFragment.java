package tk.pathfinder.UI.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Iterator;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import tk.pathfinder.Map.Room;
import tk.pathfinder.Networking.Api;
import tk.pathfinder.Map.Map;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;
import tk.pathfinder.UI.Alert;
import tk.pathfinder.UI.MapView;


public class MapViewFragment extends Fragment {
    private MapView view;
    private Map map;
    private MapFragment mapController;
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

        mapController = new MapFragment();
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
        view = new MapView(this.getContext());
        view.setMap(map);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int count = -1;
        private int smallestFloor;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int floor = smallestFloor + position;
            view.setCurrentFloor(floor);
            return mapController;
        }

        @Override
        public int getCount() {
            if(map == null)
                return 0;
            if(count == -1){ // define it if it's not defined and cache it
                int[] range = map.getFloorRange();
                for(Iterator<Room> i = map.getRooms(); i.hasNext(); )
                    System.out.println(i.next().getPoint().getY());
                smallestFloor = range[0];
                count = (Math.abs(range[0]) + Math.abs(range[1]));
                if(smallestFloor < 0) count += 1; // for floor 0
            }
            return count;
        }
    }

    @SuppressLint("ValidFragment")
    public class MapFragment extends Fragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_map_controller, container, false);
            FrameLayout layout = v.findViewById(R.id.map_view_display);
            layout.addView(view);
            return v;
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
                //d.hide();
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
