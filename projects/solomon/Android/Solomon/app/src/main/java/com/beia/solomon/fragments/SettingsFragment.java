package com.beia.solomon.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beia.solomon.R;
import com.beia.solomon.activities.LoginActivity;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.activities.ProfileSettingsActivity;
import com.beia.solomon.activities.StatsActivity;
import com.beia.solomon.runnables.SendAuthenticationDataRunnable;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.beia.solomon.activities.ProfileSettingsActivity.decodeBase64;

public class SettingsFragment extends Fragment {

    public View view;
    public CardView profileSettingsCardView, preferencesCardView, notificationsCardView, statsCardView, logoutButton;
    public CircularImageView profilePicture;
    public TextView nameTextView;

    public SettingsFragment() {

    }

    public void setArguments(@Nullable Bundle args, String bundleDataName) {
        super.setArguments(args);
        ArrayList<String> bundleData = args.getStringArrayList(bundleDataName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, container, false);
        initUI(view);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the user automatic login data from the cache
                SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
                editor.putString("username", null);
                editor.putString("password", null);
                editor.apply();
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

        statsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StatsActivity.class);
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
        logoutButton = view.findViewById(R.id.logoutButton);

        profilePicture = view.findViewById(R.id.profilePicture);
        nameTextView = view.findViewById(R.id.usernameTextView);

        nameTextView.setText(MainActivity.userData.getFirstName() + " " + MainActivity.userData.getLastName());

        //if we can't find the profile picture in the cache we check in the users prefs or we get it from the server
        if(MainActivity.picturesCache == null || MainActivity.picturesCache.get("profilePicture") == null)
        {
            Log.d("PROFILE PICTURE", "initUI: ");
            //we check for the profile picture in the memory and if we find it we check if the same user is logged in as the one who's picture is saved
            //if that's the case we get it from the memory(the picture is saved as a String and then it's concatenated with the user id with a space in between)
            //if it's not the case then we download it from the server
            if (MainActivity.sharedPref.contains("profilePicture"))
            {
                String userImageString = "";
                //get the image string from the disk
                userImageString = MainActivity.sharedPref.getString("profilePicture", "noImage");
                if(!userImageString.equals("noImage"))
                {
                    String[] data = userImageString.split(" ");
                    String imageString = data[0];
                    int userId = Integer.parseInt(data[1]);
                    if(userId == MainActivity.userData.getUserId()) {
                        Bitmap imageBitmap = decodeBase64(imageString);
                        profilePicture.setImageBitmap(imageBitmap);
                        //save the picture into cache memory(in ram)
                        MainActivity.picturesCache.put("profilePicture", imageBitmap);
                        return;
                    }
                }
            }

            //get the image from the server
            Thread requestProfilePictureThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (MainActivity.objectOutputStream) {
                            //send the photo request
                            MainActivity.objectOutputStream.writeObject("{\"requestType\":\"profilePicture\"}");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            requestProfilePictureThread.start();
        }
        else
        {
            //get the picture from cache
            Bitmap bitmap = MainActivity.picturesCache.get("profilePicture");
            profilePicture.setImageBitmap(bitmap);
        }
    }
}
