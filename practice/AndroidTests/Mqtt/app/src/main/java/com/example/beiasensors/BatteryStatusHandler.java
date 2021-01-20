package com.example.beiasensors;


import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class BatteryStatusHandler extends Handler {
    public static final int BATTERY_PERCENTAGE_CHANGED = 1;
    public static final int CHARGING_STATUS_CHANGED = 2;

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if(msg.what == BATTERY_PERCENTAGE_CHANGED) {

        } else if(msg.what == CHARGING_STATUS_CHANGED) {

        }
    }
}
