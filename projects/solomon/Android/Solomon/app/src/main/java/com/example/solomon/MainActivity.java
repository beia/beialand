package com.example.solomon;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.example.solomon.networkPackets.LocationData;
import com.example.solomon.networkPackets.UserData;
import com.example.solomon.runnables.SendLocationDataRunnable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    public Date currentTime;
    private ProximityObserver proximityObserver;
    public static TextView feedBackTextView;
    public int userId;
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get current time
        currentTime = Calendar.getInstance().getTime();

        //getUserData
        UserData userData = (UserData) getIntent().getSerializableExtra("UserData");
        userId = userData.getUserId();
        objectOutputStream = LoginActivity.objectOutputStream;
        objectInputStream = LoginActivity.objectInputStream;

        //init UI
        feedBackTextView = findViewById(R.id.feedBackTextView);

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
        final ProximityZone zone = new ProximityZoneBuilder()
                .forTag("conf room")
                .inCustomRange(2.0)
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        feedBackTextView.setText("Entered the: " + context.getTag());
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Sala de conferinte", true, currentTime, objectOutputStream));
                        sendLocationDataThread.start();
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        feedBackTextView.setText("Left the: " + context.getTag());
                        Thread sendLocationDataThread = new Thread(new SendLocationDataRunnable(userId, 1, "Sala de conferinte", false, currentTime, objectOutputStream));
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
                                proximityObserver.startObserving(zone);
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
}