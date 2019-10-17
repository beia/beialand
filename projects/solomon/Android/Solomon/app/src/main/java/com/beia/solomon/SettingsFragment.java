package com.beia.solomon;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beia.solomon.runnables.SendAuthenticationDataRunnable;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    public View view;
    public CardView profileSettingsCardView, preferencesCardView, notificationsCardView, statsCardView;
    public TextView logoutTextView;

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
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the user automatic login data from the cache
                LoginActivity.editor.putString("username", null);
                LoginActivity.editor.putString("password", null);
                LoginActivity.editor.commit();
                //send a message to the server to logout the user and wait for authentication data
                Thread logoutThread = new Thread(new SendAuthenticationDataRunnable("log out", MainActivity.objectOutputStream));
                logoutThread.start();
                //start the LoginActivity
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                //close the current activity
                MainActivity.mainActivity.finish();
            }
        });

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
        logoutTextView = view.findViewById(R.id.logoutTextView);
    }
}
