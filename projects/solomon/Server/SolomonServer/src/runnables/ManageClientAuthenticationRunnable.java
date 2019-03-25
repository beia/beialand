/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.example.solomon.networkPackets.ServerFeedback;
import com.example.solomon.networkPackets.SignInData;
import com.example.solomon.networkPackets.SignUpData;
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
                        SolomonServer.addUser(signUpData.getUsername(), signUpData.getPassword());
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