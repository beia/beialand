package com.beia.solomon.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beia.solomon.R;
import com.beia.solomon.adapters.CampaignsAdapter;
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.Campaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreAdvertisementFragment extends Fragment {

    public View view;
    public GridView campaignsGridView;
    public CampaignsAdapter campaignsAdapter;
    public ArrayList<Campaign> campaigns;

    public StoreAdvertisementFragment(ArrayList<Campaign> campaigns) {
        this.campaigns = campaigns;
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
        return view;
    }

    public void initUI(View view)
    {
        campaignsGridView = view.findViewById(R.id.campaignsGridView);
        campaignsAdapter = new CampaignsAdapter(view.getContext(), campaigns);
        campaignsGridView.setAdapter(campaignsAdapter);
    }

}
