package tk.pathfinder.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import tk.pathfinder.R;

public class Tab2Fragment extends Fragment {
    private static final String Tag = "Tab2Fragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab2_fragment, container, false);
    }
}