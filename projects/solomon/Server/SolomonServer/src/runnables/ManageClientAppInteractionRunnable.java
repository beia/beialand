/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.example.solomon.networkPackets.LocationData;
import com.example.solomon.networkPackets.UpdateUserData;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class ManageClientAppInteractionRunnable implements Runnable
{
    public int userId;
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    public ManageClientAppInteractionRunnable(int userId, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream)
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
                if(networkPacket instanceof LocationData)
                {
                    LocationData userLocationData = (LocationData)networkPacket;
                    SolomonServer.addLocationData(userLocationData.getUserId(), userLocationData.getStoreId(), userLocationData.getZoneName(), userLocationData.getZoneEntered(), userLocationData.getTime());
                }
                if(networkPacket instanceof UpdateUserData)
                {
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
            }
        } catch (IOException ex) {
            Logger.getLogger(ManageClientAppInteractionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageClientAppInteractionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ManageClientAppInteractionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
