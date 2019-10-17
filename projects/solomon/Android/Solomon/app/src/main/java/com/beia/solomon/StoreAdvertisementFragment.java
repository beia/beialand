package com.beia.solomon;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beia.solomon.networkPackets.Beacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreAdvertisementFragment extends Fragment {

    public View view;
    public LinearLayout linearLayout;

    public StoreAdvertisementFragment() {

    }

    public void setArguments(@Nullable Bundle args, String bundleDataName) {
        super.setArguments(args);
        ArrayList<String> bundleData = args.getStringArrayList(bundleDataName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.store_advertisement_fragment, container, false);
        initUI(view);
        if(MainActivity.beacons != null)
        {
            Log.d("BEACONS", "INITIALIZED BEACON TEXTVIEWS");
            MainActivity.beaconsTextViews = new HashMap<>();
            for (Map.Entry entry : MainActivity.beacons.entrySet())
            {
                Beacon beacon = (Beacon) entry.getValue();
                TextView textView = new TextView(MainActivity.context);
                textView.setText(beacon.getLabel() + ": ");
                linearLayout.addView(textView);
                MainActivity.beaconsTextViews.put(beacon.getId(), textView);
            }
        }
        return view;
    }

    public void initUI(View view)
    {
        linearLayout = view.findViewById(R.id.ScrollViewLinearLayout);
    }

}
