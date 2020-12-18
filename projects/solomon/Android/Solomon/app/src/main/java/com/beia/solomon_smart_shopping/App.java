package com.beia.solomon_smart_shopping;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


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
            mallAlertsChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
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