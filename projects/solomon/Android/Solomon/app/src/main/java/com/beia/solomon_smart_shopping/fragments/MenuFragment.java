package com.beia.solomon_smart_shopping.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon_smart_shopping.GsonRequest;
import com.beia.solomon_smart_shopping.R;
import com.beia.solomon_smart_shopping.activities.AskUsActivity;
import com.beia.solomon_smart_shopping.activities.LoginActivity;
import com.beia.solomon_smart_shopping.activities.MainActivity;
import com.beia.solomon_smart_shopping.activities.ProfileSettingsActivity;
import com.beia.solomon_smart_shopping.activities.StatsActivity;
import com.beia.solomon_smart_shopping.model.Mall;
import com.beia.solomon_smart_shopping.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.beia.solomon_smart_shopping.activities.ProfileSettingsActivity.decodeBase64;

public class MenuFragment extends Fragment {

    private View view;
    private CardView profileSettingsButton, mallStatsButton, askUsButton, preferencesButton, logoutButton;
    private CircularImageView profilePicture;
    private TextView nameTextView;

    private RequestQueue volleyQueue;
    private User user;
    private String password;
    private List<Mall> malls;
    private Gson gson;

    public MenuFragment() {}

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_fragment, container, false);
        volleyQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
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

    @Override
    public void onResume() {
        super.onResume();
        user = MainActivity.user;
        password = MainActivity.password;
        fetchAndSetProfilePicture();
    }

    private void initUI(View view) {
        profileSettingsButton = view.findViewById(R.id.profileSettingsCardView);
        mallStatsButton= view.findViewById(R.id.statsCardView);
        askUsButton = view.findViewById(R.id.askUsAgentCardView);
        preferencesButton = view.findViewById(R.id.preferencesCardView);
        logoutButton = view.findViewById(R.id.logoutButton);
        profilePicture = view.findViewById(R.id.profilePicture);
        nameTextView = view.findViewById(R.id.usernameTextView);
        String nameText = user.getFirstName() + " " + user.getLastName();
        nameTextView.setText(nameText);
        fetchAndSetProfilePicture();
        setupOnClickListeners();
    }

    private void setupOnClickListeners() {
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
    }

    private void fetchAndSetProfilePicture() {
        if (user.getImage() != null) {
            Bitmap imageBitmap = decodeBase64(user.getImage());
            profilePicture.setImageBitmap(imageBitmap);
        } else {
            getAndSetProfilePicture(user.getId());
        }
    }

    private void saveUserDataIntoCache() {
        SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
        editor.putString("user", new Gson().toJson(user));
        editor.putString("password", new Gson().toJson(password));
        editor.apply();
    }

    private void getAndSetProfilePicture(long userId) {
        String url = getResources().getString(R.string.get_profile_picture_url) + "/"
                + userId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<String> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                String.class,
                headers,
                response -> {
                    if(response != null) {
                        Log.d("RESPONSE", response);
                        profilePicture.setImageBitmap(decodeBase64(response));
                        user.setImage(response);
                        saveUserDataIntoCache();
                    }
                },
                error -> {
                    if(error.networkResponse.data != null)
                        Log.d("ERROR", "Update profile picture");
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }
}
