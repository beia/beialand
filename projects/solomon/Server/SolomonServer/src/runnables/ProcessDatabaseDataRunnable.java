/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.example.solomon.networkPackets.LocationData;
import java.sql.ResultSet;
import java.util.ArrayList;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class ProcessDatabaseDataRunnable implements Runnable
{
    private int lastLocationId;
    private ArrayList<LocationData> usersLocations;
    public ProcessDatabaseDataRunnable()
    {
        this.lastLocationId = SolomonServer.lastLocationEntryId;
        this.usersLocations = new ArrayList<>();
    }
    @Override
    public void run() {
        try
        {
            System.out.println("\n\nGetting new location data from the database...");
            while(true)
            {
                //get the new location data from the database
                ResultSet userLocationData = SolomonServer.getNewTableData("userlocations", "iduserLocations", this.lastLocationId);
                while(userLocationData.next())
                {
                    int idUser = userLocationData.getInt("idUser");
                    int idStore = userLocationData.getInt("idStore");
                    String zoneName = userLocationData.getString("zoneName");
                    boolean zoneEntered = userLocationData.getBoolean("zoneEntered");
                    String time = userLocationData.getString("time");
                    LocationData locationData = new LocationData(idUser, idStore, zoneName, zoneEntered, time);
                    this.usersLocations.add(locationData);
                    
                    //check if it's the end of the table and if it is save the last entry id so we would no longer process old data 
                    if(userLocationData.isLast())
                    {
                        this.lastLocationId = userLocationData.getInt("idUserLocations");
                        SolomonServer.lastLocationEntryId = this.lastLocationId;
                    }
                       
                }
                
                //show the new location data
                for(LocationData location : this.usersLocations)
                {
                    if(location.getZoneEntered())
                        System.out.println("User with id: " + location.getUserId() + " entered " + location.getZoneName() + " at " + location.getTime());
                    else
                        System.out.println("User with id: " + location.getUserId() + " left " + location.getZoneName() + " at " + location.getTime());
                }
                
                //check if there is no more new data available
                if(this.usersLocations.isEmpty())
                    System.out.println("No new data available");
                this.usersLocations.clear();
                Thread.sleep(30000);
                System.out.println("\n\nGetting new location data from the database...");
            }
        }
        catch(Exception ex)
        {
            
        }
    }
    
}
