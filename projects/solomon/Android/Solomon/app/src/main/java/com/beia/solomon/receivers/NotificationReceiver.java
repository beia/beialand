package com.beia.solomon.receivers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.beia.solomon.App;
import com.beia.solomon.R;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.runnables.RequestRunnable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import static android.content.Context.POWER_SERVICE;
import static com.beia.solomon.App.CHANNEL_1_ID;
import static com.beia.solomon.App.CHANNEL_2_ID;

public class NotificationReceiver extends BroadcastReceiver
{
    public static Context context;
    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {

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
    };
    @Override
    public void onReceive(Context context, Intent intent) {
        //ask for a notification from the server and if we have a new notification we show the notification to the user
        Log.d("ALARM", "onReceive: ");
        NotificationReceiver.context = context;
        int idUser = intent.getIntExtra("idUser", 0);
        if(MainActivity.objectOutputStream == null)
        {
            try {
                String requestString = "{\"requestType\":\"getNotifications\", \"userId\":" + idUser +"}";
                Log.d("USER", "onReceive: " + idUser);
                new Thread(new RequestRunnable(requestString)).start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
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

        //wake up screen
        App.notificationManager.notify(1, notification);
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"solomon:wakelock");
        wakeLock.acquire(2000);
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

        //wake up screen
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"solomon:wakelock");
        wakeLock.acquire(2000);
    }
}
