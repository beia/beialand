/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.beia.solomon.networkPackets.*;

import java.io.*;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import data.CampaignReaction;
import data.Notification;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class ManageClientAppInteractionRunnable implements Runnable
{
    public int userId;
    public String profilePicturePath = "ProfilePictures\\";
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;

    public ArrayList<Double> valuesBeacon0, valuesBeacon1, valuesBeacon2, valuesBeacon3;
    public File file0, file1, file2, file3;
    public FileWriter fileWriter0, fileWriter1, fileWriter2, fileWriter3;
    public ManageClientAppInteractionRunnable(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException {
        this.userId = userId;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
        valuesBeacon0 = new ArrayList<>();
        valuesBeacon1 = new ArrayList<>();
        valuesBeacon2 = new ArrayList<>();
        valuesBeacon3 = new ArrayList<>();
        file0 = new File("DistanceData\\file0.txt");
        file1 = new File("DistanceData\\file1.txt");
        file2 = new File("DistanceData\\file2.txt");
        file3 = new File("DistanceData\\file3.txt");
        fileWriter0 = new FileWriter(file0);
        fileWriter1 = new FileWriter(file1);
        fileWriter2 = new FileWriter(file2);
        fileWriter3 = new FileWriter(file3);
    }
    @Override
    public void run() {
        try
        {
            //wait for client location data
            while(true)
            {
                Object networkPacket = this.objectInputStream.readObject();
                String response = "";

                //AUTHENTICATION DATA
                if(networkPacket instanceof SignUpData)
                {
                    //check the instance of the object and convert the object to a SignUpData object
                    SignUpData signUpData = (SignUpData)networkPacket;
                    System.out.println(signUpData.toString());
                    //get the user data from the database
                    ResultSet resultSet = SolomonServer.getUserDataFromDatabase("users", signUpData.getUsername());
                    //check if the username exists in the database
                    if (!resultSet.isBeforeFirst() )
                    {
                        //the users isnt't in the database so we insert the user into the database
                        SolomonServer.addUser(signUpData.getUsername(), signUpData.getPassword(), signUpData.getLastName(), signUpData.getFirstName(), signUpData.getGender(), signUpData.getAge());
                        //send the user a feedback text that he was registered
                        synchronized (objectOutputStream) {
                            this.objectOutputStream.writeObject(new ServerFeedback("registered successfully"));
                        }
                        System.out.println("User registered successfully");
                    }
                    else
                    {
                        //send the user a message that the username is taken
                        synchronized (objectOutputStream) {
                            this.objectOutputStream.writeObject(new ServerFeedback("username is taken"));
                        }
                        System.out.println("Username is taken, can't register user");
                    }
                }
                if(networkPacket instanceof SignInData)
                {
                    //check the instance of the object and convert the object to a SignInData object
                    SignInData signInData = (SignInData)networkPacket;
                    //check if the username and password match in the database and if not send a message to the user that the username or password are wrong
                    //get the user data from the database
                    ResultSet resultSet = SolomonServer.getUserDataFromDatabase("users", signInData.getUsername());
                    //check if the username exists in the database
                    if (!resultSet.isBeforeFirst() )
                    {
                        //the users isnt't in the database we send a message to the user that the username and password are wrong
                        synchronized (objectOutputStream) {
                            this.objectOutputStream.writeObject(new ServerFeedback("can't login user"));
                        }
                        System.out.println("Can't login user, user doesn't exist");
                    }
                    else
                    {
                        //the user is in the database
                        resultSet.next();
                        int userId = resultSet.getInt("idusers");
                        String password = resultSet.getString("password");
                        String username = resultSet.getString("username");
                        String lastName = resultSet.getString("lastName");
                        String firstName = resultSet.getString("firstName");
                        String gender = resultSet.getString("gender");
                        int age = resultSet.getInt("age");

                        //SIGN IN SUCCESSFUL
                        if(signInData.getPassword().equals(password))
                        {
                            //username and password are correct login successful
                            boolean isFirstLogin;
                            ResultSet preferencesResultSet = SolomonServer.getPreferencesByUserId(userId);
                            if(!preferencesResultSet.isBeforeFirst())
                            {
                                //user has no preferences so the first thing that he will do when he opens the app is to set his preferences
                                isFirstLogin = true;
                            }
                            else
                            {
                                isFirstLogin = false;
                            }
                            synchronized (objectOutputStream) {
                                this.objectOutputStream.writeObject(new ServerFeedback("login successful", userId, username, password, lastName, firstName, gender, age, isFirstLogin));
                            }
                            System.out.println("User: " + signInData.getUsername() + " logged in successfully!");
                            this.userId = userId;
                        }
                        else
                        {
                            //password is wrong
                            synchronized (objectOutputStream) {
                                this.objectOutputStream.writeObject(new ServerFeedback("can't login user"));
                            }
                            System.out.println("Can't login user, password is wrong");
                        }
                    }
                    //if the username and password match in the database log in the client and start a new thread
                    System.out.println(signInData.toString());
                }

                if(networkPacket instanceof ImageData)
                {
                    //received the picture bytes from the user
                    ImageData imageData = (ImageData)networkPacket;
                    //create a file with a path name that coresponds to the user so we can identify him
                    String path = profilePicturePath + imageData.getUserId() + ".jpg";
                    File file = new File(path);
                    //save the image into the memory
                    Files.write(file.toPath(), imageData.getImageBytes());
                    //save the image path into the database
                    SolomonServer.saveImagePath(path, imageData.getUserId());
                }
                //basic requests
                if(networkPacket instanceof String)
                {
                    String requestString = (String)networkPacket;
                    System.out.println(requestString);
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(requestString, JsonObject.class);
                    String requestType = jsonObject.get("requestType").getAsString();
                    switch(requestType)
                    {
                        case "profilePicture":
                            String imagePath = SolomonServer.getUserProfilePicturePath(userId);
                            if(imagePath == null)
                            {
                                System.out.println("Server error");
                            }
                            else
                            {
                                if(imagePath.equals("No profile picture"))
                                {
                                    System.out.println("------------------------------\nNo profile picture path found\n------------------------------");
                                }
                                else
                                {
                                    try
                                    {
                                        //get the file from path
                                        File file = new File(imagePath);
                                        byte[] imageBytes = new byte[(int) file.length()];

                                        //read file into bytes[]
                                        FileInputStream fileInputStream = new FileInputStream(file);
                                        fileInputStream.read(imageBytes);
                                    
                                        //create the ImageData object ad send it to the user
                                        ImageData imageData = new ImageData(imageBytes, userId);
                                        synchronized (objectOutputStream) {
                                            objectOutputStream.writeObject(imageData);
                                        }
                                        fileInputStream.close();
                                    }
                                    catch (IOException ex)
                                    {
                                        Logger.getLogger(ManageClientAppInteractionRunnable.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }           
                            break;
                        case "logOut":
                            userId = -1;
                            break;
                        case "getBeacons":
                            //send the beacons data to the users
                            if(SolomonServer.beaconsMap != null)
                            {
                                System.out.println("Sent the beacons data to the user");
                                synchronized (objectOutputStream) {
                                    this.objectOutputStream.writeObject(new BeaconsData(SolomonServer.beaconsMap));
                                }
                            }
                            else
                            {
                                System.out.println("No beacons available to send");
                            }
                            //send the malls data to the users
                            if(SolomonServer.mallsMap != null)
                            {
                                System.out.println("Sent the malls data to the user");
                                synchronized (objectOutputStream) {
                                    this.objectOutputStream.writeObject(SolomonServer.mallsMap);
                                }
                            }
                            break;
                        case "getBeaconsTime":
                            response = "{\"responseType\":\"beaconsTime\",\"beaconsTimeMap\":";
                            response += gson.toJson(SolomonServer.totalBeaconTime) + "}";
                            synchronized (objectOutputStream) {
                                this.objectOutputStream.writeObject(response);
                            }
                            break;
                        case "getCampaigns":
                            String beaconId = jsonObject.get("beaconId").getAsString();
                            System.out.println(SolomonServer.campaignsMapByCompanyId.size());
                            ArrayList<Campaign> storeCampaigns = SolomonServer.campaignsMapByCompanyId.get(SolomonServer.beaconsMap.get(beaconId).getCompanyId());
                            if(storeCampaigns != null)
                            {
                                synchronized (objectOutputStream) {
                                    objectOutputStream.writeObject(storeCampaigns);
                                }
                                System.out.println("Campaigns sent, available campaigns: " + storeCampaigns.size());
                            }
                            else
                            {
                                System.out.println("No campaigns available");
                            }
                            break;
                        case "getNotifications":
                            int id = (int)jsonObject.get("userId").getAsLong();
                            if(SolomonServer.notificationsMap.containsKey(id) && !SolomonServer.notificationsMap.get(id).isEmpty()) {
                                Notification notification = SolomonServer.notificationsMap.get(id).poll();
                                response = "{\"responseType\":\"" + notification.getNotificationType() + "\", \"message\":\"" + notification.getMessage() + "\"}";
                            }
                            else {
                                response = "{\"responseType\":\"null\"}";
                            }
                            objectOutputStream.writeObject(response);
                            break;
                        case "getParkingStats":
                            int mallId = (int)jsonObject.get("mallId").getAsLong();
                            String responseParking;
                            if(SolomonServer.parkingSpacesAvailableMap.containsKey(mallId))
                                responseParking = "{\"responseType\":\"parkingStats\",\"freeSpacesPercentage\":"+ SolomonServer.parkingSpacesAvailableMap.get(mallId) +"}";
                            else
                                responseParking = "{\"responseType\":\"parkingStats\", \"freeSpacesPercentage\":-1}";
                            objectOutputStream.writeObject(responseParking);
                            break;
                        case "saveDistance":
                            String idBeaconDistance = jsonObject.get("idBeacon").getAsString();
                            double distance = jsonObject.get("distance").getAsDouble();
                            SolomonServer.dbAddBeaconDistance(idBeaconDistance, distance);
                            break;
                        case "postBeaconTime":
                            int idUser = (int) jsonObject.get("idUser").getAsLong();
                            String idBeacon = jsonObject.get("idBeacon").getAsString();
                            long timeSeconds = jsonObject.get("timeSeconds").getAsLong();
                            BeaconTime beaconTime = new BeaconTime(idUser, SolomonServer.beaconsMap.get(idBeacon), timeSeconds);
                            if(SolomonServer.usersBeaconTimeMap.containsKey(idUser)) {
                                if(SolomonServer.usersBeaconTimeMap.get(idUser).containsKey(idBeacon)) { //add the time to the existing one
                                    long previousTimeSeconds = SolomonServer.usersBeaconTimeMap.get(idUser).get(idBeacon).getTimeSeconds();
                                    SolomonServer.usersBeaconTimeMap.get(idUser).get(idBeacon).setTimeSeconds(previousTimeSeconds + timeSeconds);
                                    //update time in the database
                                    SolomonServer.dbUpdateBeaconTimeData(idUser, idBeacon, previousTimeSeconds + timeSeconds);
                                }
                                else {
                                    SolomonServer.usersBeaconTimeMap.get(idUser).put(idBeacon, beaconTime);
                                    //insert the user beacon time in the database
                                    SolomonServer.dbInsertBeaconTime(idUser, idBeacon, timeSeconds);
                                }
                            }
                            else { //the user never entered the range of a beacon
                                SolomonServer.usersBeaconTimeMap.put(idUser, new HashMap<>());
                                SolomonServer.usersBeaconTimeMap.get(idUser).put(idBeacon, beaconTime);
                                //insert the user beacon time in the database
                                SolomonServer.dbInsertBeaconTime(idUser, idBeacon, timeSeconds);
                            }

                            synchronized (SolomonServer.totalBeaconTime) {
                                if(SolomonServer.totalBeaconTime.containsKey(idBeacon))
                                    SolomonServer.totalBeaconTime.put(idBeacon, SolomonServer.totalBeaconTime.get(idBeacon) + timeSeconds);
                                else
                                    SolomonServer.totalBeaconTime.put(idBeacon, timeSeconds);
                            }
                            break;
                        case "postCampaignReaction":
                            String idCampaign = jsonObject.get("idCampaign").getAsString();
                            Integer idUserCampaignsReaction = jsonObject.get("idUser").getAsInt();
                            String gender = jsonObject.get("gender").getAsString();
                            Integer age = jsonObject.get("age").getAsInt();
                            String viewDate = jsonObject.get("viewDate").getAsString();
                            CampaignReaction campaignReaction = new CampaignReaction(idCampaign, idUserCampaignsReaction, gender, age, viewDate);
                            if(SolomonServer.campaignReactionsMap.containsKey(idCampaign)) {
                                SolomonServer.campaignReactionsMap.get(idCampaign).add(campaignReaction);
                            }
                            else {
                                ArrayList<CampaignReaction> campaignReactions = new ArrayList<>();
                                campaignReactions.add(campaignReaction);
                                SolomonServer.campaignReactionsMap.put(idCampaign, campaignReactions);
                            }
                            SolomonServer.dbAddCampaignReaction(idCampaign, idUserCampaignsReaction, viewDate);
                            break;
                        default:
                            break;
                    }
                }
                
                if(networkPacket instanceof UserPreferences)
                {
                    //add the user preferences into the database
                    UserPreferences userPreferences = (UserPreferences)networkPacket;
                    ArrayList<String> preferences = userPreferences.getPreferences();
                    for(String preference : preferences)
                    {
                        SolomonServer.addUserPreference(userPreferences.getUserId(), preference);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageClientAppInteractionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ManageClientAppInteractionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
