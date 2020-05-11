package com.beia.solomon.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beia.solomon.data.Point;
import com.beia.solomon.fragments.MapFragment;
import com.beia.solomon.R;
import com.beia.solomon.fragments.SettingsFragment;
import com.beia.solomon.fragments.StoreAdvertisementFragment;
import com.beia.solomon.adapters.ViewPagerAdapter;
import com.beia.solomon.networkPackets.Campaign;
import com.beia.solomon.networkPackets.Coordinates;
import com.beia.solomon.networkPackets.Mall;
import com.beia.solomon.receivers.NotificationReceiver;
import com.beia.solomon.runnables.ComputePositionRunnable;
import com.beia.solomon.runnables.PostRunnable;
import com.beia.solomon.runnables.RequestRunnable;
import com.beia.solomon.runnables.WaitForServerDataRunnable;
import com.bumptech.glide.Glide;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.beia.solomon.handlers.MainActivityHandler;
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.EstimoteBeacon;
import com.beia.solomon.networkPackets.KontaktBeacon;
import com.beia.solomon.networkPackets.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.BeaconRegion;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.spec.EddystoneFrameType;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import android.widget.Toast;

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
import java.util.Stack;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    //display variables
    public static int displayWidth;
    public static int displayHeight;

    //beacon variables
    public static final int roomDimension = 2;//meters
    public static volatile HashMap<String, Beacon> beaconsMap;//key:id value:beacon
    public static volatile HashMap<String, Beacon> beaconsMapByCompanyId;//key:id value:beacon
    public static volatile HashMap<String, Long> beaconsTimeMap;//key:id value:total beacon time spent by all users
    public static volatile HashMap<String, Boolean> regionsEntered;//key:idBeacon value:boolean
    public static volatile HashMap<String, Long> timeMap;//key:idBeacon value:current time ms when the user entered the beacon area
    public static volatile HashMap<Integer, Mall> mallsMap;//key:mallId value:mall
    public static volatile ArrayList<Mall> malls;
    public static volatile HashMap<Integer, Boolean> mallsEntered;
    public static volatile ArrayList<Campaign> campaigns;
    public static List<IndoorLevel> levels;
    public ArrayList<Boolean> levelsActivated;
    public HashSet<IBeaconDevice> ibeaconsSet;
    public IBeaconDevice[] closestBeacons;
    public Point[] closestBeaconsCoordinates;
    public Queue[] distancesQueues;
    public double[] beaconDistances;
    //Estimote variables
    public EstimoteCloudCredentials cloudCredentials;
    public ProximityObserver proximityObserver;
    public  ArrayList<ProximityZone> estimoteProximityZones;
    //Kontakt variables
    public ProximityManager proximityManager;


    public static volatile boolean active = false;

    //Communication variables
    public static volatile Socket socket;
    public static volatile ObjectOutputStream objectOutputStream;
    public static volatile ObjectInputStream objectInputStream;

    //cache variables
    public static SharedPreferences sharedPref;
    public static LruCache<String, Bitmap> picturesCache;

    //google map variables
    public static volatile Queue<Marker> positionMarkers;
    public IndoorBuilding indoorBuilding;


    //Handlers
    public static MainActivityHandler mainActivityHandler;
    public static boolean beaconsReceived = false;

    //UI variables
    //Main activity UI variables
    public static TabLayout tabLayout;
    public ViewPager viewPager;
    public static ViewPagerAdapter viewPagerAdapter;
    public LinearLayout mainActivityLinearLayout;
    //user profile UI variables
    public TextView usernameTextView;
    public TextView passwordTextView;
    public TextView ageTextView;


    //fragments
    public static StoreAdvertisementFragment storeAdvertisementFragment;
    public static MapFragment mapFragment;
    public static SettingsFragment settingsFragment;

    //Other variables
    public static Date currentTime;
    public static volatile UserData userData;
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

        //get the screen dimensions
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        //init variables
        context = getApplicationContext();
        mainActivity = this;
        currentTime = Calendar.getInstance().getTime();
        MainActivity.active = true;
        beaconsMap = new HashMap<>();
        beaconsMapByCompanyId = new HashMap<>();
        regionsEntered = new HashMap<>();
        timeMap = new HashMap<>();
        mallsMap = new HashMap<>();
        malls = new ArrayList<>();
        mallsEntered = new HashMap<>();
        levels = new ArrayList<>();
        levelsActivated = new ArrayList<>();
        ibeaconsSet = new HashSet<>();
        closestBeacons = new IBeaconDevice[4];
        closestBeaconsCoordinates = new Point[4];
        distancesQueues = new Queue[4];//a distance queue for each beacon in the room
        beaconDistances = new double[4];//the distances after the mean
        positionMarkers = new LinkedList<>();
        campaigns = new ArrayList<>();

        //create handler
        mainActivityHandler = new MainActivityHandler(this);

        //initialize the cache
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        //getUserData
        MainActivity.userData = (UserData) getIntent().getSerializableExtra("UserData");

        //check if the activity started from a notification
        notification = getIntent().getBooleanExtra("notification", false);

        if(userData != null)//NORMAL LOGIN
        {
            //start the notifications background process
            setupBackgroundProcess();

            //save the data in the cache
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("username", userData.getUsername());
            editor.putString("password", userData.getPassword());
            editor.apply();

            //get the communication variables from the login activity
            MainActivity.objectOutputStream = LoginActivity.objectOutputStream;
            MainActivity.objectInputStream = LoginActivity.objectInputStream;

            //the data can be received before the main activity was created
            //if it's not the code below will be executed in the main activity handler
            if(beaconsReceived)
            {
                //initialize all the malls and set all the malls entered values to false
                for (Map.Entry entry : MainActivity.beaconsMap.entrySet())
                {
                    Beacon beacon = (Beacon) entry.getValue();
                    if(MainActivity.mallsEntered.isEmpty())
                    {
                        MainActivity.mallsEntered.put(beacon.getMallId(), false);
                    }
                    else
                    {
                        if(MainActivity.mallsEntered.containsKey(beacon.getMallId()) == false)
                        {
                            MainActivity.mallsEntered.put(beacon.getMallId(), false);
                        }
                    }
                }
                // set Kontakt beacons
                initKontaktBeacons();
            }
        }
        else//AUTOMATIC LOGIN
        {
            MainActivity.userData = new UserData(sharedPref.getString("username", null), sharedPref.getString("password", null));
            if(userData.getUsername() != null && userData.getPassword() != null)//SUCCESSFUL AUTOMATIC LOGIN
                new Thread(new WaitForServerDataRunnable("MainActivity")).start();
            else { //AUTOMATIC LOGIN FAILED
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                active = false;
                finish();
            }
        }

        //create a local cache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 16;
        MainActivity.picturesCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        initUI();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }


    //NOTIFICATIONS
    public void setupBackgroundProcess()
    {
        Intent alarmIntent = new Intent(this, NotificationReceiver.class);
        alarmIntent.putExtra("idUser", userData.getUserId());
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
        Log.d("ALARM", "started");
    }



    //ESTIMOTE
    public void initEstimoteBeacons()
    {
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
    }



    //KONTAKT
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
        for(Beacon beacon: beaconsMap.values())
        {
            if(beacon instanceof KontaktBeacon)
            {
                Log.d("BEACONS", "initKontaktBeacons: " + beacon.getLabel());
                KontaktBeacon kontaktBeacon = (KontaktBeacon) beacon;
                IBeaconRegion kontaktRegion = new BeaconRegion.Builder()
                        .identifier(kontaktBeacon.getLabel())
                        .proximity(UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"))
                        .major(Integer.parseInt(kontaktBeacon.getMajor()))
                        .minor(Integer.parseInt(kontaktBeacon.getMinor()))
                        .build();
                beaconRegions.add(kontaktRegion);
            }
        }

        //manage the regions
        proximityManager.spaces().iBeaconRegions(beaconRegions);

        proximityManager.setIBeaconListener(new IBeaconListener()
        {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice iBeacon, IBeaconRegion region)
            {
                //CHECK IF THE MALL CHANGED
                if(MainActivity.mallsEntered.get(MainActivity.beaconsMap.get(iBeacon.getUniqueId()).getMallId()) == false)
                {
                    //update the map based on the beacon mallId
                    Mall mall = MainActivity.mallsMap.get(MainActivity.beaconsMap.get(iBeacon.getUniqueId()).getMallId());
                    Log.d("MALL", mall.getMallCoordinates().getLatitude() + " " + mall.getMallCoordinates().getLongitude());
                    LatLng mallCoordinates = new LatLng(mall.getMallCoordinates().getLatitude(), mall.getMallCoordinates().getLongitude());
                    mapFragment.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mallCoordinates, 18.0f));
                    //get all the levels from the store(the floors)
                    indoorBuilding = mapFragment.googleMap.getFocusedBuilding();

                    /*
                    while(indoorBuilding == null)
                    {
                        indoorBuilding = mapFragment.googleMap.getFocusedBuilding();
                        Log.d("NO FOCUS", "onIBeaconDiscovered: ");
                    }
                    */

                    if(indoorBuilding == null)
                        Log.d("NULL BUILDING", "onIBeaconDiscovered: ");

                    //SET THE INDOOR LEVELS
                    if(indoorBuilding != null)
                    {
                        List<IndoorLevel> levels_aux = indoorBuilding.getLevels();
                        //spin the array because it has the upper levels at the begining
                        Stack<IndoorLevel> stack = new Stack<>();
                        for(IndoorLevel indoorLevel : levels_aux)
                            stack.push(indoorLevel);
                        while(!stack.empty())
                            levels.add(stack.pop());
                        //set all the levels to false(initially we are not in a close range from a beacon so we don't know in which level we're in)
                        for(IndoorLevel indoorLevel : levels)
                        {
                            levelsActivated.add(false);
                        }
                    }

                    //the user just entered the mall we change the map and we make all the other values in the mallEntered map as false
                    KontaktBeacon kontaktBeacon = (KontaktBeacon) MainActivity.beaconsMap.get(iBeacon.getUniqueId());
                    mallsEntered.put(kontaktBeacon.getMallId(),true);
                    for(Integer mallId : mallsEntered.keySet())
                    {
                        if(mallId != mall.getMallId())
                        {
                            mallsEntered.put(mallId, false);
                        }
                    }
                }
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region)
            {
                int radius = 6371000;//Earth radius in meters
                //check if a user entered a zone and record the time spent in the zone(used for analitics and stuff)
                for(IBeaconDevice iBeaconDevice : iBeacons)
                {
                    KontaktBeacon kontaktBeacon = (KontaktBeacon) MainActivity.beaconsMap.get(iBeaconDevice.getUniqueId());
                    double distance = iBeaconDevice.getDistance();
                    double x = getXCoordinate(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude(), radius);
                    double y = getYCoordinate(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude(), radius);
                    double z = getZCoordinate(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude(), radius);
                    //Log.d("LABEL: " + kontaktBeacon.getLabel(), "distance:" + distance + "x:" + x + " y:" + y + " z: " + z);

                    switch (kontaktBeacon.getLabel()) {
                        case "McDonald's":
                            closestBeacons[0] = iBeaconDevice;
                            if(distancesQueues[0] == null)
                                distancesQueues[0] = new LinkedList<Double>();
                            distancesQueues[0].add(iBeaconDevice.getDistance());
                            closestBeaconsCoordinates[0] = new Point(x, y, z);
                            break;
                        case "Altex":
                            closestBeacons[1] = iBeaconDevice;
                            if(distancesQueues[1] == null)
                                distancesQueues[1] = new LinkedList();
                            distancesQueues[1].add(iBeaconDevice.getDistance());
                            closestBeaconsCoordinates[1] = new Point(x, y, z);
                        break;
                        case "Zara":
                            closestBeacons[2] = iBeaconDevice;
                            if(distancesQueues[2] == null)
                                distancesQueues[2] = new LinkedList();
                            distancesQueues[2].add(iBeaconDevice.getDistance());
                            closestBeaconsCoordinates[2] = new Point(x, y, z);
                            break;
                        case "Starbucks":
                            closestBeacons[3] = iBeaconDevice;
                            if(distancesQueues[3] == null)
                                distancesQueues[3] = new LinkedList();
                            distancesQueues[3].add(iBeaconDevice.getDistance());
                            closestBeaconsCoordinates[3] = new Point(x, y, z);
                            break;
                        default:
                            break;
                    }

                    /*
                    //send the distance to the server
                    String distanceDataRequest = "{\"requestType\":\"saveDistance\", \"distance\":" + distance + ", \"idBeacon\":\"" + kontaktBeacon.getId() + "\"}";
                    new Thread(new RequestRunnable(distanceDataRequest, objectOutputStream)).start();
                     */

                    //check if a user entered a region
                    if(regionsEntered.isEmpty())
                    {
                        if(distance < roomDimension * 0.5)
                        {
                            //check if the map loaded and initialize variables
                            if(indoorBuilding == null)
                            {
                                indoorBuilding = mapFragment.googleMap.getFocusedBuilding();
                                if(indoorBuilding != null)
                                {
                                    List<IndoorLevel> levels_aux = indoorBuilding.getLevels();
                                    //spin the array because it has the upper levels at the begining
                                    Stack<IndoorLevel> stack = new Stack<>();
                                    for(IndoorLevel indoorLevel : levels_aux)
                                        stack.push(indoorLevel);
                                    while(!stack.empty())
                                        levels.add(stack.pop());
                                    //set all the levels to false(initially we are not in a close range from a beacon so we don't know in which level we're in)
                                    for(IndoorLevel indoorLevel : levels)
                                    {
                                        levelsActivated.add(false);
                                    }
                                }
                            }
                            //change the map level(floor) to the current floor
                            if(levels != null && levels.isEmpty() == false) {
                                if (levelsActivated.get(kontaktBeacon.getFloor()) == false) {
                                    //checked if the floor was changed set the current floor to true in the lavels activated array and make all the others false
                                    levelsActivated.set(kontaktBeacon.getFloor(), true);
                                    for (int i = 0; i < levelsActivated.size(); i++) {
                                        if (i != kontaktBeacon.getFloor()) {
                                            levelsActivated.set(i, false);
                                        }
                                    }
                                    //set the new indoor map level
                                    levels.get(kontaktBeacon.getFloor()).activate();
                                }
                            }

                            //request campaigns
                            String campaignsRequest = "{\"requestType\":\"getCampaigns\",\"beaconId\":\"" + kontaktBeacon.getId() + "\"}";
                            new Thread(new RequestRunnable(campaignsRequest, objectOutputStream)).start();

                            //put the user on the map(not the exact location)
                            setUserPosition(new LatLng(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude()));


                            regionsEntered.put(iBeaconDevice.getUniqueId(), true);
                            Toast toast = Toast.makeText(getApplicationContext(), "Entered region: " + region.getIdentifier(), Toast.LENGTH_SHORT);
                            toast.show();
                            //LOCATION DATA
                            timeMap.put(iBeaconDevice.getUniqueId(), System.currentTimeMillis());
                        }
                    }
                    else
                    {
                        if(regionsEntered.containsKey(iBeaconDevice.getUniqueId()))
                        {
                            boolean inZone = regionsEntered.get(iBeaconDevice.getUniqueId());
                            if(!inZone)
                            {
                                //user is inside the region
                                if(distance < roomDimension * 0.5)
                                {
                                    //check if the map loaded and initialize variables
                                    if(indoorBuilding == null)
                                    {
                                        indoorBuilding = mapFragment.googleMap.getFocusedBuilding();
                                        if(indoorBuilding != null)
                                        {
                                            List<IndoorLevel> levels_aux = indoorBuilding.getLevels();
                                            //spin the array because it has the upper levels at the begining
                                            Stack<IndoorLevel> stack = new Stack<>();
                                            for(IndoorLevel indoorLevel : levels_aux)
                                                stack.push(indoorLevel);
                                            while(!stack.empty())
                                                levels.add(stack.pop());
                                            //set all the levels to false(initially we are not in a close range from a beacon so we don't know in which level we're in)
                                            for(IndoorLevel indoorLevel : levels)
                                            {
                                                levelsActivated.add(false);
                                            }
                                        }
                                    }
                                    //change the map level(floor) to the current floor
                                    if(levels != null && levels.isEmpty() == false) {
                                        if (levelsActivated.get(kontaktBeacon.getFloor()) == false) {
                                            //checked if the floor was changed set the current floor to true in the lavels activated array and make all the others false
                                            levelsActivated.set(kontaktBeacon.getFloor(), true);
                                            for (int i = 0; i < levelsActivated.size(); i++) {
                                                if (i != kontaktBeacon.getFloor()) {
                                                    levelsActivated.set(i, false);
                                                }
                                            }
                                            //set the new indoor map level
                                            levels.get(kontaktBeacon.getFloor()).activate();
                                        }
                                    }

                                    //request campaigns
                                    String campaignsRequest = "{\"requestType\":\"getCampaigns\",\"beaconId\":\"" + kontaktBeacon.getId() + "\"}";
                                    new Thread(new RequestRunnable(campaignsRequest, objectOutputStream)).start();

                                    //put the user on the map(not the exact location)
                                    setUserPosition(new LatLng(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude()));

                                    regionsEntered.put(iBeaconDevice.getUniqueId(), true);
                                    //when the distance from the beacon is smaller than 5 metres and the user was outside the region the user entered the zone
                                    Toast toast = Toast.makeText(getApplicationContext(), "Entered region: " + region.getIdentifier(), Toast.LENGTH_SHORT);
                                    toast.show();
                                    //LOCATION DATA
                                    timeMap.put(iBeaconDevice.getUniqueId(), System.currentTimeMillis());
                                }
                            }
                            else
                            {
                                //user is outside the region
                                if(distance > roomDimension)
                                {
                                    regionsEntered.put(iBeaconDevice.getUniqueId(), false);
                                    //when the distance from the beacon is smaller than 5 metres and the user was outside the region the user entered the zone
                                    Toast toast = Toast.makeText(getApplicationContext(), "Left region: " + region.getIdentifier(), Toast.LENGTH_SHORT);
                                    toast.show();
                                    //LOCATION DATA
                                    long timeSeconds = (System.currentTimeMillis() - timeMap.get(iBeaconDevice.getUniqueId())) / 1000;
                                    String requestString = "{\"requestType\":\"postBeaconTime\", \"idUser\":" + MainActivity.userData.getUserId() + ", \"idBeacon\":\"" + iBeaconDevice.getUniqueId() + "\", \"timeSeconds\":" + timeSeconds + "}";
                                    new Thread(new PostRunnable(requestString, objectOutputStream)).start();
                                }
                            }
                        }
                        else
                        {
                            if(distance < roomDimension * 0.5)
                            {
                                //check if the map loaded and initialize variables
                                if(indoorBuilding == null)
                                {
                                    indoorBuilding = mapFragment.googleMap.getFocusedBuilding();
                                    if(indoorBuilding != null)
                                    {
                                        List<IndoorLevel> levels_aux = indoorBuilding.getLevels();
                                        //spin the array because it has the upper levels at the begining
                                        Stack<IndoorLevel> stack = new Stack<>();
                                        for(IndoorLevel indoorLevel : levels_aux)
                                            stack.push(indoorLevel);
                                        while(!stack.empty())
                                            levels.add(stack.pop());
                                        //set all the levels to false(initially we are not in a close range from a beacon so we don't know in which level we're in)
                                        for(IndoorLevel indoorLevel : levels)
                                        {
                                            levelsActivated.add(false);
                                        }
                                    }
                                }
                                //change the map level(floor) to the current floor
                                if(levels != null && levels.isEmpty() == false) {
                                    if (levelsActivated.get(kontaktBeacon.getFloor()) == false) {
                                        //checked if the floor was changed set the current floor to true in the lavels activated array and make all the others false
                                        levelsActivated.set(kontaktBeacon.getFloor(), true);
                                        for (int i = 0; i < levelsActivated.size(); i++) {
                                            if (i != kontaktBeacon.getFloor()) {
                                                levelsActivated.set(i, false);
                                            }
                                        }
                                        //set the new indoor map level
                                        levels.get(kontaktBeacon.getFloor()).activate();
                                    }
                                }

                                //request campaigns
                                String campaignsRequest = "{\"requestType\":\"getCampaigns\",\"beaconId\":\"" + kontaktBeacon.getId() + "\"}";
                                new Thread(new RequestRunnable(campaignsRequest, objectOutputStream)).start();

                                //put the user on the map(not the exact location)
                                setUserPosition(new LatLng(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude()));

                                regionsEntered.put(iBeaconDevice.getUniqueId(), true);
                                Toast toast = Toast.makeText(getApplicationContext(), "Entered region: " + region.getIdentifier(), Toast.LENGTH_SHORT);
                                toast.show();
                                //LOCATION DATA
                                timeMap.put(iBeaconDevice.getUniqueId(), System.currentTimeMillis());
                            }
                        }
                    }
                }

                //INDOOR POSITION
                //check if an area can be formed from the beacons and compute the position of the user

                boolean roomDetected = true;
                for(Point beaconLocation : closestBeaconsCoordinates)
                    if(beaconLocation == null) {
                        roomDetected = false;
                        break;
                    }
                if(roomDetected)
                {
                    //check if there are enough values in the distances queues
                    boolean enoughValues = true;
                    for(int i = 0; i < distancesQueues.length; i++) {//iterate through the distances queue of each beacon
                        if (distancesQueues[i].size() < 2) {
                            enoughValues = false;
                            break;
                        }
                        else {
                            LinkedList<Double> distances = (LinkedList<Double>)distancesQueues[i];
                            double meanDistance = 0, distancesNr = 0;
                            distancesNr = distances.size();
                            while(!distances.isEmpty())
                                meanDistance += distances.poll();
                            meanDistance /= distancesNr;
                            beaconDistances[i] = meanDistance;
                        }
                    }
                    if(enoughValues) {
                        new Thread(new ComputePositionRunnable(beaconDistances, closestBeaconsCoordinates)).start();
                    }
                }
            }
            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
            }
        });
        startScanning();
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
    protected void onResume()
    {
        super.onResume();
        if(proximityManager != null) {
            //restart scanning for the Kontakt beacons
            startScanning();
        }
    }

    @Override
    protected void onStop() {
        if(proximityManager != null) {
            proximityManager.stopScanning();
        }
        super.onStop();
        this.active = false;
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

    protected void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
                Toast.makeText(context, "scanning for beacons...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void initUI()
    {
        //get UI references
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutId);
        viewPager = (ViewPager) findViewById(R.id.viewPagerId);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mainActivityLinearLayout = findViewById(R.id.mainActivityLinearLayout);

        //set tabbed layout
        storeAdvertisementFragment = new StoreAdvertisementFragment(MainActivity.campaigns);
        Bundle bundle1 = new Bundle();
        ArrayList<String> storeAdvertisementsData = new ArrayList<>();
        bundle1.putStringArrayList("storeAdvertisementsData", storeAdvertisementsData);
        storeAdvertisementFragment.setArguments(bundle1, "storeAdvertisementsData");

        mapFragment = new MapFragment();
        Bundle bundle2 = new Bundle();
        ArrayList<String> userStatsData = new ArrayList<>();
        bundle2.putStringArrayList("userStatsData", userStatsData);
        mapFragment.setArguments(bundle2, "userStatsData");

        settingsFragment = new SettingsFragment();
        Bundle bundle3 = new Bundle();
        ArrayList<String> profileDataAndSettingsData = new ArrayList<>();
        bundle3.putStringArrayList("profileDataAndSettingsData", profileDataAndSettingsData);
        settingsFragment.setArguments(bundle3, "profileDataAndSettingsData");

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
        tabLayout.getTabAt(0).setIcon(R.drawable.store_ads_icon).setText("SPECIAL OFFERS");
        tabLayout.getTabAt(1).setIcon(R.drawable.stats_icon).setText("MAP");
        tabLayout.getTabAt(2).setIcon(R.drawable.settings_icon).setText("SETTINGS");

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

    public static Bitmap createStoreMarker(Context context, Bitmap bitmap) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.store_marker, null);

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

    public static void setUserPosition(LatLng latLng) {
        LatLng coordinates = new LatLng(latLng.latitude, latLng.longitude);
        MainActivity.mapFragment.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 20.0f));
        Marker positionMarker = MainActivity.mapFragment.googleMap.addMarker(new MarkerOptions().position(coordinates));
        MainActivity.positionMarkers.add(positionMarker);
        if(MainActivity.positionMarkers.size() > 1)
        {
            MainActivity.positionMarkers.poll().remove();//remove the head of the queue leaving only the new marker
        }
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
}