package com.example.solomon;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.example.solomon.networkPackets.UserData;
import com.example.solomon.runnables.SendLocationDataRunnable;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    //Beacon variables
    public Date currentTime;
    private ProximityObserver proximityObserver;
    public static TextView feedBackTextView;
    public int userId;
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;

    //UI variables
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        //getUserData
        UserData userData = (UserData) getIntent().getSerializableExtra("UserData");
        userId = userData.getUserId();
        objectOutputStream = LoginActivity.objectOutputStream;
        objectInputStream = LoginActivity.objectInputStream;

        //initialized cloud credentials
        EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("solomon-app-ge4", "97f78b20306bb6a15ed1ddcd24b9ca21");

        //instantiated the proximity observer
        this.proximityObserver = new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                        .onError(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "proximity observer error: " + throwable);
                                feedBackTextView.setText("proximity error");
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .build();

        //instantiated a proximity zone
        final ProximityZone zone1 = new ProximityZoneBuilder()
                .forTag("Room1")
                .inCustomRange(3.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        feedBackTextView.setText("Entered the: " + context.getTag());
                        //get current time
                        currentTime = Calendar.getInstance().getTime();
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Room1", true, currentTime, objectOutputStream));
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
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Room1", false, currentTime, objectOutputStream));
                        sendLocationDataThread.start();
                        return null;
                    }
                })
                .build();

        final ProximityZone zone2 = new ProximityZoneBuilder()
                .forTag("Room2")
                .inCustomRange(3.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        feedBackTextView.setText("Entered the: " + context.getTag());
                        //get current time
                        currentTime = Calendar.getInstance().getTime();
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Room2", true, currentTime, objectOutputStream));
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
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Room2", false, currentTime, objectOutputStream));
                        sendLocationDataThread.start();
                        return null;
                    }
                })
                .build();

        final ProximityZone zone3 = new ProximityZoneBuilder()
                .forTag("Room3")
                .inCustomRange(3.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        feedBackTextView.setText("Entered the: " + context.getTag());
                        //get current time
                        currentTime = Calendar.getInstance().getTime();
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Room3", true, currentTime, objectOutputStream));
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
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Room3", false, currentTime, objectOutputStream));
                        sendLocationDataThread.start();
                        return null;
                    }
                })
                .build();

        final ProximityZone zone4 = new ProximityZoneBuilder()
                .forTag("Room4")
                .inCustomRange(3.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        feedBackTextView.setText("Entered the: " + context.getTag());
                        //get current time
                        currentTime = Calendar.getInstance().getTime();
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Room4", true, currentTime, objectOutputStream));
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
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Room4", false, currentTime, objectOutputStream));
                        sendLocationDataThread.start();
                        return null;
                    }
                })
                .build();

        //set bluetooth functionality
        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.startObserving(zone1);
                                proximityObserver.startObserving(zone2);
                                proximityObserver.startObserving(zone3);
                                proximityObserver.startObserving(zone4);
                                feedBackTextView.setText("requirements fulfiled");
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                feedBackTextView.setText("requirements missing");
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                feedBackTextView.setText("requirements error");
                                return null;
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
    }
}