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
import com.example.solomon.networkPackets.Beacon;
import com.example.solomon.networkPackets.EstimoteBeacon;
import com.example.solomon.networkPackets.KontaktBeacon;
import com.example.solomon.networkPackets.Store;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
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
            
            
            //BEACON CONFIGURATION
            //get the beacons from the database
            System.out.println("Added beacons into the database");
            System.out.println("------------------------------------------------------------");
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
                
            }
            //end of beacon configuration
            
            
            
            
            
            
            //STORE MAPS CONFIG
            System.out.println("------------------------------------------------------------");
            System.out.println("          Getting store data from the database");
            System.out.println("------------------------------------------------------------");
            ResultSet storesResultSet = SolomonServer.getTableData("stores");
            if(!storesResultSet.isBeforeFirst())
            {
                //no store available in the database
                System.out.println("No store available in the database");
                System.out.println("------------------------------------------------------------\n\n");
            }
            else
            {
                //get store data from the database
                while(storesResultSet.next())
                {
                    String storeId = storesResultSet.getString("idstores");
                    String storeName = storesResultSet.getString("name");
                    String storeMapPath = storesResultSet.getString("picture");
                    File file = new File(storeMapPath);
                    BufferedImage fullResImage = ImageIO.read(file);
                    
                    
                    //RESIZE IMAGE
                    int cmmdc = cmmdc(fullResImage.getWidth(), fullResImage.getHeight());
                    int widthScaleFactor = fullResImage.getWidth() / cmmdc;
                    int heightScaleFactor = fullResImage.getHeight() / cmmdc;
                    int imageScale = 600;
                    int width, height;
                    if(widthScaleFactor > heightScaleFactor)
                    {
                        width = imageScale;
                        height = imageScale * heightScaleFactor / widthScaleFactor;
                    }
                    else
                    {
                        width = imageScale * widthScaleFactor / heightScaleFactor ;
                        height = imageScale;
                    }
                    System.out.println("Aspect ratio = " + (double)fullResImage.getWidth() / fullResImage.getHeight());
                    System.out.println("Fraction aspect ratio = " + widthScaleFactor + "/" + heightScaleFactor);
                    System.out.println("Image scale = " + imageScale);
                    BufferedImage image = resize(fullResImage, width, height);
                    //end of resize image
                    
                    //IMAGE PROCESSING
                    
                    //TRANSFORM TO GRAYSCALE
                    for(int y = 0; y < height; y++)
                    {
                        for(int x = 0; x < width; x++)
                        {
                            //get the pixel
                            int p = image.getRGB(x,y);
                            int a = (p>>24)&0xff;
                            int r = (p>>16)&0xff;
                            int g = (p>>8)&0xff;
                            int b = p&0xff;
                            //change it to grayscale
                            int avg = (r+g+b)/3;
                            //set the pixel
                            p = (a<<24) | (avg<<16) | (avg<<8) | avg;
                            image.setRGB(x, y, p);
                        }
                    }
                    //end of grayscale transform
                    
                    //APPLY GAUSSIAN BLUR
                    //meanBlur(5, 5, image);
                    gaussianBlur(image);
                    //end of gaussian blur
                    //end of map processing
                    
                    //show the result image 
                    SolomonServer.imageFrame.setImage(image);
                }
            }
            
            
            
            
            
            //TIME PROCESSING
            //get the new enter left room pairs from the database and compute the time difeence and update the time in the database
            while(true)
            {
                //get the new location data from the database
                System.out.println("------------------------------------------------------------");
                System.out.println("          Getting new location data from the database");
                System.out.println("------------------------------------------------------------");
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
                {
                    System.out.println("No new data available");
                    System.out.println("------------------------------------------------------------\n\n");
                }
                this.usersLocations.clear();
                
                //wait 30 sec until the next data aquisition
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
        System.out.println("------------------------------------------------------------\n\n");
    }
    
    
    
    private static BufferedImage resize(BufferedImage img, int width, int height)
    {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
    
    
    private static void meanBlur(int kernelWidth, int kernelHeight, BufferedImage image)
    {
        System.out.println("Mean blur kernel size: " + kernelWidth + " * " + kernelHeight);
        System.out.println("------------------------------------------------------------\n\n");
        int meanBlurKernelSum = kernelWidth * kernelHeight;
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                //compute the image convolution with the mean kernel (a kernel with all the elements one) - equivalent to a mean
                int avg = 0;
                for(int i = y - kernelHeight / 2; i < y + kernelHeight / 2 + 1; i++)
                {
                    for(int j = x - kernelWidth / 2; j < x + kernelWidth / 2 + 1; j++)
                    {
                        if(i >= 0 && j >= 0 && i < image.getHeight() && j < image.getWidth())
                        {
                            int pixel = image.getRGB(j, i);
                            int greyPixelValue = pixel&0xff;
                            avg += greyPixelValue;
                        }
                    }
                }
                avg /= meanBlurKernelSum;
                            
                //set the pixel
                p = (a<<24) | (avg<<16) | (avg<<8) | avg;
                image.setRGB(x, y, p);
            }
        }
    }
    
    private static void gaussianBlur(BufferedImage image)
    {
        //create the gaussian kernel
        //only with 5 * 5 kernel
        double[][] gaussianKernel = new double[5][];
        gaussianKernel[0] = new double[]{0.003765,0.015019,0.023792,0.015019,0.003765};
        gaussianKernel[1] = new double[]{0.015019,0.059912,0.094907,0.059912,0.015019};
        gaussianKernel[2] = new double[]{0.023792,0.094907,0.150342,0.094907,0.023792};
        gaussianKernel[3] = new double[]{0.015019,0.059912,0.094907,0.059912,0.015019};
        gaussianKernel[4] = new double[]{0.003765,0.015019,0.023792,0.015019,0.003765};
        
        
        //make the convolution
        System.out.println("Gaussian blur kernel size: " + 5 + " * " + 5);
        System.out.println("------------------------------------------------------------\n\n");
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                //compute the sum of all neighbours of a pixel and compute the average and then change all the pixels into the average value
                double convSum = 0;
                int kernelIndexHeight = 0;
                int kernelIndexWidth = 0;
                double gaussianKernelSum = 0;
                for(int i = y - 2; i < y + 3; i++)
                {
                    for(int j = x - 2; j < x + 3; j++)
                    {
                        kernelIndexWidth = 0;
                        if(i >= 0 && j >= 0 && i < image.getHeight() && j < image.getWidth())
                        {
                            int pixel = image.getRGB(j, i);
                            int greyPixelValue = pixel&0xff;
                            convSum += (double)greyPixelValue * gaussianKernel[kernelIndexHeight][kernelIndexWidth];
                            gaussianKernelSum += gaussianKernel[kernelIndexHeight][kernelIndexWidth];
                        }
                        kernelIndexWidth++;
                    }
                    kernelIndexHeight++;
                }
                convSum /= gaussianKernelSum;
                int filteredPixel = (int)convSum;
                
                //set the pixel
                p = (a<<24) | (filteredPixel<<16) | (filteredPixel<<8) | filteredPixel;
                image.setRGB(x, y, p);
            }
        }
    }
    
    
    
    
    private static int cmmdc(int a, int b)
    {
        while(a != b)
        {
            if(a > b)
                a -= b;
            else
                b -= a;
        }
        return a;
    }
}
