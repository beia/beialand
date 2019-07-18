/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.beia.solomon.networkPackets.ImageData;
import com.beia.solomon.networkPackets.LocationData;
import com.beia.solomon.networkPackets.UpdateUserData;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
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
                    //received a location information from the user
                    LocationData userLocationData = (LocationData)networkPacket;
                    SolomonServer.addLocationData(userLocationData.getUserId(), userLocationData.getStoreId(), userLocationData.getZoneName(), userLocationData.getZoneEntered(), userLocationData.getTime());
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
                    String path = "C:\\Users\\beia\\Desktop\\UsersProfilePictures\\" + imageData.getUserId() + ".jpg";
                    File file = new File(path);
                    //save the image into the memory
                    Files.write(file.toPath(), imageData.getImageBytes());
                    //save the image path into the database
                    SolomonServer.saveImage(path, imageData.getUserId());
                }
                //basic requests
                if(networkPacket instanceof String)
                {
                    String requestString = (String)networkPacket;
                    switch(requestString)
                    {
                        case "Photo request":
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
                                        objectOutputStream.writeObject(imageData);
                                        fileInputStream.close();
                                    }
                                    catch (IOException ex)
                                    {
                                        Logger.getLogger(ManageClientAppInteractionRunnable.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                            
                            break;
                        default:
                            break;
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
