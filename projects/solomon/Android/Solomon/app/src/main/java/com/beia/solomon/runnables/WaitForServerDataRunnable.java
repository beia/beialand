package com.beia.solomon.runnables;

import android.os.Message;
import android.util.Log;

import com.beia.solomon.activities.LoginActivity;
import com.beia.solomon.activities.MainActivity;
import com.beia.solomon.activities.MallStatsActivity;
import com.beia.solomon.activities.ProfileSettingsActivity;
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.BeaconsData;
import com.beia.solomon.networkPackets.Campaign;
import com.beia.solomon.networkPackets.ImageData;
import com.beia.solomon.networkPackets.Mall;
import com.beia.solomon.networkPackets.ServerFeedback;
import com.beia.solomon.networkPackets.SignInData;
import com.beia.solomon.networkPackets.UserData;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class WaitForServerDataRunnable implements Runnable
{
    //solomon-beacon.beia-consult.ro
    public static final String ip = "172.20.10.11";
    public static final int port = 7000;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String currentActivity;
    public WaitForServerDataRunnable(String activityName)
    {
        this.currentActivity = activityName;
    }
    @Override
    public void run() {
        try
        {
            //CONNECT TO THE SERVER
            if(currentActivity.equals("LoginActivity")) {
                socket = new Socket(ip, port);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                LoginActivity.socket = socket;
                LoginActivity.objectOutputStream = objectOutputStream;
                LoginActivity.objectInputStream = objectInputStream;
            }
            if(currentActivity.equals("MainActivity"))
            {
                socket = new Socket(ip, port);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                MainActivity.socket = socket;
                MainActivity.objectOutputStream = objectOutputStream;
                MainActivity.objectInputStream = objectInputStream;
                //send the automatic login data
                SignInData signInData = new SignInData(MainActivity.userData.getUsername(), MainActivity.userData.getPassword());
                synchronized (objectOutputStream) {
                    objectOutputStream.writeObject(signInData);
                }
            }
            while (true)
            {
                Object networkPacket = objectInputStream.readObject();

                //received a sign up request
                if(networkPacket instanceof ServerFeedback)
                {
                    ServerFeedback serverFeedback = (ServerFeedback)networkPacket;
                    Message message;
                    switch (serverFeedback.getFeedbackMessage())
                    {
                        case "username is taken":
                            message = LoginActivity.handler.obtainMessage(1);
                            message.obj = "username is taken";
                            message.sendToTarget();
                            break;
                        case "registered successfully":
                            message = LoginActivity.handler.obtainMessage(1);
                            message.obj = "registered successfully";
                            message.sendToTarget();
                            break;
                        case "can't login user":
                            if(currentActivity.equals("LoginActivity")) {
                                message = LoginActivity.handler.obtainMessage(1);
                                message.obj = "username or password are wrong";
                                message.sendToTarget();
                            }
                            break;
                        case "login successful":
                            if(currentActivity.equals("LoginActivity")) {
                                message = LoginActivity.handler.obtainMessage(2);
                                message.obj = new UserData(serverFeedback.getUserId(), serverFeedback.getUsername(), serverFeedback.getPassword(), serverFeedback.getLastName(), serverFeedback.getFirstName(), serverFeedback.getGender(), serverFeedback.getAge(), serverFeedback.isFirstLogin());
                                message.sendToTarget();
                            }
                            else
                            {
                                message = MainActivity.mainActivityHandler.obtainMessage(3);
                                message.obj = new UserData(serverFeedback.getUserId(), serverFeedback.getUsername(), serverFeedback.getPassword(), serverFeedback.getLastName(), serverFeedback.getFirstName(), serverFeedback.getGender(), serverFeedback.getAge(), serverFeedback.isFirstLogin());
                                message.sendToTarget();
                            }
                            //request beacons
                            String request1 = "{\"requestType\":\"getBeacons\"}";
                            synchronized (objectOutputStream) {
                                objectOutputStream.writeObject(request1);
                            }
                            String request2 = "{\"requestType\":\"getBeaconsTime\"}";
                            synchronized (objectOutputStream) {
                                objectOutputStream.writeObject(request2);
                            }
                            break;
                        default:
                            break;
                    }
                }

                //RECEIVED BEACONS
                if(networkPacket instanceof BeaconsData)
                {
                    BeaconsData beaconsData = (BeaconsData) networkPacket;
                    MainActivity.beaconsMap = beaconsData.getBeaconsData();//change .. make beacons not static
                    for(Beacon beacon : beaconsData.getBeaconsData().values()) {
                        MainActivity.beaconsMapByCompanyId.put(beacon.getCompanyId(), beacon);
                    }
                    Log.d("BEACONS", "RECEIVED BEACON DATA " + beaconsData.getBeaconsData().size());
                }

                //RECEIVED MALLS DATA
                if(networkPacket instanceof HashMap)
                {
                    HashMap<Integer, Mall> mallsMap = (HashMap<Integer, Mall>) networkPacket;
                    MainActivity.mallsMap = mallsMap;
                    MainActivity.malls.addAll(mallsMap.values());

                    if(currentActivity.equals("MainActivity")) {
                        //send a message to the handler in the main ui thread that we can start detecting the beacons and sending data to the server regarding user position
                        Message message = MainActivity.mainActivityHandler.obtainMessage(1);
                        message.sendToTarget();
                    }
                    else
                    {
                        MainActivity.beaconsReceived = true;
                    }
                }

                //RECEIVED CAMPAIGNS FOR A STORE
                if(networkPacket instanceof ArrayList)
                {
                    MainActivity.campaigns.clear();
                    ArrayList<Campaign> campaigns = (ArrayList<Campaign>)networkPacket;
                    for(Campaign campaign : campaigns) {
                        Log.d("CAMPAIGN", campaign.getIdCompany() + " " + campaign.getCompanyName());
                        byte[] companyImage = MainActivity.beaconsMapByCompanyId.get(campaign.getIdCompany()).getImage();
                        if(companyImage != null)
                            campaign.setCompanyImage(companyImage);
                        MainActivity.campaigns.add(campaign);
                    }
                    if(currentActivity.equals("MainActivity")) {
                        Message message = MainActivity.mainActivityHandler.obtainMessage(2);
                        message.sendToTarget();
                    }
                }

                //PROFILE PICTURE
                if(networkPacket instanceof ImageData)
                {
                    ImageData imageData = (ImageData) networkPacket;
                    //Send a message to the ProfileSettings activity handler with the imagedata so we can change the profile picture
                    Message message = Message.obtain(MainActivity.mainActivityHandler);
                    message.what = 5;
                    message.obj = imageData;
                    message.sendToTarget();
                }

                if(networkPacket instanceof String) {
                    String response = (String)networkPacket;
                    Log.d("RESPONSE", "run: " + response);
                    //parse response
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                    String responseType = jsonResponse.get("responseType").getAsString();
                    switch(responseType) {
                        case "parkingStats":
                            int availableParkingSpacesPercentage = jsonResponse.get("freeSpacesPercentage").getAsInt();
                            Message message = MallStatsActivity.handler.obtainMessage();
                            message.what = 1;
                            message.obj = availableParkingSpacesPercentage;
                            message.sendToTarget();
                            break;
                        case "beaconsTime":
                            Type type = new TypeToken<HashMap<String, Long>>(){}.getType();
                            MainActivity.beaconsTimeMap = gson.fromJson(jsonResponse.get("beaconsTimeMap"), type);
                            Log.d("BEACON", "beaconsTimeMap size: " + MainActivity.beaconsTimeMap.size());
                            Message msg = MainActivity.mainActivityHandler.obtainMessage();
                            msg.what = 6;
                            msg.sendToTarget();
                            break;
                        case "location":
                            double lat = jsonResponse.get("lat").getAsDouble();
                            double lng = jsonResponse.get("lng").getAsDouble();
                            Message locationMessage = MainActivity.mainActivityHandler.obtainMessage();
                            locationMessage.what = 4;
                            locationMessage.obj = new LatLng(lat, lng);
                            locationMessage.sendToTarget();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void setCurrentActivity(String activityName) {
        this.currentActivity = activityName;
    }
}
