package com.beia.solomon.runnables;

import android.os.Message;
import android.util.Log;

import com.beia.solomon.MainActivity;
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.BeaconsData;
import com.beia.solomon.networkPackets.Mall;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class ReceiveBeaconsDataRunnable implements Runnable
{
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    public ReceiveBeaconsDataRunnable(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream)
    {
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run()
    {
        try
        {
            BeaconsData beaconsData = (BeaconsData) objectInputStream.readObject();
            MainActivity.beacons = beaconsData.getBeaconsData();//change .. make beacons not static
            objectOutputStream.writeObject("Client received beacons");

            HashMap<Integer, Mall> malls = (HashMap<Integer, Mall>) objectInputStream.readObject();
            MainActivity.malls = malls;
            objectOutputStream.writeObject("Client received malls");

            //before listening to the beacons and sending data to the server regarding positions we must be sure that the server is listening for the data
            String serverFeedback = (String)objectInputStream.readObject();

            if(serverFeedback.equals("Started listening to the location data"))
            {
                //send a message to the handler in the main ui thread that we can start detecting the beacons and sending data to the server regarding user position
                Message message = MainActivity.mainActivityHandler.obtainMessage(1);
                message.sendToTarget();
            }
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
