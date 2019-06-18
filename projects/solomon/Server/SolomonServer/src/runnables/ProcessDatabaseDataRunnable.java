/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.example.solomon.networkPackets.LocationData;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import solomonserver.Beacon;
import solomonserver.EstimoteBeacon;
import solomonserver.KontaktBeacon;
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
            //get the beacons data from the XML configuration file and add it int the database
            getBeaconsData(SolomonServer.beacons);
            
            //get the beacons from the database
            System.out.println("\n\nAdding beacons into the database:");
            System.out.println("-------------------------------------");
            ResultSet beaconData = SolomonServer.getTableData("beacons");
            if(!beaconData.isBeforeFirst())
            {
                //the beacons were never configured so we add the beacons into the database
                for(Beacon beacon : SolomonServer.beacons.values())
                { 
                    if(beacon instanceof EstimoteBeacon)
                    {
                        EstimoteBeacon estimoteBeacon = (EstimoteBeacon) beacon;
                        SolomonServer.addEstimoteBeacon(estimoteBeacon.getId(), estimoteBeacon.getLabel(), EstimoteBeacon.COMPANY);
                    }
                    if(beacon instanceof KontaktBeacon)
                    {
                        KontaktBeacon kontaktBeacon = (KontaktBeacon) beacon;
                        SolomonServer.addKontaktBeacon(kontaktBeacon.getId(), kontaktBeacon.getLabel(), kontaktBeacon.COMPANY, kontaktBeacon.getMajor(), kontaktBeacon.getMinor());
                    }
                }
            }
            else
            {
                //the beacons where already configured so we want to configure them again
                //check if the beacons frm the database are in the configuration file - if not then we must delete them from the database
                HashMap<String, Beacon> databaseBeaconMap = new HashMap<>();
                while(beaconData.next())
                {
                    String id = beaconData.getString("id");
                    String label = beaconData.getString("label");
                    String company = beaconData.getString("company");
                    switch(company)
                    {
                        case "Estimote":
                            databaseBeaconMap.put(label, new EstimoteBeacon(id, label));
                            break;
                        case "Kontakt":
                            String major = beaconData.getString("major");
                            String minor = beaconData.getString("minor");
                            databaseBeaconMap.put(label, new KontaktBeacon(id, label, major, minor));
                            break;
                        default:
                            break;
                    }
                }
                
                for(String beaconLabel : databaseBeaconMap.keySet())
                {
                    if(SolomonServer.beacons.containsKey(beaconLabel) == false)
                    {
                        //the SolomonServer.beacons hashmap contains the beacons from the configuration file
                        //this means that we don't want the beacon anymore
                        //remove the beacon from the database - the deletion from the database is CASCADE(foreign key - 'label')
                        //this action will remove also the time spent near the beacon and all the moments that where saved regarding that beacon
                        SolomonServer.deleteBeacon(beaconLabel);
                    }
                }
                
                //add the new beacons into the database
                for(Beacon beacon : SolomonServer.beacons.values())
                {
                    //check if the beacons from the configuration file are into the database
                    //if not we add them into the database
                    if(databaseBeaconMap.containsKey(beacon.getLabel()) == false)
                    {
                        if(beacon instanceof EstimoteBeacon)
                        {
                            EstimoteBeacon estimoteBeacon = (EstimoteBeacon) beacon;
                            SolomonServer.addEstimoteBeacon(estimoteBeacon.getId(), estimoteBeacon.getLabel(), EstimoteBeacon.COMPANY);
                        }
                        if(beacon instanceof KontaktBeacon)
                        {
                            KontaktBeacon kontaktBeacon = (KontaktBeacon) beacon;
                            SolomonServer.addKontaktBeacon(kontaktBeacon.getId(), kontaktBeacon.getLabel(), kontaktBeacon.COMPANY, kontaktBeacon.getMajor(), kontaktBeacon.getMinor());
                        }
                    }
                }
                //end of configuration
            }
            
            //get the new enter left room pairs from the database and compute the time difeence and update the time in the database
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
                                if(userLocationArray.get(i).getUserId() == userLocationArray.get(j).getUserId() && userLocationArray.get(j).getStoreId() == userLocationArray.get(i).getStoreId() && userLocationArray.get(j).getZoneName().equals(userLocationArray.get(i).getZoneName()) && userLocationArray.get(j).getZoneEntered() == false)
                                {
                                    System.out.println("\n\npair");
                                    System.out.println("User with id: " + userLocationArray.get(i).getUserId() + " entered =  " + userLocationArray.get(i).getZoneEntered() + " zone: " + userLocationArray.get(i).getZoneName() + " at " + userLocationArray.get(i).getTime());
                                    System.out.println("User with id: " + userLocationArray.get(j).getUserId() + " entered = " + userLocationArray.get(j).getZoneEntered() + " zone: " + userLocationArray.get(j).getZoneName() + " at " + userLocationArray.get(j).getTime());
                                    
                                    
                                    
                                    //get the usefull data from the pair
                                    int idUser = userLocationArray.get(i).getUserId();
                                    int idStore = userLocationArray.get(i).getStoreId();
                                    String roomName = userLocationArray.get(i).getZoneName();
                                    
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
                                    ResultSet roomTimeResultSet = SolomonServer.getRoomDataByUserId("userroomtime", idUser, idStore, roomName);
                                    
                                    ////compute time difference and insert the room time data for every room coresponding to each user
                                    if(!roomTimeResultSet.isBeforeFirst())
                                    {
                                        //the never entered the room and neither the store(because I will add all the other rooms in the database with the time spent of 0 seconds)
                                        long secondsEnteredSum, secondsLeftSum, secondsDifference;
                                        secondsEnteredSum = hourEntered * 3600 + minuteEntered * 60 + secondsEntered;
                                        secondsLeftSum = hourLeft * 3600 + minuteLeft * 60 + secondsLeft;
                                        secondsDifference = secondsLeftSum - secondsEnteredSum;
                                        SolomonServer.addZoneTimeData(idUser, idStore, roomName, secondsDifference);
                                        System.out.println("\nUser with id: " + idUser + "\nRoom: " + roomName + " from store with id: " + idStore + "\nCurrent time spent in room: " + secondsDifference + " seconds");
                                        //add all the other rooms into the database but with the time spent 0
                                        for (Beacon beacon : SolomonServer.beacons.values())
                                        {
                                            if(!beacon.getLabel().equals(roomName))
                                            {
                                                SolomonServer.addZoneTimeData(idUser, idStore, beacon.getLabel(), 0);
                                            }
                                        }
                                    }
                                    else
                                    {
                                        //user entered the room at least once
                                        long secondsEnteredSum, secondsLeftSum, currentSecondsDifference, previousSecondsDifference;
                                        secondsEnteredSum = hourEntered * 3600 + minuteEntered * 60 + secondsEntered;
                                        secondsLeftSum = hourLeft * 3600 + minuteLeft * 60 + secondsLeft;
                                        currentSecondsDifference = secondsLeftSum - secondsEnteredSum;
                                        roomTimeResultSet.next();
                                        previousSecondsDifference = roomTimeResultSet.getLong("timeSeconds");
                                        long totalSeconds = currentSecondsDifference + previousSecondsDifference;
                                        SolomonServer.updateZoneTimeData(idUser, idStore, roomName, totalSeconds);
                                        System.out.println("\nUser with id: " + idUser + "\nRoom: " + roomName + " from store with id: " + idStore + "\nCurrent time spent in room: " + totalSeconds + " seconds");
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
    
    public void getBeaconsData(HashMap<String, Beacon> beacons) throws SAXException, ParserConfigurationException, IOException
    {
        //get the beacons data from a XML configuration file
        File inputFile = new File("C:\\Users\\beia\\Desktop\\beialand\\projects\\solomon\\Server\\SolomonServer\\src\\configFiles\\beacons.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = (Document) dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        System.out.println("\n\nGetting beacon data from config file: \n");
        System.out.println("Root element : " + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName("beacon");
        System.out.println("----------------------------");
        
        for (int i = 0; i < nList.getLength(); i++)
        {
            Node nNode = nList.item(i);
            System.out.println("\nCurrent Element : " + nNode.getNodeName());
            if(nNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element eElement = (Element) nNode;
                String id = eElement.getAttribute("id");
                String label = eElement.getElementsByTagName("label").item(0).getTextContent();
                String company = eElement.getElementsByTagName("company").item(0).getTextContent();
                System.out.println("Beacon id : " + eElement.getAttribute("id"));
                System.out.println("Label : " + label);
                System.out.println("Company : " + company);
                
                //add the beacon into the hashmap
                switch(company)
                {
                    case "Estimote":
                        beacons.put(label , new EstimoteBeacon(id, label));
                        break;
                    case "Kontakt":
                        String major = eElement.getElementsByTagName("major").item(0).getTextContent();
                        String minor = eElement.getElementsByTagName("minor").item(0).getTextContent();
                        beacons.put(label, new KontaktBeacon(id, label, major, minor));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
