/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.example.solomon.networkPackets.SignInData;
import com.example.solomon.networkPackets.SignUpData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                    //check if the username is taken and if yes send a message to the Client that the username is taken
                    //if the username is valid add the user's data to the database
                    System.out.println(signUpData.toString());
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
        }
    }
}