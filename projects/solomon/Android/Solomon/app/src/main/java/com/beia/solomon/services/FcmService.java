package com.beia.solomon.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon.GsonRequest;
import com.beia.solomon.R;
import com.beia.solomon.activities.AskUsActivity;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.model.FcmMessageType;
import com.beia.solomon.model.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class FcmService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static SharedPreferences sharedPref;
    private RequestQueue volleyQueue;


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String messageType = remoteMessage.getData().get("messageType");
            if(messageType.equals(FcmMessageType.AGENT_REQUEST.name())) {
                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("message");
                long userId = Long.parseLong(Objects.requireNonNull(remoteMessage.getData().get("userId")));
                Bundle extraData = new Bundle();
                extraData.putLong("userId", userId);
                sendNotification(title, message, FcmMessageType.AGENT_REQUEST.name(), extraData);
            } else if(messageType.equals(FcmMessageType.CONVERSATION.name())) {
                long conversationId = Long.parseLong(remoteMessage.getData().get("conversationId"));
                Bundle extraData = new Bundle();
                extraData.putLong("conversationId", conversationId);
                sendNotification("Solomon",
                        "We found an available agent, please click so you can speak to him",
                        FcmMessageType.CONVERSATION.name(),
                        extraData);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]
    /**
     * Called if FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve
     * the token.
     */
    @Override
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        volleyQueue = Volley.newRequestQueue(this);
        sharedPref = getApplicationContext()
                .getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        User user = new Gson()
                .fromJson(sharedPref.getString("user", null), User.class);
        if(user != null)
            sendFCMToken(user.getId(), token);
    }
    // [END on_new_token]


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, String notificationType, Bundle extraData) {

        Intent intent;
        if(notificationType.equals(FcmMessageType.AGENT_REQUEST.name())) {
            intent = new Intent(this, AskUsActivity.class);
            intent.putExtra("intentType", "AGENT_REQUEST");
            intent.putExtra("userId", extraData.getLong("userId"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if(notificationType.equals(FcmMessageType.CONVERSATION.name())) {
            intent = new Intent(this, AskUsActivity.class);
            intent.putExtra("intentType", "CONVERSATION");
            intent.putExtra("conversationId", extraData.getLong("conversationId"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(this, AskUsActivity.class);
            intent.putExtra("intentType", "MESSAGE");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String defaultChannelId = getString(R.string.defaultChannelId);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, defaultChannelId)
                        .setSmallIcon(R.drawable.solomon_notification_icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel defaultChannel = new NotificationChannel(defaultChannelId,
                    "AGENT_NOTIFICATION_CHANNEL",
                    NotificationManager.IMPORTANCE_HIGH);
            if(notificationManager != null)
                notificationManager.createNotificationChannel(defaultChannel);
        }
        if(notificationManager != null)
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    public void sendFCMToken(long userId, String token) {
        String url = getResources().getString(R.string.fcm_token_url)
                + "?userId=" + userId;

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Object> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                token,
                Object.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "FCM TOKEN UPDATED");
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
