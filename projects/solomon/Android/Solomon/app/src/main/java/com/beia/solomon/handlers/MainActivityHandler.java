package com.beia.solomon.handlers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.beia.solomon.MainActivity;
import com.beia.solomon.networkPackets.Beacon;

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
            case 1:
                //set the beacons textviews data section
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
            case 2:
                //MainActivity.storeAdvertisementFragment.campaignsAdapter.notifyDataSetChanged();
                Log.d("CAMPAIGN", "handleMessage: ");
                break;
            default:
                break;
        }
    }
}
