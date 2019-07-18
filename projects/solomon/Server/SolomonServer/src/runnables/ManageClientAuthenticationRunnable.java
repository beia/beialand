/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.beia.solomon.networkPackets.BeaconsData;
import com.beia.solomon.networkPackets.ServerFeedback;
import com.beia.solomon.networkPackets.SignInData;
import com.beia.solomon.networkPackets.SignUpData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class ManageClientAuthenticationRunnable  implements Runnable
{
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;
    private boolean connected;
    private Object networkPacket;
    private SignUpData signUpData;
    private SignInData signInData;
    public ManageClientAuthenticationRunnable(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream)
    {
        this.connected = false;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }
    @Override
    public void run() {
        try 
        {
            while(!connected)
            {
                //wait for a packet from the client
                networkPacket = this.objectInputStream.readObject();
                if(networkPacket instanceof SignUpData)
                {
                    //check the instance of the object and convert the object to a SignUpData object
                    signUpData = (SignUpData)networkPacket;
                    System.out.println(signUpData.toString());
                    //get the user data from the database
                    ResultSet resultSet = SolomonServer.getUserDataFromDatabase("users", signUpData.getUsername());
                    //check if the username exists in the database
                    if (!resultSet.isBeforeFirst() ) 
                    {
                        //the users isnt't in the database so we insert the user into the database
                        SolomonServer.addUser(signUpData.getUsername(), signUpData.getPassword(), signUpData.getLastName(), signUpData.getFirstName(), signUpData.getAge());
                        //send the user a feedback text that he was registered
                        this.objectOutputStream.writeObject(new ServerFeedback("registered successfully"));
                        System.out.println("User registered successfully");
                    }
                    else
                    {
                        //send the user a message that the username is taken
                        this.objectOutputStream.writeObject(new ServerFeedback("username is taken"));
                        System.out.println("Username is taken, can't register user");
                    }
                }
                if(networkPacket instanceof SignInData)
                {
                    //check the instance of the object and convert the object to a SignInData object
                    signInData = (SignInData)networkPacket;
                    //check if the username and password match in the database and if not send a message to the user that the username or password are wrong
                    //get the user data from the database
                    ResultSet resultSet = SolomonServer.getUserDataFromDatabase("users", signInData.getUsername());
                    //check if the username exists in the database
                    if (!resultSet.isBeforeFirst() ) 
                    {
                        //the users isnt't in the database we send a message to the user that the username and password are wrong
                        this.objectOutputStream.writeObject(new ServerFeedback("can't login user"));
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
                        if(signInData.getPassword().equals(password))
                        {
                            //username and password are correct login successful
                            this.objectOutputStream.writeObject(new ServerFeedback("login successful", userId, username, lastName, firstName, age));
                            System.out.println("User: " + signInData.getUsername() + " logged in successfully!");
                            this.connected = true;
                            
                            //send the beacons data to the users
                            if(SolomonServer.beacons != null)
                            {
                                System.out.println("Sent the beacons data to the users");
                                this.objectOutputStream.writeObject(new BeaconsData(SolomonServer.beacons));
                            }
                            else
                            {
                                System.out.println("No beacons available to send");
                            }
                            
                            //check if the beacons were received by the user
                            String clientFeedback = (String)this.objectInputStream.readObject();
                            
                            //if the client received the beacons then we start listening for location data
                            if(clientFeedback.equals("Client received beacons"))
                            {
                                System.out.println("Client received the beacons");
                                //start a new thread that is monitoring user interaction with the app
                                Thread manageClientAppInteractionsThread = new Thread(new ManageClientAppInteractionRunnable(userId, this.objectOutputStream, this.objectInputStream));
                                manageClientAppInteractionsThread.start();
                                System.out.println("Started location thread");
                                
                                //send a message to the user that we started listening to the location data
                                this.objectOutputStream.writeObject("Started listening to the location data");
                            }
                            
                        }
                        else
                        {
                            //password is wrong
                            this.objectOutputStream.writeObject(new ServerFeedback("can't login user"));
                            System.out.println("Can't login user, password is wrong");
                        }
                    }
                    //if the username and password match in the database log in the client and start a new thread
                    System.out.println(signInData.toString());
                }
            }
        }
        catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(ManageClientAuthenticationRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ManageClientAuthenticationRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}