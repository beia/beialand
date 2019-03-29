/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.example.solomon.networkPackets.LocationData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class ProcessDatabaseDataRunnable implements Runnable
{
    private int lastLocationId;
    private ArrayList<LocationData> usersLocations;
    private HashMap<Integer, ArrayList<LocationData>> userLocationEntryMap;
    public ProcessDatabaseDataRunnable()
    {
        this.lastLocationId = SolomonServer.lastLocationEntryId;
        this.usersLocations = new ArrayList<>();
        this.userLocationEntryMap = new HashMap<>();
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
                
                
                //link every location data of a user to the userId using a hashmap
                for(LocationData location : this.usersLocations)
                {
                    //add all the user location entry data into an array linked by user id using a hashmap
                    if(userLocationEntryMap.containsKey(location.getUserId()))
                    {
                        //add the user data into the userLocationArayList into the hashmap
                        ArrayList<LocationData> userLocationArray = userLocationEntryMap.get(location.getUserId());
                        userLocationArray.add(location);
                        userLocationEntryMap.put(location.getUserId(), userLocationArray);
                    }
                    else
                    {
                        //create a new ArrayList and add it into the hashmap
                        ArrayList<LocationData> userLocationArray = new ArrayList<>();
                        userLocationArray.add(location);
                        userLocationEntryMap.put(location.getUserId(), userLocationArray);
                    }
                }
                
                
                //iterate through hashmap and through each user location arrayList and find pairs of enter left zone so we can compute the time spent inside a zone
                for(Map.Entry<Integer, ArrayList<LocationData>> entry : userLocationEntryMap.entrySet())
                {
                    ArrayList<LocationData> userLocationArray = entry.getValue();
                    //search for pairs
                    for(int i = 0; i < userLocationArray.size() - 1; i++)
                    {
                        if(userLocationArray.get(i).getZoneEntered() == true)
                        {
                            for(int j = i + 1; j < userLocationArray.size(); j++)
                            {
                                //if the user etry is in the same store as the previos entry, the zone is the same and the user left the zone compute the time difference for the zone and add it into the database
                                if(userLocationArray.get(j).getStoreId() == userLocationArray.get(i).getStoreId() && userLocationArray.get(j).getZoneName().equals(userLocationArray.get(i).getZoneName()) && userLocationArray.get(j).getZoneEntered() == false)
                                {
                                    System.out.println("pair");;
                                    System.out.println("User with id: " + userLocationArray.get(i).getUserId() + " entered =  " + userLocationArray.get(i).getZoneEntered() + " zone: " + userLocationArray.get(i).getZoneName() + " at " + userLocationArray.get(i).getTime());
                                    System.out.println("User with id: " + userLocationArray.get(j).getUserId() + " entered = " + userLocationArray.get(j).getZoneEntered() + " zone: " + userLocationArray.get(j).getZoneName() + " at " + userLocationArray.get(j).getTime());
                                    
                                    //compute the time diference and insert it int the database
                                    
                                    userLocationArray.remove(j);
                                }
                            }
                        }
                    }
                }
                
                //remove all data from the hashmap
                this.userLocationEntryMap.clear();
                
                
                //check if there is no more new data available
                if(this.usersLocations.isEmpty())
                    System.out.println("No new data available");
                this.usersLocations.clear();
                
                //wait 30 sec until the next data aquisition
                System.out.println("\n\nGetting new location data from the database...");
                Thread.sleep(30000);
            }
        }
        catch(Exception ex)
        {
            
        }
    }
    
}
