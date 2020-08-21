package com.beia.solomon.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.data.Location;
import com.beia.solomon.model.Beacon;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback{

    public GoogleMap googleMap;
    public HeatmapTileProvider heatMapTileProvider;
    public TileOverlay heatmapOverlay;

    public void setArguments(@Nullable Bundle args, String bundleDataName)
    {
        super.setArguments(args);
        ArrayList<String> bundleData = args.getStringArrayList(bundleDataName);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
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
                LatLng storeLocation = new LatLng(beacon.getLatitude(), beacon.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(storeLocation).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                MainActivity.createStoreMarker(MainActivity.context, null)))).setTitle(beacon.getName());
            }
            if(MainActivity.userLocations != null)
                addHeatMap(MainActivity.userLocations);
        }
    }
    private void addHeatMap(List<Location> userLocations) {
        Log.d("HEAT_MAP", "addHeatMap: " + userLocations.size() + " locations");
        heatMapTileProvider = new HeatmapTileProvider.Builder()
                .data(userLocations.stream()
                        .map(location -> new LatLng(location.getLatitude(), location.getLongitude()))
                        .collect(Collectors.toList()))
                .build();
        heatmapOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatMapTileProvider));
    }
}
