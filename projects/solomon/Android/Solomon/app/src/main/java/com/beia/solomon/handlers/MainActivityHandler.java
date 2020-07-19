package com.beia.solomon.handlers;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.activities.ProfileSettingsActivity;
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.ImageData;
import com.beia.solomon.networkPackets.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
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
            case 1://RECEIVED THE BEACONS
                for (Map.Entry entry : MainActivity.beaconsMap.entrySet())
                {
                    Beacon beacon = (Beacon) entry.getValue();
                    //initialize all the malls and set all the malls entered values to false
                    if(MainActivity.mallsEntered.isEmpty())
                    {
                        MainActivity.mallsEntered.put(beacon.getMallId(), false);
                    }
                    else
                    {
                        if(!MainActivity.mallsEntered.containsKey(beacon.getMallId()))
                        {
                            MainActivity.mallsEntered.put(beacon.getMallId(), false);
                        }
                    }
                }
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
                Log.d("POSITION", "handleMessage: ");
                LatLng latLng = (LatLng) msg.obj;
                LatLng coordinates = new LatLng(latLng.latitude, latLng.longitude);
                MainActivity.mapFragment.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 20.0f));
                Marker positionMarker = MainActivity.mapFragment.googleMap.addMarker(new MarkerOptions().position(coordinates).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                MainActivity.positionMarkers.add(positionMarker);
                if(MainActivity.positionMarkers.size() > 1)
                {
                    MainActivity.positionMarkers.poll().remove();//remove the head of the queue leaving only the new marker
                }
                break;
            case 5://profile picture
                ImageData imageData = (ImageData) msg.obj;
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData.getImageBytes(), 0, imageData.getImageBytes().length);
                MainActivity.settingsFragment.profilePicture.setImageBitmap(bitmap);

                //get preferences
                SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
                //save the profile picture into the memory
                try
                {
                    //save the profile picture into the memory
                    String imageString = ProfileSettingsActivity.encodeTobase64(bitmap);
                    String userImageString = imageString + " " + MainActivity.userData.getUserId();
                    editor.putString("profilePicture", userImageString);
                    editor.apply();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
            case 6://userLocationsHeatMap
                Log.d("USER_LOCATIONS_HEAT_MAP", "handleMessage: ");
                break;
            case 7://beaconsTimeMap
                Log.d("BEACONS_TIME_MAP", "handleMessage: ");
                break;
            default:
                break;
        }
    }
}
