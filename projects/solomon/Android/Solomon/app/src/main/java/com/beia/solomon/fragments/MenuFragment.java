package com.beia.solomon.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.beia.solomon.R;
import com.beia.solomon.activities.AskUsActivity;
import com.beia.solomon.activities.LoginActivity;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.activities.ProfileSettingsActivity;
import com.beia.solomon.activities.StatsActivity;
import com.beia.solomon.model.Mall;
import com.beia.solomon.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.internal.schedulers.NewThreadScheduler;

public class MenuFragment extends Fragment {

    public View view;
    public CardView profileSettingsButton, mallStatsButton, askUsButton, preferencesButton, logoutButton;
    public CircularImageView profilePicture;
    public TextView nameTextView;

    private User user;
    private String password;
    private List<Mall> malls;
    private Gson gson;

    public MenuFragment() {

    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, container, false);
        gson = new Gson();
        Bundle bundle = getArguments();
        if(bundle != null) {
            ArrayList<String> bundleData = bundle.getStringArrayList("settingsData");
            if(bundleData != null) {
                user = gson.fromJson(bundleData.get(0), User.class);
                password = bundleData.get(1);
                malls = gson.fromJson(bundleData.get(2), new TypeToken<List<Mall>>() {}.getType());
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI(view);
    }

    private void initUI(View view)
    {
        profileSettingsButton = view.findViewById(R.id.profileSettingsCardView);
        mallStatsButton= view.findViewById(R.id.statsCardView);
        askUsButton = view.findViewById(R.id.askUsAgentCardView);
        preferencesButton = view.findViewById(R.id.preferencesCardView);
        logoutButton = view.findViewById(R.id.logoutButton);
        profilePicture = view.findViewById(R.id.profilePicture);
        nameTextView = view.findViewById(R.id.usernameTextView);
        String nameText = user.getFirstName() + " " + user.getLastName();
        nameTextView.setText(nameText);

        profileSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileSettingsActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("password", password);
            startActivity(intent);
        });

        mallStatsButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), StatsActivity.class);
            intent.putExtra("malls", gson.toJson(malls));
            startActivity(intent);
        });

        askUsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AskUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
            editor.remove("user");
            editor.remove("password");
            editor.commit();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

//
//
//        //if we can't find the profile picture in the cache we check in the users prefs or we get it from the server
//        if(MainActivity.picturesCache == null || MainActivity.picturesCache.get("profilePicture") == null)
//        {
//            if (MainActivity.sharedPref.contains("profilePicture"))
//            {
//                String userImageString = "";
//                //get the image string from the disk
//                userImageString = MainActivity.sharedPref.getString("profilePicture", "noImage");
//                if(!userImageString.equals("noImage"))
//                {
//                    String[] data = userImageString.split(" ");
//                    String imageString = data[0];
//                    int userId = Integer.parseInt(data[1]);
//                    if(userId == MainActivity.userData.getUserId()) {
//                        Bitmap imageBitmap = decodeBase64(imageString);
//                        profilePicture.setImageBitmap(imageBitmap);
//                        //save the picture into cache memory(in ram)
//                        MainActivity.picturesCache.put("profilePicture", imageBitmap);
//                        return;
//                    }
//                }
//            }
//            //get the image from the server
//        }
//        else
//        {
//            //get the picture from cache
//            Bitmap bitmap = MainActivity.picturesCache.get("profilePicture");
//            profilePicture.setImageBitmap(bitmap);
//        }
    }
}
