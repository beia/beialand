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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback{

    public GoogleMap googleMap;
    public HeatmapTileProvider heatMapTileProvider;
    public TileOverlay heatmapOverlay;
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
        googleMap.setMyLocationEnabled(true);

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
            if(!MainActivity.beaconsMap.isEmpty() && MainActivity.beaconsTimeMap != null)
                addHeatMap(MainActivity.beaconsMap, MainActivity.beaconsTimeMap);
        }
    }
    private void addHeatMap(HashMap<String, Beacon> beacons, HashMap<String, Long> beaconTimesMap) {
        List<WeightedLatLng> beaconsLatLngs = new ArrayList<>();
        //compute the sum of all the beacon times
        long totalTimeSum = 0;
        for(Long timeSeconds : beaconTimesMap.values())
            totalTimeSum += timeSeconds;

        for(Beacon beacon : beacons.values()) { //add weighted latlng objects that contain latitude longitude and a normalized value from 0 - 100 that represents the weight of the time spent near a beacon
            if(beaconTimesMap.containsKey(beacon.getId()))
                beaconsLatLngs.add(new WeightedLatLng(
                            new LatLng(beacon.getCoordinates().getLatitude(), beacon.getCoordinates().getLongitude()),
                            (float) beaconTimesMap.get(beacon.getId()) / totalTimeSum * 100));
            else
                beaconsLatLngs.add(new WeightedLatLng(
                        new LatLng(beacon.getCoordinates().getLatitude(), beacon.getCoordinates().getLongitude()),
                        0));
        }

        //Create a heat map tile provider, passing it the latlngs of the beacons
        heatMapTileProvider = new HeatmapTileProvider.Builder()
                .weightedData(beaconsLatLngs)
                .radius(30)
                .build();
        //Add a tile overlay to the map, using the heat map tile provider
        heatmapOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatMapTileProvider));
    }
}
