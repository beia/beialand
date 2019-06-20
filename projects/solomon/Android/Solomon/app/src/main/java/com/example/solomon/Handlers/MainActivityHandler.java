package com.example.solomon.Handlers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.solomon.MainActivity;

import java.io.ObjectInputStream;

public class MainActivityHandler extends Handler
{
    private MainActivity mainActivity;
    public MainActivityHandler(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }
    @Override
    public void handleMessage(Message msg)
    {
        switch(msg.what)
        {
            case 1:
                //beacons where received
                if(mainActivity.beacons.isEmpty())
                {
                    Log.d("Handler", "no beacons");
                }
                //set Estimote beacons
                //mainActivity.initEstimoteBeacons();
                //set Kontakt beacons
                mainActivity.initKontaktBeacons();
                break;
            default:
                break;
        }
    }
}
