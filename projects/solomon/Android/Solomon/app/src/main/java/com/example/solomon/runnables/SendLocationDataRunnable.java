package com.example.solomon.runnables;

import android.util.Log;

import com.example.solomon.networkPackets.LocationData;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class SendLocationDataRunnable implements Runnable
{
    public int userId;
    public Date time;
    public boolean zoneEntered;
    public ObjectOutputStream objectOutputStream;
    public SendLocationDataRunnable(int userId, Date time, boolean zoneEntered, ObjectOutputStream objectOutputStream)
    {
        this.userId = userId;
        this.time = time;
        this.zoneEntered = zoneEntered;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run() {
        try
        {
            objectOutputStream.writeObject(new LocationData(userId, 1, "Sala de conferinte", zoneEntered, "26.03.2019", time.toString()));
            Log.d("LOCATION SENT", "run: ");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
