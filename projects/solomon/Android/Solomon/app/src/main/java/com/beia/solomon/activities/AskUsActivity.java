package com.beia.solomon.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon.GsonRequest;
import com.beia.solomon.R;
import com.beia.solomon.adapters.ConversationRecyclerViewAdapter;
import com.beia.solomon.model.Conversation;
import com.beia.solomon.model.Message;
import com.beia.solomon.model.Role;
import com.beia.solomon.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskUsActivity extends AppCompatActivity {

    private User user;
    private Conversation conversation;

    private RecyclerView recyclerView;
    private ConversationRecyclerViewAdapter mAdapter;
    private LinearLayout animationLayout;
    private ImageView loadingImage;
    private LinearLayout messageInputLayout;
    private EditText messageEditText;
    private Button messageButton;

    private SharedPreferences sharedPref;
    private Gson gson;

    RequestQueue volleyQueue;
    AnimationDrawable loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_us);

        gson = new Gson();
        sharedPref = getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        volleyQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        fetchUser(intent);
        fetchConversation();
        handleIntentType(intent);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        createLoadingAnimation();
        if(conversation == null) {
            if(!user.getRole().equals(Role.AGENT.name()))
                requestAgentChat(user.getId());
        } else {
            loadConversationUI();
            //TODO::request conversation thread
        }
    }

    public void initUI() {
        recyclerView = findViewById(R.id.recyclerView);
        animationLayout = findViewById(R.id.animationLayout);
        loadingImage = findViewById(R.id.loadingAnimationImage);
        messageInputLayout = findViewById(R.id.messageInputView);
        messageEditText = findViewById(R.id.messageEditText);
        messageButton = findViewById(R.id.messageButton);
    }

    public void loadConversationUI() {
        animationLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        messageInputLayout.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new ConversationRecyclerViewAdapter(conversation, user.getId());
        recyclerView.setAdapter(mAdapter);
    }

    public void createLoadingAnimation() {
        loadingImage.setBackgroundResource(R.drawable.loading_animation);
        loadingAnimation = (AnimationDrawable) loadingImage.getBackground();
        loadingAnimation.start();
    }

    public void fetchUser(Intent intent) {
        user = (User) intent.getSerializableExtra("user");
        if(user != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("user", gson.toJson(user));
            editor.apply();
        } else {
            user = gson.fromJson(
                    sharedPref.getString("user", null),
                    User.class);
        }
    }

    public void fetchConversation() {
        conversation = gson.fromJson(
                sharedPref.getString("conversation", null),
                Conversation.class);
    }

    public void saveConversation(Conversation conversation) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("conversation", gson.toJson(conversation));
        editor.apply();
    }

    public void handleIntentType(Intent intent) {
        String intentType = intent.getStringExtra("intentType");
        if(intentType != null) {
            switch (intentType) {
                case "AGENT_REQUEST"://received only by agents
                    startConversation(user.getId(), intent.getLongExtra("userId", -1));
                    break;
                case "CONVERSATION":
                    long conversationId = intent.getLongExtra("conversationId", -1);
                    if(conversationId != -1)
                        getConversation(conversationId);
                    break;
                default:
                    break;
            }
        }
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

    public void startConversation(long agentId, long userId) {
        String url = getResources().getString(R.string.startConversationUrl)
                + "?agentId=" + agentId
                + "&userId=" + userId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Conversation> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                null,
                Conversation.class,
                headers,
                response -> {
                    this.conversation = response;
                    saveConversation(conversation);
                    loadConversationUI();
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

    public void getConversation(long conversationId) {
        String url = getResources().getString(R.string.getConversationUrl)
                + "?conversationId=" + conversationId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Conversation> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                Conversation.class,
                headers,
                response -> {
                    conversation = response;
                    saveConversation(conversation);
                    loadConversationUI();
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

    public void getConversationMessages(long conversationId) {
        String url = getResources().getString(R.string.getConversationMessagesUrl)
                + "?conversationId=" + conversationId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<List> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                List.class,
                headers,
                response -> {
                    conversation
                            .setMessages(gson.fromJson(gson.toJson(response),
                                    new TypeToken<List<Message>>(){}.getType()));
                    loadConversationUI();
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

