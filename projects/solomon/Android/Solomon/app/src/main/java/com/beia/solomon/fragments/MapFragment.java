package com.beia.solomon.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon.GsonRequest;
import com.beia.solomon.backgroundTasks.MapHandler;
import com.beia.solomon.R;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.backgroundTasks.UpdateParkingData;
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

    public static MapHandler mapHandler;

    private RequestQueue volleyQueue;
    private Gson gson;
    private List<Mall> malls;
    private List<Beacon> beacons;
    private List<Location> locations;
    private LatLng carLocation;

    private View view;
    private GoogleMap googleMap;

    private Map<String, Marker> beaconMarkers;
    private Map<Long, Marker> parkingSpacesMarkers;
    private Marker carMarker;
    private ImageView findCarButton;
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
        findCarButton = view.findViewById(R.id.buttonFindCar);
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

        MapFragment.mapHandler = new MapHandler(this);
        new Thread(new UpdateParkingData(getContext(), volleyQueue)).start();

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
        setupFindCarButton();
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
                                                    .fromBitmap(createStoreMarker(MainActivity.context, resource, ProximityStatus.FAR)))
                                            .title(mall.getName()));
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
                                                    .fromBitmap(createStoreMarker(MainActivity.context, resource, ProximityStatus.FAR)))
                                            .title(beacon.getName()));
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
                .flatMap(mall -> mall.getParkingSpaces().stream())
                .filter(parkingSpace -> {
                    if(carLocation != null) {
                        return parkingSpace.getLatitude() != carLocation.latitude || parkingSpace.getLongitude() != carLocation.longitude;
                    }
                    return true;
                })
                .collect(Collectors.toList())
                .forEach(parkingSpace -> {
                    Marker parkingSpaceMarker;
                    parkingSpaceMarker = createParkingMarker(parkingSpace);
                    parkingSpaceMarker.setTag(parkingSpace);
                    parkingSpacesMarkers.put(parkingSpace.getId(), parkingSpaceMarker);
                });
    }

    private void setupMapListener() {
        googleMap.setOnCameraMoveListener(() -> {
            if(googleMap.getCameraPosition().zoom > 18 && !storeMarkersLoaded) {
                googleMap.clear();
                setupBeacons();
                setupParkingSpaces();
                if(carLocation != null) {
                    createCarMarkerAtLocation(carLocation);
                }
                else {
                    createCarMarkerFromCache();
                }
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
                                .get(parkingSpace.getParkingData().size() - 1)
                                .getStatus()
                                .equals(Status.FREE) &&
                        carMarker == null) {
                    carMarker = createCarMarker(parkingSpace);
                    occupiedByCarParkingSpace = parkingSpace;
                    removeParkingSpaceMarker(parkingSpace);
                }
            }
            else if(marker.equals(carMarker)) {
                carMarker.remove();
                carMarker = null;
                carLocation = null;
                deleteCarLocationFromCache();
                Marker parkingSpaceMarker = createParkingMarker(occupiedByCarParkingSpace);
                parkingSpaceMarker.setTag(occupiedByCarParkingSpace);
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

    private void setupFindCarButton() {
        findCarButton.setOnClickListener(v -> {

            if(carLocation != null) {
                googleMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(carLocation, 20.5f));
            }
            else {
                String carLat = MainActivity.sharedPref.getString("carLat", null);
                String carLng = MainActivity.sharedPref.getString("carLng", null);
                if(carLat != null && carLng != null) {
                    googleMap.animateCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(Double.parseDouble(carLat), Double.parseDouble(carLng)), 20.5f));
                }
                else {
                    Toast.makeText(getContext(), "Car not found!", Toast.LENGTH_SHORT).show();
                }
            }
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
            if(parkingSpace
                    .getParkingData()
                    .get(parkingSpace.getParkingData().size() - 1)
                    .getStatus().equals(Status.FREE)) {
                 marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parkingSpace.getLatitude(),
                                parkingSpace.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.free_parking_space))
                        .rotation(parkingSpace.getRotation())
                        .title("free"));
            }
            else {
                marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parkingSpace.getLatitude(),
                                parkingSpace.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.occupied_parking_space))
                        .rotation(parkingSpace.getRotation())
                        .title("occupied"));
            }
        }
        return marker;
    }

    private Marker createCarMarker(ParkingSpace parkingSpace) {
        Marker marker = null;
        if(parkingSpace != null) {
            carLocation = new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude());
            saveCarLocationInCache(carLocation);
            marker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parkingSpace.getLatitude(), parkingSpace.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                        .rotation(parkingSpace.getRotation()));
        }
        return marker;
    }

    private void createCarMarkerAtLocation(LatLng location) {
        if(location != null) {
            ParkingSpace carParkingSpace = malls
                    .stream()
                    .flatMap(mall -> mall.getParkingSpaces().stream())
                    .filter(parkingSpace -> parkingSpace.getLatitude() == location.latitude &&
                            parkingSpace.getLongitude() == location.longitude)
                    .findFirst()
                    .orElse(null);
            if(carParkingSpace != null) {
                removeParkingSpaceMarker(carParkingSpace);
                carMarker = createCarMarker(carParkingSpace);
            }
        }
    }

    private void createCarMarkerFromCache() {
        String carLat = MainActivity.sharedPref.getString("carLat", null);
        String carLng = MainActivity.sharedPref.getString("carLng", null);
        if(carLat != null && carLng != null) {
            carLocation = new LatLng(Double.parseDouble(carLat), Double.parseDouble(carLng));
            ParkingSpace carParkingSpace = malls
                    .stream()
                    .flatMap(mall -> mall.getParkingSpaces().stream())
                    .filter(parkingSpace -> parkingSpace.getLatitude() == carLocation.latitude &&
                            parkingSpace.getLongitude() == carLocation.longitude)
                    .findFirst()
                    .orElse(null);
            occupiedByCarParkingSpace = carParkingSpace;
            removeParkingSpaceMarker(carParkingSpace);
            carMarker = createCarMarker(carParkingSpace);
        }
    }

    private void saveCarLocationInCache(LatLng location) {
        SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
        editor.putString("carLat", Double.toString(location.latitude));
        editor.putString("carLng", Double.toString(location.longitude));
        editor.apply();
    }

    private void deleteCarLocationFromCache() {
        SharedPreferences.Editor editor = MainActivity.sharedPref.edit();
        editor.remove("carLat");
        editor.remove("carLng");
        editor.apply();
    }

    private void removeParkingSpaceMarker(ParkingSpace parkingSpace) {
        if(parkingSpace != null && parkingSpacesMarkers.get(parkingSpace.getId()) != null)
            parkingSpacesMarkers.get(parkingSpace.getId()).remove();
    }

    private void setupMapZone(MapZone mapZone) {
        googleMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(mapZone.getCoordinates(), mapZone.getZoom()));
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void updateParkingSpaces(List<Mall> malls) {
        this.malls = malls;
        if(storeMarkersLoaded) {
            parkingSpacesMarkers.forEach((id, marker) -> marker.remove());
            parkingSpacesMarkers.clear();
            setupParkingSpaces();
            if(carMarker == null) {
                if (carLocation != null) {
                    createCarMarkerAtLocation(carLocation);
                } else {
                    createCarMarkerFromCache();
                }
            }
        }
    }
}
