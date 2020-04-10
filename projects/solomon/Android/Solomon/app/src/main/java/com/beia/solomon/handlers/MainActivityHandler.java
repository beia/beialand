package com.beia.solomon.handlers;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
                // Reload current fragment
                Fragment frg = MainActivity.viewPagerAdapter.getItem(0);
                final FragmentTransaction ft = MainActivity.mainActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
                break;
            case 3://AUTOMATIC LOGIN SUCCESSFUL
                //MainActivity.storeAdvertisementFragment.campaignsAdapter.notifyDataSetChanged();
                MainActivity.userData = (UserData) msg.obj;
                Log.d("AUTOMATIC-LOGIN", "SUCCESS");
                break;
            case 4:
                LatLng latLng = (LatLng) msg.obj;
                LatLng coordinates = new LatLng(latLng.latitude, latLng.longitude);
                MainActivity.mapFragment.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 20.0f));
                Marker positionMarker = MainActivity.mapFragment.googleMap.addMarker(new MarkerOptions().position(coordinates));
                MainActivity.positionMarkers.add(positionMarker);
                if(MainActivity.positionMarkers.size() > 1)
                {
                    MainActivity.positionMarkers.poll().remove();//remove the head of the queue leaving only the new marker
                }
                break;
            default:
                break;
        }
    }
}
