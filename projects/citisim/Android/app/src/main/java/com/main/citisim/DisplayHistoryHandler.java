package com.main.citisim;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DisplayHistoryHandler extends Handler
{
    private GoogleMap mMap;
    private Bitmap smallMarker;

    @Override
    public void handleMessage(Message msg) {

        if (profile.isReadyHistory == true) {
            switch (msg.what) {
                case 1:
                    //adaugam marker
                    LatLng position = (LatLng) msg.obj;
                    if (msg.arg1 == 0) {
                        //move camera over first marker
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.5f));
                    }
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(position));

                    break;
                default:
                    break;
            }
        }
    }
}