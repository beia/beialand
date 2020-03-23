package com.beia.solomon.runnables;

import android.os.Message;
import android.util.Log;

import com.beia.solomon.MainActivity;
import com.beia.solomon.ProfileSettingsActivity;
import com.beia.solomon.networkPackets.BeaconsData;
import com.beia.solomon.networkPackets.Campaign;
import com.beia.solomon.networkPackets.ImageData;
import com.beia.solomon.networkPackets.Mall;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class WaitForServerDataRunnable implements Runnable
{
    private ObjectInputStream objectInputStream;
    public WaitForServerDataRunnable(ObjectInputStream objectInputStream)
    {
        this.objectInputStream = objectInputStream;
    }
    @Override
    public void run() {
        try
        {
            while (true)
            {
                Object networkPacket = objectInputStream.readObject();

                //RECEIVED BEACONS
                if(networkPacket instanceof BeaconsData)
                {
                    BeaconsData beaconsData = (BeaconsData) networkPacket;
                    MainActivity.beacons = beaconsData.getBeaconsData();//change .. make beacons not static
                    Log.d("BEACONS", "RECEIVED BEACON DATA");
                }

                //RECEIVED MALLS DATA
                if(networkPacket instanceof HashMap)
                {
                    HashMap<Integer, Mall> malls = (HashMap<Integer, Mall>) networkPacket;
                    MainActivity.malls = malls;
                    Log.d("BEACONS", "RECEIVED MALLS DATA");
                    //send a message to the handler in the main ui thread that we can start detecting the beacons and sending data to the server regarding user position
                    Message message = MainActivity.mainActivityHandler.obtainMessage(1);
                    message.sendToTarget();
                }

                //RECEIVED CAMPAIGNS FOR A STORE
                if(networkPacket instanceof ArrayList)
                {
                    ArrayList objects = (ArrayList)networkPacket;
                    for(Object object : objects)
                    {
                        if(object instanceof Campaign)
                        {
                            Campaign campaign = (Campaign)object;
                            MainActivity.campaigns.add(campaign);
                            Message message = MainActivity.mainActivityHandler.obtainMessage(2);
                            message.sendToTarget();
                        }
                    }
                }

                //PROFILE PICTURE
                if(networkPacket instanceof ImageData)
                {
                    ImageData imageData = (ImageData) networkPacket;
                    //Send a message to the ProfileSettings activity handler with the imagedata so we can change the profile picture
                    Message message = Message.obtain(ProfileSettingsActivity.handler);
                    message.what = 1;
                    message.obj = imageData;
                    message.sendToTarget();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
