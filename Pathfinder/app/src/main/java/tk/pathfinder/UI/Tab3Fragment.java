package tk.pathfinder.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import tk.pathfinder.R;

public class Tab3Fragment extends Fragment {
    private static final String Tag = "Tab3Fragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab3_fragment, container, false);
    }
}