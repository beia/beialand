package com.beia.solomon.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.beia.solomon.R;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.model.Location;
import com.beia.solomon.model.Beacon;
import com.beia.solomon.model.ProximityStatus;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback{

    private GoogleMap googleMap;
    private Gson gson;
    private List<Beacon> beacons;
    private HeatmapTileProvider heatMapTileProvider;
    private TileOverlay heatmapOverlay;
    private Map<String, Marker> beaconMarkers;

    public MapFragment(Map<String, Marker> beaconMarkers) {
        this.beaconMarkers = beaconMarkers;
    }

    @Override
    public void setArguments(Bundle bundle) {
        super.setArguments(bundle);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);

        gson = new Gson();
        Bundle bundle = getArguments();
        if(bundle != null) {
            ArrayList<String> bundleData = bundle.getStringArrayList("mapData");
            if(bundleData != null) {
                beacons = gson.fromJson(bundleData.get(0), new TypeToken<List<Beacon>>() {}.getType());
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        beacons.forEach(beacon -> {
            LatLng storeLocation = new LatLng(beacon.getLatitude(), beacon.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions().position(storeLocation)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(createStoreMarker(MainActivity.context, null, ProximityStatus.FAR))));
            marker.setTitle(beacon.getName());
            beaconMarkers.put(beacon.getManufacturerId(), marker);
        });
        if(MainActivity.userLocations != null)
            addHeatMap(MainActivity.userLocations);
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

    public Bitmap createStoreMarker(Context context, Bitmap bitmap, ProximityStatus proximityStatus) {
        View marker = null;
        switch (proximityStatus) {
            case CLOSE:
                marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.store_marker_entered, null);
                break;
            case FAR:
                marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.store_marker_left, null);
                break;
            default:
                break;
        }
        CircleImageView markerImageView = marker.findViewById(R.id.profile_image);
        markerImageView.setImageBitmap(bitmap);

        marker.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        marker.layout(0, 0, (int)(marker.getMeasuredWidth() * 0.6), (int)(marker.getMeasuredHeight() * 0.6)) ;
        marker.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap((int)(marker.getMeasuredWidth() * 0.6), (int)(marker.getMeasuredHeight() * 0.6),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = marker.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        marker.draw(canvas);

        return returnedBitmap;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
