package com.beia.solomon.runnables;

import com.beia.solomon.networkPackets.LocationData;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class SendLocationDataRunnable implements Runnable
{
    public int userId;
    public String beaconId;
    public String beaconLabel;
    public int mallId;
    public boolean zoneEntered;
    public Date time;
    public ObjectOutputStream objectOutputStream;
    public SendLocationDataRunnable(int userId, String beaconId, String beaconLabel, int mallId, boolean zoneEntered, Date time, ObjectOutputStream objectOutputStream)
    {
        this.userId = userId;
        this.beaconId = beaconId;
        this.beaconLabel = beaconLabel;
        this.mallId = mallId;
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
                objectOutputStream.writeObject(new LocationData(userId, beaconId, beaconLabel, mallId, zoneEntered, time.toString()));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
