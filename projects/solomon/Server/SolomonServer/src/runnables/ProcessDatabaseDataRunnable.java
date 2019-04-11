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
                this.usersLocations = new ArrayList<>();
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
                        if(zoneEntered == false)
                        {
                            //we want to collect future data from the database from the next zone entered so we won't lose user room time
                            this.lastLocationId = userLocationData.getInt("idUserLocations");
                            SolomonServer.lastLocationEntryId = this.lastLocationId;
                        }
                        else
                        {
                            this.lastLocationId = userLocationData.getInt("idUserLocations") - 1;
                            SolomonServer.lastLocationEntryId = this.lastLocationId;
                        }
                    }
                }
                //we now created the new arraylist that contains the new location data from the database from all users
                
                
                //link every location data of a user to the userId using a hashmap
                for(LocationData location : this.usersLocations)
                {
                    //add all the user location entry data into an array linked by user id using a hashmap
                    if(userLocationEntryMap.containsKey(location.getUserId()))
                    {
                        //add the user location data into the userLocationArrayList into the hashmap if the user already is into the hashmap
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
                                //if the user entry is in the same store as the previos entry, the zone is the same and the user left the zone compute the time difference for the zone and add it into the database
                                if(userLocationArray.get(j).getStoreId() == userLocationArray.get(i).getStoreId() && userLocationArray.get(j).getZoneName().equals(userLocationArray.get(i).getZoneName()) && userLocationArray.get(j).getZoneEntered() == false)
                                {
                                    System.out.println("\n\npair");
                                    System.out.println("User with id: " + userLocationArray.get(i).getUserId() + " entered =  " + userLocationArray.get(i).getZoneEntered() + " zone: " + userLocationArray.get(i).getZoneName() + " at " + userLocationArray.get(i).getTime());
                                    System.out.println("User with id: " + userLocationArray.get(j).getUserId() + " entered = " + userLocationArray.get(j).getZoneEntered() + " zone: " + userLocationArray.get(j).getZoneName() + " at " + userLocationArray.get(j).getTime());
                                    
                                    
                                    
                                    //get the usefull data from the pair
                                    int idUser = userLocationArray.get(i).getUserId();
                                    int idStore = userLocationArray.get(i).getStoreId();
                                    String zoneName = userLocationArray.get(i).getZoneName();
                                    
                                    //compute the time diference and insert it into the database
                                    //extract the hour from the time - time format example: Fri Mar 29 14:00:40 GMT+02:00 2019
                                    String timeEnteredHour = userLocationArray.get(i).getTime().split(" ")[3];
                                    String timeLeftHour = userLocationArray.get(j).getTime().split(" ")[3];
                                    
                                    String []date = timeEnteredHour.split(":");
                                    int hourEntered = Integer.parseInt(date[0].trim());
                                    int minuteEntered = Integer.parseInt(date[1].trim());
                                    int secondsEntered = Integer.parseInt(date[2].trim());
                                    
                                    date = timeLeftHour.split(":");
                                    int hourLeft = Integer.parseInt(date[0].trim());
                                    int minuteLeft = Integer.parseInt(date[1].trim());
                                    int secondsLeft = Integer.parseInt(date[2].trim());
                                    
                                    //get room data from the database
                                    ResultSet roomTimeResultSet = SolomonServer.getRoomTimeDataFromDatabase("userroomtime", idUser, idStore);
                                    
                                    if(!roomTimeResultSet.isBeforeFirst())
                                    {
                                        //the user never entered the store
                                        //insert the room time data for the user coresponding the store into the database
                                        switch(zoneName)
                                        {
                                            //add in the future new tags
                                            case "Sala de conferinte":
                                                //compute time difference
                                                int secondsEnteredSum = hourEntered * 3600 + minuteEntered * 60 + secondsEntered;
                                                int secondsLeftSum = hourLeft * 3600 + minuteLeft * 60 + secondsLeft;
                                                int secondsDifference = secondsLeftSum - secondsEnteredSum;
                                                int hours = secondsDifference / 3600;
                                                int minutes = (secondsDifference % 3600) / 60;
                                                int seconds = ((secondsDifference % 3600) % 60);
                                                String room1Time = hours + " hours " + minutes + " minutes " + seconds + " seconds";
                                                String room2Time = "0 hours 0 minutes 0 seconds";
                                                String room3Time = "0 hours 0 minutes 0 seconds";
                                                String room4Time = "0 hours 0 minutes 0 seconds";
                                                String[] zonesTime = new String[]{room1Time, room2Time, room3Time, room4Time};
                                                SolomonServer.addZoneTimeData(idUser, idStore, zonesTime);
                                                System.out.println("\nUser with id: " + idUser + "\nRoom: Sala de conferinte from store with id: " + idStore + "\nCurrent time spent in room: " + room1Time);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    else
                                    {
                                        //user entered the store at least once
                                        roomTimeResultSet.next();
                                        switch(zoneName)
                                        {
                                            //add in the future new tags
                                            case "Sala de conferinte":
                                                //compute time difference
                                                String room1PreviousTime = roomTimeResultSet.getString("room1Time");
                                                int secondsEnteredSum = hourEntered * 3600 + minuteEntered * 60 + secondsEntered;
                                                int secondsLeftSum = hourLeft * 3600 + minuteLeft * 60 + secondsLeft;
                                                int secondsDifference = secondsLeftSum - secondsEnteredSum;
                                                int currentHours = secondsDifference / 3600;
                                                int currentMinutes = (secondsDifference % 3600) / 60;
                                                int currentSeconds = ((secondsDifference % 3600) % 60);
                                                String currentTimeString = currentHours + " hours " + currentMinutes + " minutes " + currentSeconds + " seconds";
                                                
                                                String[] data = room1PreviousTime.split(" ");
                                                int previousHours = Integer.parseInt(data[0]);
                                                int previousMinutes = Integer.parseInt(data[2]);
                                                int previousSeconds = Integer.parseInt(data[4]);
                                                
                                                int hours = previousHours + currentHours + ((previousMinutes + currentMinutes) / 60);
                                                int minutes = ((previousMinutes + currentMinutes) % 60) + ((previousSeconds + currentSeconds) / 60);
                                                int seconds = (previousSeconds + currentSeconds) % 60;
                                                
                                                String room1Time = hours + " hours " + minutes + " minutes " + seconds + " seconds";
                                                
                                                SolomonServer.updateZoneTimeData(idUser, idStore, "room1Time", room1Time);
                                                System.out.println("\nUser with id: " + idUser + "\nRoom: Sala de conferinte from store with id: " + idStore + "\nCurrent time spent in room: " + currentTimeString);
                                                System.out.println("Total time spent in room: " + room1Time);
                                                break;
                                            default:
                                                break;
                                        } 
                                        
                                    }
                                    
                                    userLocationArray.remove(j);
                                    i++;
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
           ex.printStackTrace();
        }
    }
    
}
