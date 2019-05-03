package com.example.solomon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    public View view;
    public CardView profileSettingsCardView, preferencesCardView, notificationsCardView, statsCardView;

    public SettingsFragment() {

    }

    public void setArguments(@Nullable Bundle args, String bundleDataName) {
        super.setArguments(args);
        ArrayList<String> bundleData = args.getStringArrayList(bundleDataName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, container, false);
        initUI(view);

        profileSettingsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileSettingsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    public void initUI(View view)
    {
        profileSettingsCardView = view.findViewById(R.id.profileSettingsCardView);
        preferencesCardView = view.findViewById(R.id.preferencesCardView);
        notificationsCardView = view.findViewById(R.id.notificationsCardView);
        statsCardView = view.findViewById(R.id.statsCardView);
    }
}
