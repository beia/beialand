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
import com.main.citisim.profile;

import java.util.ArrayList;

public class UpdateMarkers implements Runnable
{

    public static int speedTime=400;

   // Activity activity;
    private ArrayList<LatLng> positions;
    public UpdateMarkers(ArrayList<LatLng> positions)
    {
        this.positions = positions;
       // this.activity=activity;
        Log.d("terogeu",positions.toString());
    }
    @Override
    public void run()
    {
        long updateInterval = Math.round(positions.size()==0?1:5000 / positions.size());
        try
        {
            Log.d("positions",positions.size()+"");

            for (int i = 0; i < positions.size(); i++)
            {
                Message showMarkerMessage = MapActivity.markersHandler.obtainMessage(1);

                showMarkerMessage.obj = positions.get(i);

                if(i == 0)//if it's the first marker tell the handler to move the camera over it
                    showMarkerMessage.arg1 = 0;
                else
                if(i == positions.size()-1)//if it's the last marker
                {

                    showMarkerMessage.arg1 = 2;
                   // showMarkerMessage.obj = positions.get(i);
                    Log.d("test","intra");

                }
                else
                    showMarkerMessage.arg1 = 1;


                Log.d("show","marker : "+positions.get(i).toString());
                showMarkerMessage.sendToTarget();
                //Thread.sleep(updateInterval);
                Thread.sleep(speedTime);

            }
            Thread.sleep(1500);
            profile.setIsReadyHistory(false);


            //MapActivity.refreshMap();


        }
        catch (InterruptedException e)
        {
                e.printStackTrace();
        }
    }


}
