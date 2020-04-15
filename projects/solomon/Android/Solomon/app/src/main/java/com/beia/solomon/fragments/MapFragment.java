package com.beia.solomon.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.os.Handler;
import android.view.View;

import com.beia.solomon.R;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.networkPackets.Beacon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback{

    public GoogleMap googleMap;
    //Display information variables
    private boolean needsInit=false;

    public void setArguments(@Nullable Bundle args, String bundleDataName)
    {
        super.setArguments(args);
        ArrayList<String> bundleData = args.getStringArrayList(bundleDataName);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            needsInit=true;
        }
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        //create markers for all the beacons
        if(!MainActivity.beaconsMap.isEmpty()) {
            for(Beacon beacon : MainActivity.beaconsMap.values()) {
                if(beacon.getImage() != null) {
                    Bitmap storeImageBitmap = BitmapFactory.decodeByteArray(beacon.getImage(), 0, beacon.getImage().length);
                    LatLng storeLocation = new LatLng(beacon.getCoordinates().getLatitude(), beacon.getCoordinates().getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(storeLocation).
                            icon(BitmapDescriptorFactory.fromBitmap(
                                    MainActivity.createStoreMarker(MainActivity.context, storeImageBitmap)))).setTitle(beacon.getLabel());
                }
            }
        }
    }
}
