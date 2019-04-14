package tk.pathfinder.UI.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import tk.pathfinder.Map.Room;
import tk.pathfinder.UI.AppStatus;
import tk.pathfinder.R;


public class NavigationResult extends Fragment {
    // the fragment initialization parameters,
    private static final String ROOM_ID = "roomId";
    private static final String ROOM_NUMBER = "roomNumber";
    private static final String ROOM_NAME = "roomName";
    private static final String DISTANCE = "distance";
    private static final String AUTH = "requires_auth";

    private int roomId;
    private String roomNumber;
    private String roomName;
    private int distance;
    private boolean requires_auth;

    private NavigationResultListener mListener;

    public NavigationResult() {
        // Required empty public constructor
    }


    public static NavigationResult newInstance(Room node, AppStatus context) {
        NavigationResult fragment = new NavigationResult();
        Bundle args = new Bundle();
        args.putInt(ROOM_ID, node.getId());
        args.putString(ROOM_NUMBER, node.getRoomNumber());
        args.putString(ROOM_NAME, node.getName());
        int dist = context.getCurrentMap().getNodeDistance(context.getCurrentLocation(), node);
        args.putInt(DISTANCE, dist);
        args.putBoolean(AUTH, node.requiresAuthorization());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roomId = getArguments().getInt(ROOM_ID);
            roomNumber = getArguments().getString(ROOM_NUMBER);
            roomName = getArguments().getString(ROOM_NAME);
            distance = getArguments().getInt(DISTANCE);
            requires_auth = getArguments().getBoolean(AUTH);
        }
        else throw new NullPointerException();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigation_result, container, false);
        TextView num = v.findViewById(R.id.res_r_num);
        TextView name = v.findViewById(R.id.res_r_name);
        TextView dist = v.findViewById(R.id.res_r_dist);
        ImageView auth = v.findViewById(R.id.res_r_auth);

        // set attributes to display
        num.setText(roomNumber);
        name.setText(roomName);
        dist.setText(String.format(Locale.US, "%d ft.", distance));
        if(requires_auth)
            auth.setVisibility(View.VISIBLE);
        else auth.setVisibility(View.GONE);

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationResultListener) {
            mListener = (NavigationResultListener) context;
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

    public interface NavigationResultListener {
        void onRoomSelected(int roomId);
    }

    public void onClick(View v){
        mListener.onRoomSelected(roomId);
    }
}
