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
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon.GsonRequest;
import com.beia.solomon.R;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.model.Beacon;
import com.beia.solomon.model.BeaconType;
import com.beia.solomon.model.Location;
import com.beia.solomon.model.Mall;
import com.beia.solomon.model.MapZone;
import com.beia.solomon.model.ParkingSpace;
import com.beia.solomon.model.ProximityStatus;
import com.beia.solomon.model.Status;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
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

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private RequestQueue volleyQueue;
    private Gson gson;
    private List<Mall> malls;
    private List<Beacon> beacons;
    private List<Location> locations;

    private View view;
    private GoogleMap googleMap;

    private Map<String, Marker> beaconMarkers;
    private Map<Long, Marker> parkingSpacesMarkers;
    private Marker carMarker;
    private ParkingSpace occupiedByCarParkingSpace;
    private boolean storeMarkersLoaded = false;

    private HeatmapTileProvider heatMapTileProvider;
    private TileOverlay heatmapOverlay;
    private ImageView heatmapButton;
    private boolean heatmapActive;

    private void getHeatmapLocations(String startDate, String endDate) {
        String url = view.getContext().getResources().getString(R.string.getHeatmapLocationsUrl)
                + "?startDate=" + startDate
                + "&endDate=" + endDate;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", view.getContext().getResources().getString(R.string.universal_user));

        GsonRequest<List> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                List.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "RECEIVED HEATMAP LOCATIONS");
                    locations = gson.fromJson(gson.toJson(response), new TypeToken<List<Location>>(){}.getType());
                    addHeatMap(locations);
                },
                error -> {
                    if(error.networkResponse != null && error.networkResponse.data != null)
                        Log.d("ERROR", "getHeatmapLocations: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }



    public MapFragment(Map<String, Marker> beaconMarkers, Map<Long, Marker> parkingSpacesMarkers) {
        this.beaconMarkers = beaconMarkers;
        this.parkingSpacesMarkers = parkingSpacesMarkers;
    }

    @Override
    public void setArguments(Bundle bundle) {
        super.setArguments(bundle);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        this.view = v;
        heatmapButton = view.findViewById(R.id.buttonHeatMap);
        volleyQueue = Volley.newRequestQueue(view.getContext());

        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        if(mapFragment != null)
            mapFragment.getMapAsync(this);

        gson = new Gson();
        Bundle bundle = getArguments();
        if(bundle != null) {
            ArrayList<String> bundleData = bundle.getStringArrayList("mapData");
            if(bundleData != null) {
                beacons = gson.fromJson(bundleData.get(0), new TypeToken<List<Beacon>>() {}.getType());
                malls = gson.fromJson(bundleData.get(1), new TypeToken<List<Mall>>() {}.getType());
            }
        }
        return v;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        setupMapZone(MapZone.BUCHAREST);
        setupHeatmapButton();
        setupMalls();
        setupMapListener();
        setupMarkerListener();
    }

    private void setupMalls() {
        malls
                .forEach(mall -> {
                    LatLng storeLocation = new LatLng(mall.getLatitude(), mall.getLongitude());
                    GlideUrl glideUrl = new GlideUrl(getResources().getString(R.string.mallPicturesUrl) + "/" + mall.getId() + "_small.png",
                            new LazyHeaders.Builder()
                                    .addHeader("Authorization", getResources().getString(R.string.universal_user))
                                    .build());
                    Glide.with(this)
                            .asBitmap()
                            .load(glideUrl)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(storeLocation)
                                            .icon(BitmapDescriptorFactory
                                                    .fromBitmap(createStoreMarker(MainActivity.context, resource, ProximityStatus.FAR))));
                                    marker.setTitle(mall.getName());
                                    marker.setTag(mall);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                });
    }

    private void setupBeacons() {
        beacons
                .stream()
                .filter(beacon -> beacon.getType().equals(BeaconType.NORMAL))
                .forEach(beacon -> {
                    LatLng storeLocation = new LatLng(beacon.getLatitude(), beacon.getLongitude());
                    GlideUrl glideUrl = new GlideUrl(getResources().getString(R.string.beaconPicturesUrl) + "/" + beacon.getManufacturerId() + ".png",
                            new LazyHeaders.Builder()
                                    .addHeader("Authorization", getResources().getString(R.string.universal_user))
                                    .build());
                    Glide.with(this)
                            .asBitmap()
                            .load(glideUrl)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(storeLocation)
                                            .icon(BitmapDescriptorFactory
                                                    .fromBitmap(createStoreMarker(MainActivity.context, resource, ProximityStatus.FAR))));
                                    marker.setTitle(beacon.getName());
                                    marker.setTag(beacon);
                                    beaconMarkers.put(beacon.getManufacturerId(), marker);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                });
    }

    private void setupParkingSpaces() {
        malls
                .stream()
                .filter(mall -> mall.getParkingSpaces() != null)
                .forEach(mall -> mall.getParkingSpaces().forEach(parkingSpace -> {
            Marker parkingSpaceMarker;
            if(parkingSpace
                    .getParkingData()
                    .get(parkingSpace.getParkingData().size() - 1)
                    .getStatus()
                    .equals(Status.FREE)) {
                 parkingSpaceMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.free_parking_space))
                        .rotation(parkingSpace.getRotation()));
            }
            else {
                parkingSpaceMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.occupied_parking_space))
                        .rotation(parkingSpace.getRotation()));
            }
            parkingSpaceMarker.setTag(parkingSpace);
            parkingSpacesMarkers.put(parkingSpace.getId(), parkingSpaceMarker);
        }));
    }

    private void setupMapListener() {
        googleMap.setOnCameraMoveListener(() -> {
            if(googleMap.getCameraPosition().zoom > 18 && !storeMarkersLoaded) {
                googleMap.clear();
                setupBeacons();
                setupParkingSpaces();
                storeMarkersLoaded = true;
            }
            else if(googleMap.getCameraPosition().zoom < 18 && storeMarkersLoaded) {
                googleMap.clear();
                setupMalls();
                storeMarkersLoaded = false;
            }
        });
    }

    private void setupMarkerListener() {
        googleMap.setOnMarkerClickListener(marker -> {
            if(marker.getTag() instanceof Mall) {
                Mall mall = (Mall) marker.getTag();
                googleMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(mall.getLatitude(),
                                        mall.getLongitude()),
                                20.5f));
                return true;
            }
            else if (marker.getTag() instanceof ParkingSpace) {
                ParkingSpace parkingSpace = (ParkingSpace) marker.getTag();
                if(parkingSpace.getParkingData() != null &&
                        parkingSpace
                                .getParkingData()
                                .get(0)
                                .getStatus()
                                .equals(Status.FREE)) {
                    carMarker = createCarMarker(parkingSpace);
                    occupiedByCarParkingSpace = parkingSpace;
                    removeParkingSpaceMarker(parkingSpace);
                }
            }
            else if(marker.equals(carMarker)) {
                carMarker.remove();
                Marker parkingSpaceMarker = createParkingMarker(occupiedByCarParkingSpace);
                parkingSpacesMarkers.put(occupiedByCarParkingSpace.getId(), parkingSpaceMarker);
            }
            return false;
        });
    }

    private void setupHeatmapButton() {
        heatmapActive = false;
        heatmapButton.setOnClickListener((view) -> {
            if(!heatmapActive) {
                getHeatmapLocations(LocalDateTime.now().minusMinutes(30).toString(),
                        LocalDateTime.now().toString());
                heatmapButton.setImageResource(R.drawable.heat_map_activ);
            }
            else {
                if(heatmapOverlay != null)
                    heatmapOverlay.setVisible(false);
                heatmapButton.setImageResource(R.drawable.heat_map_inactiv);
            }
            heatmapActive = !heatmapActive;
        });
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

    private Marker createParkingMarker(ParkingSpace parkingSpace) {
        Marker marker = null;
        if(parkingSpace != null && parkingSpace.getParkingData() != null) {
            if(occupiedByCarParkingSpace.getParkingData().get(0).getStatus().equals(Status.FREE)) {
                 marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.free_parking_space))
                        .rotation(occupiedByCarParkingSpace.getRotation()));
            }
            else {
                marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(occupiedByCarParkingSpace.getLatitude(),
                                occupiedByCarParkingSpace.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.free_parking_space))
                        .rotation(occupiedByCarParkingSpace.getRotation()));
            }
        }
        return marker;
    }

    private Marker createCarMarker(ParkingSpace parkingSpace) {
        Marker marker = null;
        if(parkingSpace != null) {
            marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                        .rotation(parkingSpace.getRotation()));
        }
        return marker;
    }

    private void removeParkingSpaceMarker(ParkingSpace parkingSpace) {
        if(parkingSpacesMarkers.get(parkingSpace.getId()) != null)
            parkingSpacesMarkers.get(parkingSpace.getId()).remove();
    }

    private void setupMapZone(MapZone mapZone) {
        googleMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mapZone.getCoordinates(), mapZone.getZoom()));
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }
}
