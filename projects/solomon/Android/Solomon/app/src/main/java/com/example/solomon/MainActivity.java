package com.example.solomon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.example.solomon.Handlers.MainActivityHandler;
import com.example.solomon.networkPackets.Beacon;
import com.example.solomon.networkPackets.BeaconsData;
import com.example.solomon.networkPackets.EstimoteBeacon;
import com.example.solomon.networkPackets.KontaktBeacon;
import com.example.solomon.networkPackets.UserData;
import com.example.solomon.runnables.ReceiveBeaconsDataRunnable;
import com.example.solomon.runnables.SendLocationDataRunnable;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.BeaconRegion;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.SpaceListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.ble.spec.EddystoneFrameType;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    //beacon variables
    public static volatile HashMap<String, Beacon> beacons;//change tu public not static
    //Estimote variables
    public EstimoteCloudCredentials cloudCredentials;
    public ProximityObserver proximityObserver;
    public  ArrayList<ProximityZone> estimoteProximityZones;
    //Kontakt variables
    public ProximityManager proximityManager;

    //Communication variables
    public static volatile ObjectOutputStream objectOutputStream;
    public static volatile ObjectInputStream objectInputStream;

    //Handlers
    public static MainActivityHandler mainActivityHandler;

    //UI variables
    //Main activity UI variables
    public TabLayout tabLayout;
    public ViewPager viewPager;
    public ViewPagerAdapter viewPagerAdapter;
    public TextView feedBackTextView;
    //user profile UI variables
    public TextView usernameTextView;
    public TextView passwordTextView;
    public TextView ageTextView;
    public EditText usernameEditText;
    public EditText passswordEditText;
    public EditText ageEditText;

    //Other variables
    public static Date currentTime;
    public static int userId;
    public static Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        currentTime = Calendar.getInstance().getTime();

        initUI();

        //getUserData
        UserData userData = (UserData) getIntent().getSerializableExtra("UserData");
        userId = userData.getUserId();
        objectOutputStream = LoginActivity.objectOutputStream;
        objectInputStream = LoginActivity.objectInputStream;

        //create handler
        mainActivityHandler = new MainActivityHandler(this);

        //get the beacons data and initialize the beacons
        beacons = new HashMap<>();
        Thread getBeaconsDataThread = new Thread(new ReceiveBeaconsDataRunnable(beacons, objectInputStream, objectOutputStream));
        getBeaconsDataThread.start();
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
                                feedBackTextView.setText("Entered the: " + context.getTag());
                                //get current time
                                currentTime = Calendar.getInstance().getTime();
                                Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, estimoteBeacon.getLabel(), true, currentTime, objectOutputStream));
                                sendLocationDataThread.start();
                                return null;
                            }
                        })
                        .onExit(new Function1<ProximityZoneContext, Unit>() {
                            @Override
                            public Unit invoke(ProximityZoneContext context) {
                                feedBackTextView.setText("Left the: " + context.getTag());
                                //get current time
                                currentTime = Calendar.getInstance().getTime();
                                Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, estimoteBeacon.getLabel(), false, currentTime, objectOutputStream));
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
        //check if we have Kontakt beacons
        if(beacons.isEmpty() == false)
        {
            Log.d("BEACONS", "We have beacons...checking for Kontakt beacons");
            for(Beacon beacon : beacons.values())
            {
                if(beacon instanceof KontaktBeacon)
                Log.d("Kontakt beacons", beacon.getLabel());
            }
        }


        //initialize the Kontakt SDK
        KontaktSDK.initialize(String.valueOf(R.string.kontakt_io_api_key));
        proximityManager = ProximityManagerFactory.create(this);


        //configure the proximity manager
        proximityManager.configuration()
                .scanMode(ScanMode.BALANCED)
                .scanPeriod(ScanPeriod.RANGING)
                .activityCheckConfiguration(ActivityCheckConfiguration.DEFAULT)
                .eddystoneFrameTypes(Arrays.asList(EddystoneFrameType.UID, EddystoneFrameType.URL));


        //configure the regions
        Collection<IBeaconRegion> beaconRegions = new ArrayList<>();
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


        //region listener
        proximityManager.setSpaceListener(new SpaceListener() {
            @Override
            public void onRegionEntered(IBeaconRegion region) {
                //IBeacon region has been entered
                Toast toast = Toast.makeText(getApplicationContext(), "Entered region: " + region.getIdentifier(), Toast.LENGTH_LONG);
                toast.show();
                feedBackTextView.setText("Entered region: " + region.getIdentifier());

                //send the location data to the server
                currentTime = Calendar.getInstance().getTime();
                Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, region.getIdentifier(), true, currentTime, objectOutputStream));
                sendLocationDataThread.start();
            }

            @Override
            public void onRegionAbandoned(IBeaconRegion region) {
                //IBeacon region has been abandoned
                Toast toast = Toast.makeText(getApplicationContext(), "Left region: " + region.getIdentifier(), Toast.LENGTH_LONG);
                toast.show();
                feedBackTextView.setText("Left region: " + region.getIdentifier());

                //send the location data to the server
                currentTime = Calendar.getInstance().getTime();
                Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, region.getIdentifier(), false, currentTime, objectOutputStream));
                sendLocationDataThread.start();
            }

            @Override
            public void onNamespaceEntered(IEddystoneNamespace namespace) {
                //Eddystone namespace has been entered
            }

            @Override
            public void onNamespaceAbandoned(IEddystoneNamespace namespace) {
                //Eddystone namespace has been abandoned
            }
        });


        //start scanning for the Kontakt beacons
        startScanning();
    }





    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        proximityManager.stopScanning();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }

    private void startScanning() {
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
        feedBackTextView = findViewById(R.id.feedBackTextView);


        //set tabbed layout
        StoreAdvertisementFragment storeAdvertisementFragment = new StoreAdvertisementFragment();
        Bundle bundle1 = new Bundle();
        ArrayList<String> storeAdvertisementsData = new ArrayList<>();
        bundle1.putStringArrayList("storeAdvertisementsData", storeAdvertisementsData);
        storeAdvertisementFragment.setArguments(bundle1, "storeAdvertisementsData");

        UserStatsFragment userStatsFragment = new UserStatsFragment();
        Bundle bundle2 = new Bundle();
        ArrayList<String> userStatsData = new ArrayList<>();
        bundle2.putStringArrayList("userStatsData", userStatsData);
        userStatsFragment.setArguments(bundle2, "userStatsData");

        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle3 = new Bundle();
        ArrayList<String> profileDataAndSettingsData = new ArrayList<>();
        bundle3.putStringArrayList("profileDataAndSettingsData", profileDataAndSettingsData);
        settingsFragment.setArguments(bundle3, "profileDataAndSettingsData");

        //add the fragment to the viewPagerAdapter
        int numberOfTabs = 3;
        viewPagerAdapter.addFragment(storeAdvertisementFragment, "storeAdvertisementsData");
        viewPagerAdapter.addFragment(userStatsFragment, "userStatsData");
        viewPagerAdapter.addFragment(settingsFragment, "profileDataAndSettingsData");

        //set my ViewPagerAdapter to the ViewPager
        viewPager.setAdapter(viewPagerAdapter);
        //set the tabLayoutViewPager
        tabLayout.setupWithViewPager(viewPager);

        //set images instead of title text for each tab
        tabLayout.getTabAt(0).setIcon(R.drawable.store_ads_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.stats_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.settings_icon);



        //set the user profile UI variables
        usernameTextView = findViewById(R.id.usernameTextView);
        passwordTextView = findViewById(R.id.passwordTexView);
        ageTextView = findViewById(R.id.ageTextView);
    }
}