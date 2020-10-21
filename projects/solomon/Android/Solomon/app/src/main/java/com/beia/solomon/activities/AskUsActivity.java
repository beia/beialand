package com.beia.solomon.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon.GsonRequest;
import com.beia.solomon.R;
import com.beia.solomon.model.User;

import java.util.HashMap;
import java.util.Map;

public class AskUsActivity extends AppCompatActivity {

    private User user;
    private RecyclerView recyclerView;
    private LinearLayout animationLayout;
    private ImageView loadingImage;
    private ConstraintLayout messageInputLayout;
    private EditText messageEditText;
    private Button messageButton;

    RequestQueue volleyQueue;
    AnimationDrawable loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_us);
        user = (User) getIntent().getSerializableExtra("user");
        volleyQueue = Volley.newRequestQueue(this);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        createLoadingAnimation();
        if(user != null)
            requestAgentChat(user.getId());
    }

    public void initUI() {
        recyclerView = findViewById(R.id.recyclerView);
        animationLayout = findViewById(R.id.animationLayout);
        loadingImage = findViewById(R.id.loadingAnimationImage);
        messageInputLayout = findViewById(R.id.messageInputView);
        messageEditText = findViewById(R.id.messageEditText);
        messageButton = findViewById(R.id.messageButton);
    }

    public void createLoadingAnimation() {
        loadingImage.setBackgroundResource(R.drawable.loading_animation);
        loadingAnimation = (AnimationDrawable) loadingImage.getBackground();
        loadingAnimation.start();
    }


    public void requestAgentChat(long userId) {
        String url = getResources().getString(R.string.findChatAgentUrl)
                + "?userId=" + userId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Object> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                null,
                Object.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "AGENT CHAT REQUEST SENT");
                },
                error -> {
                    if(error.networkResponse != null) {
                        Log.d("ERROR", new String(error.networkResponse.data));
                    }
                    else {
                        error.printStackTrace();
                    }
                });
        volleyQueue.add(request);
    }
}

