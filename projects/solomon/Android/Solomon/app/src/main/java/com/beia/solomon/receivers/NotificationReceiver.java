package com.beia.solomon.receivers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.beia.solomon.App;
import com.beia.solomon.R;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.runnables.RequestRunnable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static com.beia.solomon.App.CHANNEL_1_ID;
import static com.beia.solomon.App.CHANNEL_2_ID;
import static com.beia.solomon.runnables.WaitForServerDataRunnable.ip;
import static com.beia.solomon.runnables.WaitForServerDataRunnable.port;

public class NotificationReceiver extends BroadcastReceiver
{
    public static int userId;
    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {

        public Context context = null;
        @Override
        public void handleMessage(Message msg) {

            if(context != null) {
                switch (msg.what) {
                    case 1: //RECEIVED NOTIFICATION
                        String response = (String) msg.obj;
                        Log.d("NOTIFICATIONS", "onReceive: received response:" + response);
                        //parse response
                        Gson gson = new Gson();
                        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                        String responseType = jsonResponse.get("responseType").getAsString();
                        switch (responseType) {
                            case "mallAlert":
                                String messageAlert = jsonResponse.get("message").getAsString();
                                sendOnMallAlertsChannel(context, "Solomon", messageAlert);
                                break;
                            case "normalNotification":
                                String messageNotification = jsonResponse.get("message").getAsString();
                                sendOnNormalNotificationsChannel(context, "Solomon", messageNotification);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        public void setContext(Context context) {
            this.context = context;
        }
    };
    @Override
    public void onReceive(Context context, Intent intent) {
        //ask for a notification from the server and if we have a new notification we show the notification to the user
        Log.d("ALARM", "onReceive: ");
        if(MainActivity.objectOutputStream == null)
        {
            try
            {
                String requestString = "{\"requestType\":\"getNotifications\", \"userId\":" + userId +"}";
                new Thread(new RequestRunnable(requestString)).start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        //sendOnMallAlertsChannel(context, "Solomon", "Mall alert");
        //sendOnNormalNotificationsChannel(context, "Solomon", "Normal notification");
    }
    //NOTIFICATIONS
    public static void sendOnMallAlertsChannel(Context context, String title, String message) {

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.solomon_notification_icon)
                .setColor(context.getResources().getColor(R.color.mallAlertsColor))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        App.notificationManager.notify(1, notification);
    }

    public static void sendOnNormalNotificationsChannel(Context context, String title, String message) {

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.solomon_notification_icon)
                .setColor(context.getResources().getColor(R.color.normalNotificationsColor))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        App.notificationManager.notify(2, notification);
    }
}
