package com.beia.solomon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beia.solomon.comparators.DistanceComparator;
import com.beia.solomon.networkPackets.Mall;
import com.beia.solomon.trilateration.Point;
import com.beia.solomon.trilateration.Trilateration;
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
import com.beia.solomon.runnables.ReceiveBeaconsDataRunnable;
import com.beia.solomon.runnables.SendLocationDataRunnable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    //beacon variables
    public static volatile HashMap<String, Beacon> beacons;//change to public not static
    public static HashMap<String, Boolean> regionsEntered;
    public static HashMap<Integer, Mall> malls;
    public static HashMap<Integer, Boolean> mallsEntered;
    public static List<IndoorLevel> levels;
    public ArrayList<Boolean> levelsActivated;
    public HashSet<IBeaconDevice> ibeaconsSet;
    public IBeaconDevice[] closestBeacons;
    //Estimote variables
    public EstimoteCloudCredentials cloudCredentials;
    public ProximityObserver proximityObserver;
    public  ArrayList<ProximityZone> estimoteProximityZones;
    //Kontakt variables
    public ProximityManager proximityManager;

    //Communication variables
    public static volatile ObjectOutputStream objectOutputStream;
    public static volatile ObjectInputStream objectInputStream;

    //cache variables
    public static LruCache<String, Bitmap> picturesCache;

    //google map variables
    public Queue<Marker> positionMarkers;
    public IndoorBuilding indoorBuilding;



    //Handlers
    public static MainActivityHandler mainActivityHandler;

    //UI variables
    //Main activity UI variables
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    public LinearLayout mainActivityLinearLayout;
    //user profile UI variables
    public TextView usernameTextView;
    public TextView passwordTextView;
    public TextView ageTextView;
    public EditText usernameEditText;
    public EditText passswordEditText;
    public EditText ageEditText;


    //fragments
    public static StoreAdvertisementFragment storeAdvertisementFragment;
    public static MapFragment mapFragment;
    public static SettingsFragment settingsFragment;

    public static HashMap<String, TextView> beaconsTextViews;

    //Other variables
    public static Date currentTime;
    public static int userId;
    public static String username;
    public static String lastName;
    public static String firstName;
    public static int age;
    public static Context context;
    public static MainActivity mainActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        mainActivity = this;
        currentTime = Calendar.getInstance().getTime();


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

        //getUserData
        UserData userData = (UserData) getIntent().getSerializableExtra("UserData");
        userId = userData.getUserId();
        username = userData.getUsername();
        lastName = userData.getLastName();
        firstName = userData.getFirstName();
        age = userData.getAge();

        //set communication streams
        objectOutputStream = LoginActivity.objectOutputStream;
        objectInputStream = LoginActivity.objectInputStream;

        //create handler
        mainActivityHandler = new MainActivityHandler(this);

        MainActivity.beaconsTextViews = new HashMap<>();

        //get the beacons data and initialize the beacons
        beacons = new HashMap<>();
        regionsEntered = new HashMap<>();
        malls = new HashMap<>();
        mallsEntered = new HashMap<>();
        levels = new ArrayList<>();
        levelsActivated = new ArrayList<>();
        ibeaconsSet = new HashSet<>();
        closestBeacons = new IBeaconDevice[3];
        positionMarkers = new LinkedList<>();

        Thread getBeaconsDataThread = new Thread(new ReceiveBeaconsDataRunnable(objectInputStream, objectOutputStream));
        getBeaconsDataThread.start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent startLoginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(startLoginActivityIntent);
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
        for(Beacon beacon : beacons.values())
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
                                Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, estimoteBeacon.getId(), estimoteBeacon.getLabel(), 0, true, currentTime, objectOutputStream));
                                sendLocationDataThread.start();
                                return null;
                            }
                        })
                        .onExit(new Function1<ProximityZoneContext, Unit>() {
                            @Override
                            public Unit invoke(ProximityZoneContext context) {
                                //get current time
                                currentTime = Calendar.getInstance().getTime();
                                Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, estimoteBeacon.getId(), estimoteBeacon.getLabel(), 0,  false, currentTime, objectOutputStream));
                                sendLocationDataThread.start();
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
        for(Beacon beacon: beacons.values())
        {
            if(beacon instanceof KontaktBeacon)
            {
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
                //check if the mall changed
                if(MainActivity.mallsEntered.get(MainActivity.beacons.get(iBeacon.getUniqueId()).getMallId()) == false)
                {
                    //update the map based on the beacon mallId
                    Mall mall = MainActivity.malls.get(MainActivity.beacons.get(iBeacon.getUniqueId()).getMallId());
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
                    KontaktBeacon kontaktBeacon = (KontaktBeacon) MainActivity.beacons.get(iBeacon.getUniqueId());
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
                //trilateration algorithm for finding the position of the user using three beacons that are closest to the user
                //this is used for giving the user a good shopping experience in the store by provinding a good indoor localization
                for(IBeaconDevice iBeaconDevice : iBeacons)
                    ibeaconsSet.add(iBeaconDevice);
                if(ibeaconsSet.size() >= 3) {
                    ArrayList<IBeaconDevice> iBeaconDevices = new ArrayList<>();
                    for (IBeaconDevice iBeaconDevice : ibeaconsSet)
                        iBeaconDevices.add(iBeaconDevice);
                    //Collections.sort(iBeaconDevices, new DistanceComparator());
                    double x1, y1, x2, y2, x3, y3, r1, r2, r3, x, y, A, B, C, D, E, F;
                    KontaktBeacon kontaktBeacon1 = (KontaktBeacon) beacons.get(iBeaconDevices.get(0).getUniqueId());
                    KontaktBeacon kontaktBeacon2 = (KontaktBeacon) beacons.get(iBeaconDevices.get(1).getUniqueId());
                    KontaktBeacon kontaktBeacon3 = (KontaktBeacon) beacons.get(iBeaconDevices.get(2).getUniqueId());
                    r1 = iBeaconDevices.get(0).getDistance() / 1000;
                    r2 = iBeaconDevices.get(0).getDistance() / 1000;
                    r3 = iBeaconDevices.get(0).getDistance() / 1000;

                    Point p1=new Point(kontaktBeacon1.getCoordinates().getLatitude(),kontaktBeacon1.getCoordinates().getLongitude(), r1);
                    Point p2=new Point(kontaktBeacon2.getCoordinates().getLatitude(), kontaktBeacon2.getCoordinates().getLongitude(), r2);
                    Point p3=new Point(kontaktBeacon3.getCoordinates().getLatitude(), kontaktBeacon3.getCoordinates().getLongitude(), r3);
                    double[] userPos = Trilateration.Compute(p1,p2,p3);

                    //display a marker on the map
                    if(userPos != null)
                    {
                        LatLng userCoordinates = new LatLng(userPos[0], userPos[1]);
                        mapFragment.googleMap.addMarker(new MarkerOptions().position(userCoordinates).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }

                    ibeaconsSet.clear();
                }
                else
                {
                    Log.d("IBEACONS", "too few beacons for trilateration");
                }
                //end of trilateration algorithm




                //check if a user entered a zone and record the time spent in the zone(used for analitics and stuff)
                for(IBeaconDevice iBeaconDevice : iBeacons)
                {
                    KontaktBeacon kontaktBeacon = (KontaktBeacon) MainActivity.beacons.get(iBeaconDevice.getUniqueId());
                    double distance = iBeaconDevice.getDistance();
                    TextView textView = beaconsTextViews.get(iBeaconDevice.getUniqueId());
                    if(textView!=null)
                        textView.setText(region.getIdentifier() + ": " + iBeaconDevice.getDistance());

                    //check if a user entered a region
                    if(regionsEntered.isEmpty())
                    {
                        if(distance < 2)
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
                            //put the user on the map(not the exact location)
                            LatLng coordinates = new LatLng(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude());
                            mapFragment.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18.0f));
                            Marker positionMarker = mapFragment.googleMap.addMarker(new MarkerOptions().position(coordinates).title(kontaktBeacon.getLabel()));
                            positionMarkers.add(positionMarker);
                            if(positionMarkers.size() > 1)
                            {
                                positionMarkers.poll().remove();//remove the head of the queue leaving only the new marker
                            }

                            regionsEntered.put(iBeaconDevice.getUniqueId(), true);
                            Toast toast = Toast.makeText(getApplicationContext(), "Entered region: " + region.getIdentifier(), Toast.LENGTH_SHORT);
                            toast.show();
                            //send the location data to the server
                            synchronized (objectOutputStream)
                            {
                                currentTime = Calendar.getInstance().getTime();
                                Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, iBeaconDevice.getUniqueId(), region.getIdentifier(), kontaktBeacon.getMallId(), true, currentTime, objectOutputStream));
                                sendLocationDataThread.start();
                            }
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
                                if(distance < 2)
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
                                    //put the user on the map(not the exact location)
                                    LatLng coordinates = new LatLng(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude());
                                    mapFragment.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18.0f));
                                    Marker positionMarker = mapFragment.googleMap.addMarker(new MarkerOptions().position(coordinates).title(kontaktBeacon.getLabel()));
                                    positionMarkers.add(positionMarker);
                                    if(positionMarkers.size() > 1)
                                    {
                                        positionMarkers.poll().remove();//remove the head of the queue leaving only the new marker
                                    }

                                    regionsEntered.put(iBeaconDevice.getUniqueId(), true);
                                    //when the distance from the beacon is smaller than 5 metres and the user was outside the region the user entered the zone
                                    Toast toast = Toast.makeText(getApplicationContext(), "Entered region: " + region.getIdentifier(), Toast.LENGTH_SHORT);
                                    toast.show();
                                    //send the location data to the server
                                    synchronized (objectOutputStream)
                                    {
                                        currentTime = Calendar.getInstance().getTime();
                                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, iBeaconDevice.getUniqueId(), region.getIdentifier(), kontaktBeacon.getMallId(), true, currentTime, objectOutputStream));
                                        sendLocationDataThread.start();
                                    }
                                }
                            }
                            else
                            {
                                //user is outside the region
                                if(distance > 6)
                                {
                                    regionsEntered.put(iBeaconDevice.getUniqueId(), false);
                                    //when the distance from the beacon is smaller than 5 metres and the user was outside the region the user entered the zone
                                    Toast toast = Toast.makeText(getApplicationContext(), "Left region: " + region.getIdentifier(), Toast.LENGTH_SHORT);
                                    toast.show();

                                    //send the location data to the server
                                    synchronized (objectOutputStream)
                                    {
                                        currentTime = Calendar.getInstance().getTime();
                                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, iBeaconDevice.getUniqueId(), region.getIdentifier(), kontaktBeacon.getMallId(), false, currentTime, objectOutputStream));
                                        sendLocationDataThread.start();
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(distance < 2)
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
                                //put the user on the map(not the exact location)
                                LatLng coordinates = new LatLng(kontaktBeacon.getCoordinates().getLatitude(), kontaktBeacon.getCoordinates().getLongitude());
                                mapFragment.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18.0f));
                                Marker positionMarker = mapFragment.googleMap.addMarker(new MarkerOptions().position(coordinates).title(kontaktBeacon.getLabel()));
                                positionMarkers.add(positionMarker);
                                if(positionMarkers.size() > 1)
                                {
                                    positionMarkers.poll().remove();//remove the head of the queue leaving only the new marker
                                }


                                regionsEntered.put(iBeaconDevice.getUniqueId(), true);
                                Toast toast = Toast.makeText(getApplicationContext(), "Entered region: " + region.getIdentifier(), Toast.LENGTH_SHORT);
                                toast.show();
                                //send the location data to the server
                                synchronized (objectOutputStream)
                                {
                                    currentTime = Calendar.getInstance().getTime();
                                    Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, iBeaconDevice.getUniqueId(), region.getIdentifier(), kontaktBeacon.getMallId(), true, currentTime, objectOutputStream));
                                    sendLocationDataThread.start();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
            }
        });
        start();
    }





    @Override
    protected void onStart() {
        super.onStart();

        if(proximityManager != null) {
            //restart scanning for the Kontakt beacons
            start();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(proximityManager != null) {
            //restart scanning for the Kontakt beacons
            start();
        }
    }

    @Override
    protected void onStop() {
        if(proximityManager != null) {
            proximityManager.stopScanning();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }

    protected void start() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
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

        Drawable backround = ContextCompat.getDrawable(context, R.drawable.cool_sky);
        backround.setAlpha(120);
        mainActivityLinearLayout.setBackground(backround);


        //set tabbed layout
        storeAdvertisementFragment = new StoreAdvertisementFragment();
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
        tabLayout.getTabAt(0).setIcon(R.drawable.store_ads_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.stats_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.settings_icon);


        //set the user profile UFI variables
        usernameTextView = findViewById(R.id.usernameTextView);
        passwordTextView = findViewById(R.id.passwordTexView);
        ageTextView = findViewById(R.id.ageTextView);
    }
}