package com.beia.solomon;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.beia.solomon.receivers.NotificationReceiver;


public class App extends Application {
    public static final String CHANNEL_1_ID = "MALL_ALERT";
    public static final String CHANNEL_2_ID = "NORMAL_NOTIFICATIONS";

    public static NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mallAlertsChannel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "MALL_ALERT",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mallAlertsChannel.setDescription("MALL ALERTS");
            mallAlertsChannel.setVibrationPattern(new long[]{1000, 0, 1000, 0, 1000});
            mallAlertsChannel.enableLights(true);

            NotificationChannel normalNotificationsChannel = new NotificationChannel(
                    CHANNEL_2_ID,
                    "NORMAL_NOTIFICATIONS",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            normalNotificationsChannel.setDescription("NORMAL NOTIFICATIONS");

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mallAlertsChannel);
            notificationManager.createNotificationChannel(normalNotificationsChannel);
        }
    }
}