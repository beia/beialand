package com.beia.solomon_smart_shopping.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon_smart_shopping.GsonRequest;
import com.beia.solomon_smart_shopping.R;
import com.beia.solomon_smart_shopping.adapters.ConversationRecyclerViewAdapter;
import com.beia.solomon_smart_shopping.model.Conversation;
import com.beia.solomon_smart_shopping.model.ConversationStatus;
import com.beia.solomon_smart_shopping.model.Message;
import com.beia.solomon_smart_shopping.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskUsActivity extends AppCompatActivity {

    private User user;
    private Conversation conversation;

    private LinearLayout toolbar;
    private TextView toolbarText;
    private TextView leaveChatButton;
    private RecyclerView recyclerView;
    private ConversationRecyclerViewAdapter mAdapter;
    private LinearLayout animationLayout;
    private ImageView loadingImage;
    private LinearLayout messageInputLayout;
    private EditText messageEditText;
    private Button sendMessageButton;

    private SharedPreferences sharedPref;
    private Gson gson;

    RequestQueue volleyQueue;
    AnimationDrawable loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void initUI() {
        toolbar = findViewById(R.id.toolbar);
        toolbarText = findViewById(R.id.toolbarText);
        leaveChatButton = findViewById(R.id.leaveChatButton);
        recyclerView = findViewById(R.id.recyclerView);
        animationLayout = findViewById(R.id.animationLayout);
        loadingImage = findViewById(R.id.loadingAnimationImage);
        messageInputLayout = findViewById(R.id.messageInputView);
        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.messageButton);

        setConversationPadding();
        scrollToConversationEndOnLayoutChange();
        setupClickListeners();
    }

    private void setConversationPadding() {
        ViewTreeObserver messageTreeObserver = messageInputLayout.getViewTreeObserver();
        ViewTreeObserver toolbarTreeObserver = toolbar.getViewTreeObserver();

        if (messageTreeObserver.isAlive()) {
            messageTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    messageInputLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    float viewHeight = messageInputLayout.getHeight();
                    recyclerView.setPadding(0, recyclerView.getPaddingTop(), 0, (int) viewHeight);
                }
            });
        }

        if (toolbarTreeObserver.isAlive()) {
            toolbarTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    float viewHeight = toolbar.getHeight();
                    recyclerView.setPadding(0, (int) viewHeight, 0, recyclerView.getPaddingBottom());
                }
            });
        }
    }

    private void setupClickListeners() {
        sendMessageButton.setOnClickListener(v -> {
            User sender, receiver;
            if(user.getRole().equals("AGENT")) {
                sender = conversation.getUser1();
                receiver = conversation.getUser2();
            } else {
                sender = conversation.getUser2();
                receiver = conversation.getUser1();
            }
            if(!messageEditText.getText().toString().trim().equals("")) {
                sendMessage(Message.builder()
                        .senderId(sender.getId())
                        .receiverId(receiver.getId())
                        .text(messageEditText.getText().toString())
                        .date(LocalDateTime.now().toString())
                        .conversationId(conversation.getId())
                        .build());
                messageEditText.setText("");
            }
        });

        leaveChatButton.setOnClickListener(v -> {
            if(conversation != null)
                finishConversation(conversation);
        });
    }

    private void scrollToConversationEndOnLayoutChange() {
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if(conversation != null
                    && conversation.getMessages() != null
                    && !conversation.getMessages().isEmpty()) {
                recyclerView.scrollToPosition(conversation.getMessages().size() - 1);
            }
        });
    }

    public void loadConversationUI(Conversation conversation) {
        animationLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        messageInputLayout.setVisibility(View.VISIBLE);
        if(user.getRole().equals("AGENT")) {
            leaveChatButton.setVisibility(View.VISIBLE);
            toolbarText.setText("Chatting with user");
        } else {
            toolbarText.setText("Chatting with agent");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new ConversationRecyclerViewAdapter(conversation, user.getId());
        recyclerView.setAdapter(mAdapter);

        new Thread(() -> {
            while (true) {
                try {
                    getConversationMessages(conversation.getId());
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
        if(conversation != null) {
            getConversation(conversation.getId());
        } else {
            if(user.getRole().equals("USER")) {
                getConversationByUserId(user.getId());
            } else {
                getConversationByAgentId(user.getId());
            }
        }
    }

    public void saveConversation(Conversation conversation) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("conversation", gson.toJson(conversation));
        editor.apply();
    }

    public void clearConversation() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("conversation");
        editor.apply();
        conversation = null;
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
                    loadConversationUI(conversation);
                    saveConversation(conversation);
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
                    if(response.getStatus().equals(ConversationStatus.FINISHED)) {
                        clearConversation();
                        if(user.getRole().equals("USER")) {
                            getConversationByUserId(user.getId());
                        } else {
                            getConversationByAgentId(user.getId());
                        }
                    } else {
                        this.conversation = response;
                        loadConversationUI(conversation);
                        saveConversation(conversation);
                    }
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

    public void getConversationByUserId(long userId) {
        String url = getResources().getString(R.string.getConversationByUserIdUrl)
                + "?userId=" + userId;

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
                    this.conversation = response;
                    loadConversationUI(conversation);
                    saveConversation(conversation);
                },
                error -> {
                    requestAgentChat(user.getId());
                });
        volleyQueue.add(request);
    }

    public void getConversationByAgentId(long agentId) {
        String url = getResources().getString(R.string.getConversationByAgentIdUrl)
                + "?agentId=" + agentId;

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
                    this.conversation = response;
                    loadConversationUI(conversation);
                    saveConversation(conversation);
                },
                error -> {
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
                    List<Message> messages = gson.fromJson(gson.toJson(response),
                            new TypeToken<List<Message>>(){}.getType());
                    if(conversation != null && areNewMessages(messages, conversation.getMessages())) {
                        conversation.setMessages(messages);
                        mAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size() - 1);
                        saveConversation(conversation);
                    }
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

    private boolean areNewMessages(List<Message> updatedMessages, List<Message> messages) {
        return updatedMessages.stream()
                .anyMatch(updatedMessage -> messages.stream()
                        .noneMatch(message -> message.equals(updatedMessage)));
    }

    public void sendMessage(Message message) {
        String url = getResources().getString(R.string.sendMessageUrl);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Message> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                message,
                Message.class,
                headers,
                response -> {
                    conversation.getMessages().add(response);
                    mAdapter.notifyItemInserted(conversation.getMessages().size() - 1);
                    recyclerView.scrollToPosition(conversation.getMessages().size() - 1);
                    saveConversation(conversation);
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

    public void finishConversation(Conversation conversation) {
        String url = getResources().getString(R.string.finishConversationUrl)
                + "?conversationId=" + conversation.getId();

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
                    clearConversation();
                    finish();
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

