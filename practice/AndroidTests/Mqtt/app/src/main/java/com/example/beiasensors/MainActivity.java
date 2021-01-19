package com.example.beiasensors;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new MqttAndroidClient(getApplicationContext(),
                getResources().getString(R.string.beia_mqtt_broker_url),
                getResources().getString(R.string.mqtt_client_id));

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("MQTT", "connectComplete: ");
                if (reconnect) {
                    subscribeToTopic(getResources().getString(R.string.phone_battery_topic));
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MQTT", "connectionLost: ");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("MQTT", "messageArrived: " + message.getPayload().toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MQTT", "deliveryComplete: ");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setConnectionTimeout(30000);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    client.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic(getResources().getString(R.string.phone_battery_topic));
                    publishValue(getResources().getString(R.string.mqtt_test_payload),
                            getResources().getString(R.string.phone_battery_topic));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTT","Failed to connect to: "
                            + getResources().getString(R.string.beia_mqtt_broker_url));
                    exception.printStackTrace();
                }
            });
        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }



    public void subscribeToTopic(String topic){
        try {
            client.subscribe(topic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT_SUBSCRIBE", "onSuccess: ");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTT_SUBSCRIBE", "onFailure: ");
                }
            });
        } catch (MqttException ex){
            Log.d("MQTT", "subscribeToTopic ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void publishValue(String payload, String topic){
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            client.publish(topic, message);
        } catch (MqttException e) {
            Log.d("MQTT", "publishValue ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}