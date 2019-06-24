package com.example.solomon.runnables;

import android.util.Log;

import com.example.solomon.networkPackets.LocationData;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class SendLocationDataRunnable implements Runnable
{
    public int userId;
    public int storeId;
    public String zoneName;
    public boolean zoneEntered;
    public Date time;
    public ObjectOutputStream objectOutputStream;
    public SendLocationDataRunnable(int userId, int storeId, String zoneName, boolean zoneEntered, Date time, ObjectOutputStream objectOutputStream)
    {
        this.userId = userId;
        this.storeId = storeId;
        this.zoneName = zoneName;
        this.zoneEntered = zoneEntered;
        this.time = time;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run() {
        try
        {
            //send the location data
            synchronized (objectOutputStream) {
                objectOutputStream.writeObject(new LocationData(userId, storeId, zoneName, zoneEntered, time.toString()));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
