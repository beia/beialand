/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.beia.solomon.networkPackets.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public ManageClientAppInteractionRunnable(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream)
    {
        this.userId = userId;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
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



                //APP INTERACTION DATA
                if(networkPacket instanceof LocationData)
                {
                    //received a location information from the user
                    LocationData userLocationData = (LocationData)networkPacket;
                    SolomonServer.addLocationData(userLocationData.getUserId(), userLocationData.getBeaconId(), userLocationData.getBeaconLabel(), userLocationData.getMallId(), userLocationData.getZoneEntered(), userLocationData.getTime());
                }
                if(networkPacket instanceof UpdateUserData)
                {
                    //received some personal info from the user to be updated
                    UpdateUserData updateUserData = (UpdateUserData)networkPacket;
                    if(updateUserData.getUsername() != null)
                    {
                        SolomonServer.updateUsername(updateUserData.getId(), updateUserData.getUsername());
                    }
                    if(updateUserData.getPassword() != null)
                    {
                        SolomonServer.updatePassword(updateUserData.getId(), updateUserData.getPassword());
                    }
                    if(updateUserData.getAge() != 0)
                    {
                        SolomonServer.updateAge(updateUserData.getId(), updateUserData.getAge());
                    }
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
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(requestString.trim());
                    String requestType = (String)jsonObject.get("requestType");
                    switch(requestType)
                    {
                        case "profilePicture":
                            System.out.println("User with id: "+ userId + " requested an image");
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
                            if(SolomonServer.beacons != null)
                            {
                                System.out.println("Sent the beacons data to the user");
                                synchronized (objectOutputStream) {
                                    this.objectOutputStream.writeObject(new BeaconsData(SolomonServer.beacons));
                                }
                            }
                            else
                            {
                                System.out.println("No beacons available to send");
                            }
                            //send the malls data to the users
                            if(SolomonServer.malls != null)
                            {
                                System.out.println("Sent the malls data to the user");
                                synchronized (objectOutputStream) {
                                    this.objectOutputStream.writeObject(SolomonServer.malls);
                                }
                            }
                            break;
                        case "getCampaigns":
                            String companyName = (String)jsonObject.get("companyName");
                            System.out.println("Client requested campaigns for store '" + companyName + "'");
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
