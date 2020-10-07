package com.beia.solomon.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.beia.solomon.GsonRequest;
import com.beia.solomon.R;
import com.beia.solomon.adapters.ViewPagerAdapter;
import com.beia.solomon.model.BeaconLocalizationData;
import com.beia.solomon.model.Location;
import com.beia.solomon.model.Point;
import com.beia.solomon.fragments.MapFragment;
import com.beia.solomon.fragments.SettingsFragment;
import com.beia.solomon.fragments.StoreAdvertisementFragment;
import com.beia.solomon.model.Beacon;
import com.beia.solomon.model.Campaign;
import com.beia.solomon.model.Mall;
import com.beia.solomon.model.ProximityStatus;
import com.beia.solomon.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.device.BeaconRegion;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.spec.EddystoneFrameType;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public RequestQueue volleyQueue;
    public Gson gson;
    public static User user;
    public String password;

    public List<Mall> malls;
    public List<Beacon> beacons;
    public List<Campaign> campaigns;
    public Mall currentMall;

    public Map<String, Marker> beaconMarkers;
    public Map<Long, Marker> parkingSpacesMarkers;


    public static int displayWidth;
    public static int displayHeight;

    public static final int roomDimension = 2;//meters
    public static volatile HashMap<String, Beacon> beaconsMap;//key:id value:beacon
    public static volatile HashMap<String, Beacon> beaconsMapByCompanyId;//key:id value:beacon
    public static volatile HashMap<String, Long> beaconsTimeMap;//key:id value:total beacon time spent by all users
    public static volatile HashMap<String, Boolean> regionsEntered;//key:idBeacon value:boolean
    public static volatile HashMap<String, Long> timeMap;//key:idBeacon value:current time ms when the user entered the beacon area
    public static volatile HashMap<Integer, Mall> mallsMap;//key:mallId value:mall
    public static volatile List<Location> userLocations;//contains all the user locations in the last 24h
    public static volatile HashMap<Integer, Boolean> mallsEntered;
    public static List<IndoorLevel> levels;
    public ArrayList<Boolean> levelsActivated;
    public HashSet<IBeaconDevice> ibeaconsSet;
    public IBeaconDevice[] closestBeacons;

    //LOCATION
    public Point[] closestBeaconsCoordinates;
    public Queue[] distancesQueues;
    public double[] beaconDistances;

    //ESTIMOTE
    public EstimoteCloudCredentials cloudCredentials;
    public ProximityObserver proximityObserver;
    public  ArrayList<ProximityZone> estimoteProximityZones;

    //KONTAKT
    public ProximityManager proximityManager;

    public static volatile boolean active = false;

    //COMMUNICATION
    public static volatile Socket socket;
    public static volatile ObjectOutputStream objectOutputStream;
    public static volatile ObjectInputStream objectInputStream;

    //CACHE
    public static SharedPreferences sharedPref;
    public static LruCache<String, Bitmap> picturesCache;

    //GOOGLE MAP
    public static volatile Queue<Marker> positionMarkers;
    public IndoorBuilding indoorBuilding;

    //UI
    public static TabLayout tabLayout;
    public ViewPager viewPager;
    public static ViewPagerAdapter viewPagerAdapter;
    public LinearLayout mainActivityLinearLayout;

    //USER PROFILE
    public TextView usernameTextView;
    public TextView passwordTextView;
    public TextView ageTextView;

    //FRAGMENTS
    public static StoreAdvertisementFragment storeAdvertisementFragment;
    public static MapFragment mapFragment;
    public static SettingsFragment settingsFragment;

    public static Date currentTime;
    public static Context context;
    public static MainActivity mainActivity;

    //NOTIFICATIONS BACKGROUND PROCESS
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    public static boolean notification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        volleyQueue = Volley.newRequestQueue(this);
        gson = new Gson();

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        user = gson.fromJson(sharedPref.getString("user", null), User.class);
        password = sharedPref.getString("password", null);

        if(user == null || password == null) {
            user = (User) getIntent().getSerializableExtra("user");
            password = getIntent().getStringExtra("password");
            if(user == null || password == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("user", gson.toJson(user));
                editor.putString("password", password);
                editor.apply();
            }
        }
        else {
            initData();
            getMalls();
        }
    }

    public void getMalls() {
        String url = getResources().getString(R.string.getMallsUrl);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<List> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                List.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "malls");
                    malls = gson.fromJson(gson.toJson(response), new TypeToken<List<Mall>>(){}.getType());
                    malls.forEach(mall -> Log.d("Mall", mall.toString()));
                    getBeacons();
                },
                error -> {
                    if(error.networkResponse != null && error.networkResponse.data != null)
                        Log.d("ERROR", "getMalls: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                    getMalls();
                });

        volleyQueue.add(request);
    }

    public void getBeacons() {
        String url = getResources().getString(R.string.getBeaconsUrl);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<List> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                List.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "beacons");
                    this.beacons = gson.fromJson(gson.toJson(response), new TypeToken<List<Beacon>>(){}.getType());
                    beacons.forEach(beacon -> Log.d("Beacon", beacon.toString()));
                    initUI();
                    initEstimoteBeacons();
                    initKontaktBeacons();
                },
                error -> {
                    if(error.networkResponse != null && error.networkResponse.data != null)
                        Log.d("ERROR", "getBeacons: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                    getBeacons();
                });

        volleyQueue.add(request);
    }

    public void postBeaconTime(Beacon beacon, long seconds) {
        String url = getResources().getString(R.string.postBeaconTimeUrl) +
                "?user_id=" + user.getId() +
                "&beacon_id=" + beacon.getId();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Object> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                seconds,
                Object.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "SAVED BEACON TIME");
                },
                error -> {
                    if(error.networkResponse != null && error.networkResponse.data != null)
                        Log.d("ERROR", "postBeaconTime: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }

    public void requestLocalization(BeaconLocalizationData beaconLocalizationData) {
        String url = getResources().getString(R.string.computeLocationUrl) + "/" + user.getId();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<Location> request = new GsonRequest<>(
                Request.Method.POST,
                url,
                beaconLocalizationData,
                Location.class,
                headers,
                response -> {
                    Log.d("RESPONSE", "Location:" + response);
                    LatLng coordinates = new LatLng(response.getLatitude(), response.getLongitude());
                    setUserPosition(coordinates);
                    closestBeaconsCoordinates = new Point[4];
                },
                error -> {
                    if(error.networkResponse != null && error.networkResponse.data != null)
                        Log.d("ERROR", "requestLocalization: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }

    public void requestCampaigns(long companyId) {
        String url = getResources().getString(R.string.getCampaignsUrl) +
                "?companyId=" + companyId;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        headers.put("Authorization", getResources().getString(R.string.universal_user));

        GsonRequest<List> request = new GsonRequest<>(
                Request.Method.GET,
                url,
                null,
                List.class,
                headers,
                response -> {
                    this.campaigns = gson.fromJson(gson.toJson(response), new TypeToken<List<Campaign>>(){}.getType());
                    campaigns.forEach(campaign -> Log.d("Campaign", campaign.toString()));
                    storeAdvertisementFragment.refreshCampaigns(campaigns);
                },
                error -> {
                    if(error.networkResponse != null && error.networkResponse.data != null)
                        Log.d("ERROR", "requestCampaigns: " + new String(error.networkResponse.data));
                    else
                        error.printStackTrace();
                });

        volleyQueue.add(request);
    }

    public void initData() {
        //init variables
        context = getApplicationContext();
        mainActivity = this;
        currentTime = Calendar.getInstance().getTime();
        MainActivity.active = true;
        beaconsMap = new HashMap<>();
        beaconsMapByCompanyId = new HashMap<>();
        regionsEntered = new HashMap<>();
        timeMap = new HashMap<>();
        malls = new ArrayList<>();
        levels = new ArrayList<>();
        levelsActivated = new ArrayList<>();
        ibeaconsSet = new HashSet<>();
        closestBeacons = new IBeaconDevice[4];
        closestBeaconsCoordinates = new Point[4];
        distancesQueues = new Queue[4];//a distance queue for each beacon in the room
        beaconDistances = new double[4];//the distances after the mean
        positionMarkers = new LinkedList<>();
        beaconMarkers = new HashMap<>();
        parkingSpacesMarkers = new HashMap<>();
    }

    @SuppressLint("ResourceAsColor")
    public void initUI()
    {
        tabLayout = findViewById(R.id.tabLayoutId);
        viewPager = findViewById(R.id.viewPagerId);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mainActivityLinearLayout = findViewById(R.id.mainActivityLinearLayout);

        storeAdvertisementFragment = new StoreAdvertisementFragment(campaigns, user);
        Bundle bundle1 = new Bundle();
        ArrayList<String> storeAdvertisementsData = new ArrayList<>();
        bundle1.putStringArrayList("storeAdvertisementsData", storeAdvertisementsData);
        storeAdvertisementFragment.setArguments(bundle1);

        mapFragment = new MapFragment(beaconMarkers, parkingSpacesMarkers);
        Bundle bundle2 = new Bundle();
        ArrayList<String> beaconsData = new ArrayList<>();
        beaconsData.add(gson.toJson(beacons));
        beaconsData.add(gson.toJson(malls));
        bundle2.putStringArrayList("mapData", beaconsData);
        mapFragment.setArguments(bundle2);

        settingsFragment = new SettingsFragment();
        Bundle bundle3 = new Bundle();
        ArrayList<String> settingsFragmentData = new ArrayList<>();
        settingsFragmentData.add(gson.toJson(user));
        settingsFragmentData.add(password);
        settingsFragmentData.add(gson.toJson(malls));
        bundle3.putStringArrayList("settingsData", settingsFragmentData);
        settingsFragment.setArguments(bundle3);

        //add the fragment to the viewPagerAdapter
        int numberOfTabs = 3;
        viewPagerAdapter.addFragment(storeAdvertisementFragment, "storeAdvertisementsData");
        viewPagerAdapter.addFragment(mapFragment, "userStatsData");
        viewPagerAdapter.addFragment(settingsFragment, "profileDataAndSettingsData");

        //set my ViewPagerAdapter to the ViewPager
        viewPager.setAdapter(viewPagerAdapter);
        //set the tabLayoutViewPager
        tabLayout.setupWithViewPager(viewPager);

        //set images instead of title text for each tab
        tabLayout.getTabAt(0).setIcon(R.drawable.store_ads_icon).setText("Campaigns");
        tabLayout.getTabAt(1).setIcon(R.drawable.stats_icon).setText("Map");
        tabLayout.getTabAt(2).setIcon(R.drawable.settings_icon).setText("Menu");

        //select the initial tab that the user sees
        if(!MainActivity.notification)
            tabLayout.getTabAt(1).select();
        else
            tabLayout.getTabAt(0).select();

        //set the user profile UFI variables
        usernameTextView = findViewById(R.id.usernameTextView);
        passwordTextView = findViewById(R.id.passwordTexView);
        ageTextView = findViewById(R.id.ageTextView);
        Log.d("BEACONS", "LAYOUT");
    }

    public void initKontaktBeacons()
    {
        Log.d("BEACONS", "initKontaktBeacons: ");
        //initialize the Kontakt SDK
        KontaktSDK.initialize(String.valueOf(R.string.kontakt_io_api_key));
        proximityManager = ProximityManagerFactory.create(this);


        //configure the proximity manager
        proximityManager.configuration()
                .scanMode(ScanMode.LOW_LATENCY)
                .scanPeriod(ScanPeriod.RANGING)
                .activityCheckConfiguration(ActivityCheckConfiguration.DEFAULT)
                .eddystoneFrameTypes(Arrays.asList(EddystoneFrameType.UID, EddystoneFrameType.URL));


        //configure the regions
        final Collection<IBeaconRegion> beaconRegions = new ArrayList<>();
        beacons.stream()
                .filter(beacon -> beacon.getManufacturer().equals("KONTAKT"))
                .forEach(beacon -> beaconRegions.add(new BeaconRegion.Builder()
                        .identifier(beacon.getName())
                        .proximity(UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"))
                        .major(beacon.getMajor())
                        .minor(beacon.getMinor())
                        .build()));

        //manage the regions
        proximityManager.spaces().iBeaconRegions(beaconRegions);

        proximityManager.setIBeaconListener(new IBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice iBeacon, IBeaconRegion region) {
                Beacon beacon = beacons
                        .stream()
                        .filter(b -> b.getManufacturerId().equals(iBeacon.getUniqueId()))
                        .findFirst()
                        .orElse(null);
                //MALL CHANGED
                if(beacon != null && !beacon.getMall().equals(currentMall)) {
                    currentMall = beacon.getMall();
                    Log.d("MALL", currentMall.getLatitude() + " " + currentMall.getLongitude());
                    LatLng mallCoordinates = new LatLng(currentMall.getLatitude(),
                            currentMall.getLongitude());
                    if(mapFragment.getGoogleMap() != null)
                        mapFragment
                                .getGoogleMap()
                                .moveCamera(CameraUpdateFactory.newLatLngZoom(mallCoordinates, 18.0f));
                }
            }

            //DETECTED BEACONS
            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region){
                int radius = 6371000;//Earth radius in meters
                for(IBeaconDevice iBeaconDevice : iBeacons) {

                    Beacon beacon = beacons
                            .stream()
                            .filter(b -> b.getManufacturerId().equals(iBeaconDevice.getUniqueId()))
                            .findFirst()
                            .orElse(null);

                    if (beacon != null) {

                        double distance = iBeaconDevice.getDistance();
                        double x = getXCoordinate(beacon.getLatitude(), beacon.getLongitude(), radius);
                        double y = getYCoordinate(beacon.getLatitude(), beacon.getLongitude(), radius);
                        double z = getZCoordinate(beacon.getLatitude(), beacon.getLongitude(), radius);

                        switch (beacon.getName()) {
                            case "Emag":
                                closestBeacons[0] = iBeaconDevice;
                                if (distancesQueues[0] == null)
                                    distancesQueues[0] = new LinkedList<Double>();
                                distancesQueues[0].add(iBeaconDevice.getDistance());
                                closestBeaconsCoordinates[0] = new Point(x, y, z);
                                break;
                            case "Nike":
                                closestBeacons[1] = iBeaconDevice;
                                if (distancesQueues[1] == null)
                                    distancesQueues[1] = new LinkedList();
                                distancesQueues[1].add(iBeaconDevice.getDistance());
                                closestBeaconsCoordinates[1] = new Point(x, y, z);
                                break;
                            case "Taco Bell":
                                closestBeacons[2] = iBeaconDevice;
                                if (distancesQueues[2] == null)
                                    distancesQueues[2] = new LinkedList();
                                distancesQueues[2].add(iBeaconDevice.getDistance());
                                closestBeaconsCoordinates[2] = new Point(x, y, z);
                                break;
                            case "Bershka":
                                closestBeacons[3] = iBeaconDevice;
                                if (distancesQueues[3] == null)
                                    distancesQueues[3] = new LinkedList();
                                distancesQueues[3].add(iBeaconDevice.getDistance());
                                closestBeaconsCoordinates[3] = new Point(x, y, z);
                                break;
                            case "McDonald's":
                                closestBeacons[0] = iBeaconDevice;
                                if (distancesQueues[0] == null)
                                    distancesQueues[0] = new LinkedList<Double>();
                                distancesQueues[0].add(iBeaconDevice.getDistance());
                                closestBeaconsCoordinates[0] = new Point(x, y, z);
                                break;
                            case "Altex":
                                closestBeacons[1] = iBeaconDevice;
                                if (distancesQueues[1] == null)
                                    distancesQueues[1] = new LinkedList();
                                distancesQueues[1].add(iBeaconDevice.getDistance());
                                closestBeaconsCoordinates[1] = new Point(x, y, z);
                                break;
                            case "Zara":
                                closestBeacons[2] = iBeaconDevice;
                                if (distancesQueues[2] == null)
                                    distancesQueues[2] = new LinkedList();
                                distancesQueues[2].add(iBeaconDevice.getDistance());
                                closestBeaconsCoordinates[2] = new Point(x, y, z);
                                break;
                            case "Starbucks":
                                closestBeacons[3] = iBeaconDevice;
                                if (distancesQueues[3] == null)
                                    distancesQueues[3] = new LinkedList();
                                distancesQueues[3].add(iBeaconDevice.getDistance());
                                closestBeaconsCoordinates[3] = new Point(x, y, z);
                                break;
                            default:
                                break;
                        }

                        if (regionsEntered.containsKey(iBeaconDevice.getUniqueId())) {
                            boolean inZone = regionsEntered.get(iBeaconDevice.getUniqueId());
                            if (!inZone) {
                                if (distance < roomDimension * 0.5) {
                                    regionsEntered.put(iBeaconDevice.getUniqueId(), true);
                                    manageEnteredRegion(beacon, iBeaconDevice, region);
                                }
                            }
                            else {
                                if (distance > roomDimension) {
                                    regionsEntered.put(iBeaconDevice.getUniqueId(), false);
                                    manageLeftRegion(beacon, iBeaconDevice, region);
                                }
                            }
                        }
                        else {
                            if (distance < roomDimension * 0.5) {
                                regionsEntered.put(iBeaconDevice.getUniqueId(), true);
                                manageEnteredRegion(beacon, iBeaconDevice, region);
                            }
                        }
                    }

                    //INDOOR POSITION
                    boolean roomDetected = true;
                    for (Point beaconLocation : closestBeaconsCoordinates)
                        if (beaconLocation == null) {
                            roomDetected = false;
                            break;
                        }
                    if (roomDetected) {
                        boolean enoughValues = true;
                        for (int i = 0; i < distancesQueues.length; i++) {
                            if (distancesQueues[i].size() < 1) {
                                enoughValues = false;
                                break;
                            }
                            else {
                                Queue<Double> distances = distancesQueues[i];
                                double meanDistance = 0, distancesNr = 0;
                                distancesNr = distances.size();
                                while (!distances.isEmpty())
                                    meanDistance += distances.poll();
                                meanDistance /= distancesNr;
                                beaconDistances[i] = meanDistance;
                            }
                        }

                        //COMPUTE LOCATION
                        if (enoughValues) {
                            Log.d("POSITION", "onIBeaconsUpdated: ");
                            requestLocalization(new BeaconLocalizationData(closestBeaconsCoordinates, beaconDistances));
                        }
                    }
                }
            }

            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
            }
        });
        startScanning();
    }

    private void manageEnteredRegion(Beacon beacon, IBeaconDevice iBeaconDevice, IBeaconRegion region) {
        indoorBuilding = mapFragment.getGoogleMap().getFocusedBuilding();
        List<IndoorLevel> levels = indoorBuilding != null ? indoorBuilding.getLevels() : null;
        int currentActiveFloor = levels != null ? levels.size() - indoorBuilding.getActiveLevelIndex() : 0;
        if(indoorBuilding != null && beacon.getFloor() != currentActiveFloor) {
            levels.get(levels.size() - beacon.getFloor()).activate();
        }

        requestCampaigns(beacon.getUser().getId());
        highlightBeaconMarker(beacon, ProximityStatus.CLOSE);

        timeMap.put(iBeaconDevice.getUniqueId(), System.currentTimeMillis());
        Toast.makeText(getApplicationContext(), "Entered region: " + region.getIdentifier(), Toast.LENGTH_SHORT).show();
    }

    private void manageLeftRegion(Beacon beacon, IBeaconDevice iBeaconDevice, IBeaconRegion region) {
        long timeSeconds = (System.currentTimeMillis() - timeMap.get(iBeaconDevice.getUniqueId())) / 1000;
        highlightBeaconMarker(beacon, ProximityStatus.FAR);
        postBeaconTime(beacon, timeSeconds);
        Toast.makeText(getApplicationContext(), "Left region: " + region.getIdentifier(), Toast.LENGTH_SHORT).show();
    }

    private void highlightBeaconMarker(Beacon beacon, ProximityStatus proximityStatus) {
        if(mapFragment.getGoogleMap().getCameraPosition().zoom > 18) {
            if (beaconMarkers.containsKey(beacon.getManufacturerId())) {
                beaconMarkers.get(beacon.getManufacturerId()).remove();
                beaconMarkers.remove(beacon.getManufacturerId());
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
                                Marker marker = mapFragment.getGoogleMap().addMarker(new MarkerOptions().position(storeLocation)
                                        .icon(BitmapDescriptorFactory
                                                .fromBitmap(mapFragment.createStoreMarker(MainActivity.context,
                                                        resource, proximityStatus))));
                                marker.setTitle(beacon.getName());
                                beaconMarkers.put(beacon.getManufacturerId(), marker);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        }
    }

    private void setUserPosition(LatLng coordinates) {
        if(mapFragment.getGoogleMap() != null && mapFragment.getGoogleMap().getCameraPosition().zoom > 18) {
            Marker positionMarker = mapFragment
                    .getGoogleMap()
                    .addMarker(new MarkerOptions()
                            .position(coordinates)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            positionMarkers.add(positionMarker);
            if (positionMarkers.size() > 1) {
                positionMarkers.poll().remove();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(proximityManager != null) {
            //restart scanning for the Kontakt beacons
            startScanning();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(proximityManager != null) {
            startScanning();
        }
    }

    @Override
    protected void onStop() {
        if(proximityManager != null) {
            proximityManager.stopScanning();
        }
        super.onStop();
        active = false;
    }

    @Override
    protected void onDestroy() {
        if(proximityManager != null) {
            proximityManager.stopScanning();
            proximityManager.disconnect();
            proximityManager = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    protected void startScanning() {
        proximityManager.connect(() -> {
            proximityManager.startScanning();
            Toast.makeText(context, "scanning for beacons...", Toast.LENGTH_SHORT).show();
        });
    }

    public double getXCoordinate(double lat, double lon, int radius) {
        return radius * Math.cos(lon * 2 * Math.PI / 360) * Math.cos(lat * 2 * Math.PI / 360);//cos because the latitude starts from 90 degrees instead of 0
    }
    public double getYCoordinate(double lat, double lon, int radius) {
        return radius * Math.cos(lat * 2 * Math.PI / 360) * Math.sin(lon * 2 * Math.PI / 360);
    }
    public double getZCoordinate(double lat, double lon, int radius) {
        return radius * Math.sin(lat * 2 * Math.PI / 360);
    }
    public double getLatitude(double x, double y, double z) {
        return 360 / (2 * Math.PI) * Math.asin(z / 6371000);
    }
    public double getLongitude(double x, double y, double z) {
        return 360 / (2 * Math.PI) * Math.atan2(y, x);
    }

    public void initEstimoteBeacons()
    {
        /*
        //initialized cloud credentials
        cloudCredentials = new EstimoteCloudCredentials("solomon-app-ge4", "97f78b20306bb6a15ed1ddcd24b9ca21");

        //instantiated the proximity observer
        proximityObserver = new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "proximity observer error: " + throwable);
                        //feedBackTextView.setText("proximity error");
                        return null;
                    }
                })
                .withBalancedPowerMode()
                .build();

        //create the proximity zones based on the beacons
        estimoteProximityZones = new ArrayList<>();
        for(Beacon beacon : beaconsMap.values())
        {
            if(beacon instanceof EstimoteBeacon)
            {
                //create a proximity zone based on a beacon
                final EstimoteBeacon estimoteBeacon = (EstimoteBeacon) beacon;
                final ProximityZone estimoteProximityZone = new ProximityZoneBuilder()
                        .forTag(estimoteBeacon.getLabel())
                        .inCustomRange(3.0)
                        .onEnter(new Function1<ProximityZoneContext, Unit>() {
                            @Override
                            public Unit invoke(ProximityZoneContext context) {
                                //get current time
                                currentTime = Calendar.getInstance().getTime();
                                //LOCATION DATA
                                timeMap.put(estimoteBeacon.getId(), System.currentTimeMillis());
                                return null;
                            }
                        })
                        .onExit(new Function1<ProximityZoneContext, Unit>() {
                            @Override
                            public Unit invoke(ProximityZoneContext context) {
                                //get current time
                                currentTime = Calendar.getInstance().getTime();
                                //LOCATION DATA
                                long timeSeconds = (System.currentTimeMillis() - timeMap.get(estimoteBeacon.getId())) / 1000;
                                String requestString = "{\"requestType\":\"postBeaconTime\", \"idUser\":" + MainActivity.userData.getUserId() + ", \"idBeacon\":\"" + estimoteBeacon.getId() + "\", \"timeSeconds\":" + timeSeconds + "}";
                                new Thread(new PostRunnable(requestString, objectOutputStream)).start();
                                return null;
                            }
                        })
                        .build();

                //add the proximity zone to the proximity zones array
                estimoteProximityZones.add(estimoteProximityZone);
            }
        }

        //set bluetooth functionality for Estimote
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                for(ProximityZone proximityZone : estimoteProximityZones)
                                {
                                    proximityObserver.startObserving(proximityZone);
                                }

                                //feedBackTextView.setText("requirements fulfiled");
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                //feedBackTextView.setText("requirements missing");
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                //feedBackTextView.setText("requirements error");
                                return null;
                            }
                        });
         */
    }

    //NOTIFICATIONS
//    public void setupBackgroundProcess()
//    {
//        Intent alarmIntent = new Intent(this, NotificationReceiver.class);
//        alarmIntent.putExtra("idUser", userData.getUserId());
//        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
//        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
//        Log.d("ALARM", "started");
//    }
}