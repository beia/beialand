package com.main.citisim.runnables;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.main.citisim.MainActivity;
import com.main.citisim.MapActivity;
import com.main.citisim.data.DeviceParameters;
import com.main.citisim.profile;

import java.util.ArrayList;

public class UpdateMarkers implements Runnable
{

    public static int speedTime=400;
    private ArrayList<DeviceParameters> deviceParametersArrayList;//a time function
    public UpdateMarkers(ArrayList<DeviceParameters> deviceParametersArrayList)
    {
        this.deviceParametersArrayList = deviceParametersArrayList;
    }
    @Override
    public void run()
    {
        long updateInterval = Math.round(deviceParametersArrayList.size()==0?1:5000 / deviceParametersArrayList.size());
        try
        {
            for (int i = 0; i < deviceParametersArrayList.size(); i++)
            {
                Message showMarkerMessage = MapActivity.markersHandler.obtainMessage(1);
                showMarkerMessage.obj = deviceParametersArrayList.get(i);
                if(i == 0)//if it's the first marker tell the handler to move the camera over it
                {
                    showMarkerMessage.arg1 = 0;
                }
                else if(i == deviceParametersArrayList.size()-1)//if it's the last marker
                {
                    showMarkerMessage.arg1 = 2;
                }
                else
                {
                    showMarkerMessage.arg1 = 1;
                }
                showMarkerMessage.sendToTarget();
                Thread.sleep(speedTime);
            }
            MapActivity.historyThreadFinished = true;
            profile.setIsReadyHistory(false);

        }
        catch (InterruptedException e)
        {
                e.printStackTrace();
        }
    }


}
