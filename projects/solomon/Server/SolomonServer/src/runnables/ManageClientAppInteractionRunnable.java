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
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Notification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
                        SolomonServer.addUser(signUpData.getUsername(), signUpData.getPassword(), signUpData.getLastName(), signUpData.getFirstName(), signUpData.getAge());
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
                                this.objectOutputStream.writeObject(new ServerFeedback("login successful", userId, username, password, lastName, firstName, age, isFirstLogin));
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
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(requestString.trim());
                    String requestType = (String)jsonObject.get("requestType");
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
                        case "getCampaigns":
                            String companyName = (String)jsonObject.get("companyName");
                            System.out.println(SolomonServer.campaignsMapByCompanyName.size());
                            ArrayList<Campaign> storeCampaigns = SolomonServer.campaignsMapByCompanyName.get(companyName);
                            if(storeCampaigns != null)
                            {
                                synchronized (objectOutputStream) {
                                    objectOutputStream.writeObject(SolomonServer.campaignsMapByCompanyName.get(companyName));
                                }
                                System.out.println("Campaigns sent, available campaigns: " + storeCampaigns.size());
                            }
                            else
                            {
                                System.out.println("No campaigns available");
                            }
                            break;
                        case "getNotifications":
                            int id = (int)(long) jsonObject.get("userId");
                            String response;
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
                            int mallId = (int)(long)jsonObject.get("mallId");
                            String responseParking;
                            if(SolomonServer.parkingSpacesAvailableMap.containsKey(mallId))
                                responseParking = "{\"responseType\":\"parkingStats\",\"freeSpacesPercentage\":"+ SolomonServer.parkingSpacesAvailableMap.get(mallId) +"}";
                            else
                                responseParking = "{\"responseType\":\"parkingStats\", \"freeSpacesPercentage\":-1}";
                            objectOutputStream.writeObject(responseParking);
                            break;
                        case "saveDistance":
                            double distance = (double)jsonObject.get("distance");
                            String beaconLabel = (String)jsonObject.get("beaconLabel");
                            switch (beaconLabel)
                            {
                                case "0":
                                    valuesBeacon0.add(distance);
                                    if (valuesBeacon0.size() > 20) {
                                        BufferedWriter writer = new BufferedWriter(new FileWriter(file0, true));
                                        for(Double value : valuesBeacon0) {
                                            writer.write(value + "");
                                            writer.newLine();
                                        }
                                        writer.close();
                                        valuesBeacon0.clear();
                                    }
                                    break;
                                case "1":
                                    valuesBeacon1.add(distance);
                                    if (valuesBeacon1.size() > 20) {
                                        BufferedWriter writer = new BufferedWriter(new FileWriter(file1, true));
                                        for(Double value : valuesBeacon1) {
                                            writer.write(value + "");
                                            writer.newLine();
                                        }
                                        writer.close();
                                        valuesBeacon1.clear();
                                    }
                                    break;
                                case "2":
                                    valuesBeacon2.add(distance);
                                    if (valuesBeacon2.size() > 20) {
                                        BufferedWriter writer = new BufferedWriter(new FileWriter(file2, true));
                                        for(Double value : valuesBeacon2) {
                                            writer.write(value + "");
                                            writer.newLine();
                                        }
                                        writer.close();
                                        valuesBeacon2.clear();
                                    }
                                    break;
                                case "3":
                                    valuesBeacon3.add(distance);
                                    if (valuesBeacon3.size() > 20) {
                                        BufferedWriter writer = new BufferedWriter(new FileWriter(file3, true));
                                        for(Double value : valuesBeacon3) {
                                            writer.write(value + "");
                                            writer.newLine();
                                        }
                                        writer.close();
                                        valuesBeacon3.clear();
                                    }
                                    break;
                                default:
                                    break;
                            }
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
