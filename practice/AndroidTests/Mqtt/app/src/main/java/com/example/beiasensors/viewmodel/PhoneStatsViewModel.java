package com.example.beiasensors.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class PhoneStatsViewModel extends ViewModel {
    MutableLiveData<MqttAndroidClient> client;
    MutableLiveData<Float> batteryPercentage;

    public LiveData<MqttAndroidClient> getMqttClient() {
        if(client == null) {
            client = new MutableLiveData<>();
        }
        return client;
    }

    public LiveData<Float> getBatteryPercentage() {
        if(batteryPercentage == null) {
            batteryPercentage = new MutableLiveData<>();
        }
        return batteryPercentage;
    }
}
