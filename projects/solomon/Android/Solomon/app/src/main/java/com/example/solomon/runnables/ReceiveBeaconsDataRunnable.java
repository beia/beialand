package com.example.solomon.runnables;

import android.os.Message;
import android.util.Log;

import com.example.solomon.MainActivity;
import com.example.solomon.networkPackets.Beacon;
import com.example.solomon.networkPackets.BeaconsData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class ReceiveBeaconsDataRunnable implements Runnable
{
    private HashMap<String, Beacon> beacons;
    private ObjectInputStream objectInputStream;
    public ReceiveBeaconsDataRunnable(HashMap<String, Beacon> beacons, ObjectInputStream objectInputStream)
    {
        this.beacons = beacons;
        this.objectInputStream = objectInputStream;
    }
    @Override
    public void run()
    {
        try
        {
            BeaconsData beaconsData = (BeaconsData) objectInputStream.readObject();
            MainActivity.beacons = beaconsData.getBeaconsData();//change .. make beacons not static
            Message message = MainActivity.mainActivityHandler.obtainMessage(1);
            message.sendToTarget();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
