package com.beia.solomon.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.os.Handler;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

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
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng afi = new LatLng(50, 6.051922);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(afi, 18.0f));
    }
}
