package com.beia.solomon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beia.solomon.R;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.adapters.CampaignsAdapter;
import com.beia.solomon.model.Campaign;
import com.beia.solomon.model.User;

import java.util.List;

public class StoreAdvertisementFragment extends Fragment {

    private View view;
    private GridView campaignsGridView;
    private CampaignsAdapter campaignsAdapter;
    private List<Campaign> campaigns;
    private User user;

    public StoreAdvertisementFragment(List<Campaign> campaigns, User user) {
        this.campaigns = campaigns;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.store_advertisement_fragment, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        campaignsGridView = view.findViewById(R.id.campaignsGridView);
        campaignsGridView.setColumnWidth((int)(MainActivity.displayWidth / 2.1f));
        campaignsAdapter = new CampaignsAdapter(view.getContext(), campaigns, user);
        campaignsGridView.setAdapter(campaignsAdapter);
    }

    public void refreshCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
        campaignsAdapter = new CampaignsAdapter(view.getContext(), campaigns, user);
        campaignsGridView.setAdapter(campaignsAdapter);
    }
}
