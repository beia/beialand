package com.main.citisim;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MarkersHandler extends Handler
{
    private GoogleMap mMap;
    private Bitmap smallMarker;
    private Bitmap firstMarker;
    private Bitmap lastMarker;
    static int counter=1;
    static Marker m1;
    static Marker m3;
    static Marker m2;
    static boolean isRefreshed=false;
    public static ArrayList<Marker> markerVector = new ArrayList<>();






    public MarkersHandler(GoogleMap mMap, Bitmap smallMarker,Bitmap firstMarker,Bitmap lastMarker)
    {
        this.mMap = mMap;
        this.smallMarker = smallMarker;
        this.firstMarker = firstMarker;
        this.lastMarker = lastMarker;
        counter=1;

    }



    @Override
    public void handleMessage(Message msg) {




        if (profile.isReadyHistory == true) {
            switch (msg.what) {
                case 1:
                    //adaugam marker
                    LatLng position = (LatLng) msg.obj;
                    if (msg.arg1 == 0) {
                        //move camera over first marker

                        if(mMap!=null)
                            mMap.clear();

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16.5f));



                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(firstMarker)).position(position)) ;





                    }

                    if (msg.arg1 == 2) {

                        for(int i= 0; i<markerVector.size();i++){
                            markerVector.get(i).remove();
                        }

                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(lastMarker)).position(position)) ;
                        MapActivity.isDisplayed=false;
                    }
                    else {


                        Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).position(position));


                        markerVector.add(marker);

                        if (markerVector.size() > 6) {
                            markerVector.get(0).remove();
                            markerVector.remove(0);

                            markerVector.get(0).setAlpha(0.3f);
                            markerVector.get(1).setAlpha(0.4f);
                            markerVector.get(2).setAlpha(0.5f);
                            markerVector.get(3).setAlpha(0.6f);
                            markerVector.get(4).setAlpha(0.7f);
                            markerVector.get(5).setAlpha(0.8f);

                        }
                    }



                break;
            }
        }
    }
}