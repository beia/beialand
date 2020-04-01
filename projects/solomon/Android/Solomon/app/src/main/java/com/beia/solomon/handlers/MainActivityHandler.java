package com.beia.solomon.handlers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.UserData;

import java.util.Map;

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
            case 1://RECEIVED THE BEACONS when inside the main activity
                for (Map.Entry entry : MainActivity.beacons.entrySet())
                {
                    Beacon beacon = (Beacon) entry.getValue();
                    //initialize all the malls and set all the malls entered values to false
                    if(MainActivity.mallsEntered.isEmpty())
                    {
                        MainActivity.mallsEntered.put(beacon.getMallId(), false);
                    }
                    else
                    {
                        if(MainActivity.mallsEntered.containsKey(beacon.getMallId()) == false)
                        {
                            MainActivity.mallsEntered.put(beacon.getMallId(), false);
                        }
                    }
                }
                // set Kontakt beacons
                mainActivity.initKontaktBeacons();
                break;
            case 2://RECEIVED A CAMPAIGN
                //MainActivity.storeAdvertisementFragment.campaignsAdapter.notifyDataSetChanged();
                Log.d("CAMPAIGN", "handleMessage: " + MainActivity.campaigns.size());
                break;
            case 3://AUTOMATIC LOGIN SUCCESSFUL
                //MainActivity.storeAdvertisementFragment.campaignsAdapter.notifyDataSetChanged();
                MainActivity.userData = (UserData) msg.obj;
                Log.d("AUTOMATIC-LOGIN", "SUCCESS");
                break;
            default:
                break;
        }
    }
}
