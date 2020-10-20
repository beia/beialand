package com.beia.solomon.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beia.solomon.R;
import com.beia.solomon.model.User;
import com.google.gson.Gson;

public class AskUsActivity extends AppCompatActivity {

    private User user;
    private RecyclerView recyclerView;
    private LinearLayout animationLayout;
    private ImageView loadingImage;
    private ConstraintLayout messageInputLayout;
    private EditText messageEditText;
    private Button messageButton;

    AnimationDrawable loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_us);
        user = (User) getIntent().getSerializableExtra("user");
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        createLoadingAnimation();
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
}
