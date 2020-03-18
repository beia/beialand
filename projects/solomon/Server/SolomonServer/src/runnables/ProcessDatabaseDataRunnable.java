/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import com.beia.solomon.networkPackets.LocationData;
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
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.Coordinates;
import com.beia.solomon.networkPackets.EstimoteBeacon;
import com.beia.solomon.networkPackets.ImageData;
import com.beia.solomon.networkPackets.KontaktBeacon;
import com.beia.solomon.networkPackets.Mall;
import com.beia.solomon.networkPackets.MallData;
import com.beia.solomon.networkPackets.Store;
import data.ColorRGB;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class ProcessDatabaseDataRunnable implements Runnable
{
    private int lastLocationId;
    private ArrayList<LocationData> usersLocations;
    private HashMap<Integer, MallData> malls;
    private HashMap<Integer, ArrayList<LocationData>> userLocationEntryMap;
    public ProcessDatabaseDataRunnable()
    {
        this.lastLocationId = SolomonServer.lastLocationEntryId;
        this.usersLocations = new ArrayList<>();
        this.malls = new HashMap<>();
        this.userLocationEntryMap = new HashMap<>();
    }
    @Override
    public void run() {
        try
        {   
                        
            //BEACON CONFIGURATION
            //get the beacons data from the XML configuration file
            getBeaconsData(SolomonServer.beacons);
            //update the beacon database table accordingly to the xml configuration file
            ResultSet beaconData = SolomonServer.getTableData("beacons");
            if(!beaconData.isBeforeFirst())
            {
                //the beacons were never configured so we add the beacons into the database
                for(Beacon beacon : SolomonServer.beacons.values())
                { 
                    if(beacon instanceof EstimoteBeacon)
                    {
                        EstimoteBeacon estimoteBeacon = (EstimoteBeacon) beacon;
                        SolomonServer.addEstimoteBeacon(estimoteBeacon.getId(), estimoteBeacon.getLabel(), estimoteBeacon.getMallId(), EstimoteBeacon.COMPANY, estimoteBeacon.getCoordinates(), estimoteBeacon.getLayer(), estimoteBeacon.getFloor());
                    }
                    if(beacon instanceof KontaktBeacon)
                    {
                        KontaktBeacon kontaktBeacon = (KontaktBeacon) beacon;
                        SolomonServer.addKontaktBeacon(kontaktBeacon.getId(), kontaktBeacon.getLabel(), kontaktBeacon.getMallId(), kontaktBeacon.COMPANY, kontaktBeacon.getMajor(), kontaktBeacon.getMinor(), kontaktBeacon.getCoordinates(), kontaktBeacon.getLayer(), kontaktBeacon.getFloor());
                    }
                }
            }
            else
            {
                //the beacons where already configured so we want to configure them again
                //check if the beacons from the database are in the configuration file - if not then we must delete them from the database
                HashMap<String, Beacon> databaseBeaconMap = new HashMap<>();
                while(beaconData.next())
                {
                    String id = beaconData.getString("id");
                    String label = beaconData.getString("label");
                    int mallId = beaconData.getInt("idMall");
                    String company = beaconData.getString("company");
                    double latitude = beaconData.getDouble("latitude");
                    double longitude = beaconData.getDouble("longitude");
                    int layer = beaconData.getInt("layer");
                    int floor = beaconData.getInt("floor");
                    
                    switch(company)
                    {
                        case "Estimote":
                            databaseBeaconMap.put(id, new EstimoteBeacon(id, label, mallId, new Coordinates(latitude, longitude), layer, floor));
                            break;
                        case "Kontakt":
                            String major = beaconData.getString("major");
                            String minor = beaconData.getString("minor");
                            
                            databaseBeaconMap.put(id, new KontaktBeacon(id, label, mallId, major, minor, new Coordinates(latitude, longitude), layer, floor));
                            break;
                        default:
                            break;
                    }
                }
                
                for(String beaconId : databaseBeaconMap.keySet())
                {
                    if(SolomonServer.beacons.containsKey(beaconId) == false)
                    {
                        //the SolomonServer.beacons hashmap contains the beacons from the configuration file
                        //this means that we don't want the beacon anymore
                        //remove the beacon from the database - the deletion from the database is CASCADE(foreign key - 'label')
                        //this action will remove also the time spent near the beacon and all the moments that where saved regarding that beacon
                        SolomonServer.deleteBeacon(beaconId);
                    }
                }
                
                //add the new beacons into the database
                for(Beacon beacon : SolomonServer.beacons.values())
                {
                    //check if the beacons from the configuration file are into the database
                    //if not we add them into the database
                    if(databaseBeaconMap.containsKey(beacon.getId()) == false)
                    {
                        if(beacon instanceof EstimoteBeacon)
                        {
                            EstimoteBeacon estimoteBeacon = (EstimoteBeacon) beacon;
                            SolomonServer.addEstimoteBeacon(estimoteBeacon.getId(), estimoteBeacon.getLabel(), estimoteBeacon.getMallId(), EstimoteBeacon.COMPANY, estimoteBeacon.getCoordinates(), estimoteBeacon.getLayer(), estimoteBeacon.getFloor());
                        }
                        if(beacon instanceof KontaktBeacon)
                        {
                            KontaktBeacon kontaktBeacon = (KontaktBeacon) beacon;
                            SolomonServer.addKontaktBeacon(kontaktBeacon.getId(), kontaktBeacon.getLabel(), kontaktBeacon.getMallId(), kontaktBeacon.COMPANY, kontaktBeacon.getMajor(), kontaktBeacon.getMinor(), kontaktBeacon.getCoordinates(), kontaktBeacon.getLayer(), kontaktBeacon.getFloor());
                        }
                    }
                }
            }
            System.out.println("Added beacons into the database");
            System.out.println("------------------------------------------------------------");
            //end of beacon configuration
            
            //MALLS CONFIG
            File file = new File("src/configFiles/mallsCoordinates");
            Scanner scan = new Scanner(file);
            //get stores from the database
            ArrayList<Store> stores = new ArrayList<>();
            ResultSet storesResultSet = SolomonServer.getTableData("stores");
            while(storesResultSet.next())
            {
                int idStore = storesResultSet.getInt("idStores");
                String name = storesResultSet.getString("name");
                String idEntranceBeacon = storesResultSet.getString("idBeacon");
                int idMall = storesResultSet.getInt("idMall");
                stores.add(new Store(idStore, name, idMall, SolomonServer.beacons.get(idEntranceBeacon)));
            }
            while(scan.hasNextLine())
            {
                String[] data = scan.nextLine().split(" ");
                int mallId = Integer.parseInt(data[0]);
                String mallName = data[1];
                double latitude = Double.parseDouble(data[2]);
                double longitude = Double.parseDouble(data[3]);
                ArrayList<Store> storesFromMall = new ArrayList<>();
                for(Store store : stores)
                {
                    if(store.getMallId() == mallId)
                    {
                        storesFromMall.add(store);
                    }
                }
                Mall mall = new Mall(mallId, storesFromMall, new Coordinates(latitude, longitude));
                SolomonServer.malls.put(mallId, mall);
                System.out.println("Created mall with id: " + mall.getMallId() + " and name: " + mallName);
            }
            
            
            //TIME PROCESSING
            //get the new enter left room pairs from the database and compute the time difeence and update the time in the database
            while(true)
            {
                //get the new location data from the database
                System.out.println("------------------------------------------------------------");
                System.out.println("          Getting new location data from the database");
                System.out.println("------------------------------------------------------------");
                ResultSet userLocationData = SolomonServer.getNewLocationData("userlocations", "iduserLocations", this.lastLocationId);
                this.usersLocations = new ArrayList<>();
                while(userLocationData.next())
                {
                    int idUser = userLocationData.getInt("idUser");
                    String beaconId = userLocationData.getString("idBeacon");
                    String beaconLabel = userLocationData.getString("beaconLabel");
                    int mallId = userLocationData.getInt("idMall");
                    boolean zoneEntered = userLocationData.getBoolean("zoneEntered");
                    String time = userLocationData.getString("time");
                    LocationData locationData = new LocationData(idUser, beaconId, beaconLabel, mallId, zoneEntered, time);
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
                        //add the user location data into the userLocationArrayList if the user already is into the hashmap
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
                                if(userLocationArray.get(i).getBeaconId().equals(userLocationArray.get(j).getBeaconId()) && userLocationArray.get(i).getMallId() == userLocationArray.get(j).getMallId() && userLocationArray.get(j).getZoneEntered() == false)
                                {
                                    System.out.println("\n\npair");
                                    System.out.println("User with id: " + userLocationArray.get(i).getUserId() + " entered =  " + userLocationArray.get(i).getZoneEntered() + " zone: " + userLocationArray.get(i).getBeaconLabel() + " with beaconId = " + userLocationArray.get(i).getBeaconId() + " at " + userLocationArray.get(i).getTime());
                                    System.out.println("User with id: " + userLocationArray.get(j).getUserId() + " entered = " + userLocationArray.get(j).getZoneEntered() + " zone: " + userLocationArray.get(j).getBeaconLabel() + " with beaconId = " + userLocationArray.get(i).getBeaconId() + " at " + userLocationArray.get(j).getTime());
                                    
                                    
                                    
                                    //get the usefull data from the pair
                                    int idUser = userLocationArray.get(i).getUserId();
                                    String beaconId = userLocationArray.get(i).getBeaconId();
                                    String beaconLabel = userLocationArray.get(i).getBeaconLabel();
                                    int mallId = userLocationArray.get(i).getMallId();
                                    
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
                                    
                                    
                                    
                                    //get beacon time data from the database(time spent by users in the proximity of a beacon)
                                    ResultSet beaconTimeResultSet = SolomonServer.getBeaconTimeByUserId(idUser, beaconId, mallId);
                                    
                                    ////compute time difference and insert the beacon time data
                                    if(!beaconTimeResultSet.isBeforeFirst())
                                    {
                                        //the never entered the room and neither the store(because I will add all the other beacon time data in the database with the time spent of 0 seconds)
                                        long secondsEnteredSum, secondsLeftSum, secondsDifference;
                                        secondsEnteredSum = hourEntered * 3600 + minuteEntered * 60 + secondsEntered;
                                        secondsLeftSum = hourLeft * 3600 + minuteLeft * 60 + secondsLeft;
                                        secondsDifference = secondsLeftSum - secondsEnteredSum;
                                        SolomonServer.addBeaconTimeData(idUser, beaconId, beaconLabel, mallId, secondsDifference);
                                        System.out.println("\nUser with id: " + idUser + "\nBeacon: " + beaconLabel + " from mall with id: " + mallId + "\nCurrent time spent near beacon: " + secondsDifference + " seconds");
                                        //add all the other beacon time data into the database but with the time spent 0
                                        for (Beacon beacon : SolomonServer.beacons.values())
                                        {
                                            if(!beacon.getId().equals(beaconId))
                                            {
                                                SolomonServer.addBeaconTimeData(idUser, beacon.getId(), beacon.getLabel(), beacon.getMallId(), 0);
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
                                        beaconTimeResultSet.next();
                                        previousSecondsDifference = beaconTimeResultSet.getLong("timeSeconds");
                                        long totalSeconds = currentSecondsDifference + previousSecondsDifference;
                                        SolomonServer.updateBeaconTimeData(idUser, beaconLabel, mallId, totalSeconds);
                                        System.out.println("\nUser with id: " + idUser + "\nBeacon: " + beaconLabel + " from mall with id: " + mallId + "\nCurrent time spent near the beacon: " + totalSeconds + " seconds");
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
                {
                    System.out.println("No new data available");
                    System.out.println("------------------------------------------------------------\n\n");
                }
                this.usersLocations.clear();
                
                //wait 30 sec until the next data aquisition
                Thread.sleep(300000);
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
        File inputFile = new File("src/configFiles/beacons.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = (Document) dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        System.out.println("------------------------------------------------------------");
        System.out.println("          Getting beacon data from the config file");
        System.out.println("------------------------------------------------------------");
        System.out.println("Root element : " + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName("beacon");
        
        for (int i = 0; i < nList.getLength(); i++)
        {
            Node nNode = nList.item(i);
            System.out.println("\nCurrent Element : " + nNode.getNodeName());
            if(nNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element eElement = (Element) nNode;
                String id = eElement.getAttribute("id");
                String label = eElement.getElementsByTagName("label").item(0).getTextContent();
                int mallId = Integer.parseInt(eElement.getElementsByTagName("idMall").item(0).getTextContent());
                String company = eElement.getElementsByTagName("company").item(0).getTextContent();
                String latitude = eElement.getElementsByTagName("latitude").item(0).getTextContent();
                String longitude = eElement.getElementsByTagName("longitude").item(0).getTextContent();
                int layer = Integer.parseInt(eElement.getElementsByTagName("layer").item(0).getTextContent());
                int floor = Integer.parseInt(eElement.getElementsByTagName("floor").item(0).getTextContent());
                System.out.println("Beacon id : " + eElement.getAttribute("id"));
                System.out.println("Label : " + label);
                System.out.println("Mall id: " + mallId);
                System.out.println("Company : " + company);
                System.out.println("Latitude : " + latitude);
                System.out.println("Longitude : " + longitude);
                System.out.println("Layer : " + layer);
                System.out.println("Floor : " + floor);
                
                //add the beacon into the hashmap
                switch(company)
                {
                    case "Estimote":
                        beacons.put(id , new EstimoteBeacon(id, label, mallId, new Coordinates(Double.parseDouble(latitude), Double.parseDouble(longitude)), layer, floor));
                        break;
                    case "Kontakt":
                        String major = eElement.getElementsByTagName("major").item(0).getTextContent();
                        String minor = eElement.getElementsByTagName("minor").item(0).getTextContent();
                        beacons.put(id, new KontaktBeacon(id, label, mallId, major, minor, new Coordinates(Double.parseDouble(latitude), Double.parseDouble(longitude)), layer, floor));
                        break;
                    default:
                        break;
                }
            }
        }
        System.out.println("------------------------------------------------------------\n\n");
    }
    
    
    
    //IMAGE PROCESSING
    private static BufferedImage resize(BufferedImage img, int width, int height)
    {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
    private static ColorRGB[][] convertToRGB(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        ColorRGB[][] result = new ColorRGB[width][height];
        for(int i = 0; i < width; i++)
        {
            for(int j = 0; j < height; j++)
            {
                int pixel = image.getRGB(i, j);
                //int a = (pixel >> 24) & 0xff;
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                result[i][j] = new ColorRGB(r, g, b);
            }
        }
        System.out.println("Finished the image conversion");
        return result;
    }
    
}
