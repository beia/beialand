/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.beia.solomon.networkPackets.Campaign;
import com.mysql.cj.protocol.Resultset;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import solomonserver.SolomonServer;

import javax.xml.transform.Result;

/**
 *
 * @author beia
 */
public class WaitForWebPlatformClientsRequestsRunnable implements Runnable {
    
    private ServerSocket serverSocket;
    private final int TOKEN_DIMENSION = 14;
    private String campaignsPhotoPath = "CampaignsPictures\\";
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    public WaitForWebPlatformClientsRequestsRunnable(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }
    @Override
    public void run() {
            Socket socket = null;
            String authToken, campaignId;
            while(true)
            {
                try { 
                socket = serverSocket.accept();
                //REQUEST
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                System.out.println("Waiting for web plaform clients http requests...");
                StringBuilder content;
                String body = ""; 
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                    byte[] byteArray = buffer.toByteArray();
                    body = new String(byteArray, StandardCharsets.UTF_8);
                    Scanner scan = new Scanner(body);
                    boolean dataReceived = false;
                    while(scan.hasNextLine())
                    {
                        String line = scan.nextLine();
                        if(line.length() > 0 && line.charAt(line.length() - 1) == '}')
                        {
                            System.out.println(line);
                            body = line;
                            dataReceived = true;
                            break;
                        }
                    }
                    if(dataReceived == true)
                        break;
                }
                //System.out.println(body);
                buffer.flush();
                
                //PARSE JSON
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(body.trim());
                String requestType = (String)jsonObject.get("requestType");
                switch(requestType)
                {
                    case "login":
                        String usernameLogin = (String)jsonObject.get("username");
                        String passwordLogin = (String)jsonObject.get("password");
                        ResultSet resultSetLogin = SolomonServer.getUserDataFromDatabase("companies", usernameLogin);
                        if(!resultSetLogin.isBeforeFirst())//the user isn't in the database
                        {
                            System.out.println("LOGIN FAILED");
                            //write the response
                            PrintWriter out = new PrintWriter(outputStream);
                            out.print("HTTP/1.1 200 \r\n"); // Version & status code
                            out.print("Content-Type: application/json\r\n"); // The type of data
                            out.print("Connection: close\r\n"); // Will close stream
                            out.print("\r\n"); // End of headers
                            //write the body of the response
                            String jsonResponseLogin = "{\"authToken\":null}";
                            out.print(jsonResponseLogin);
                            out.close();
                        }
                        else
                        {
                            resultSetLogin.next();
                            String usernameDatabase = resultSetLogin.getString("username");
                            String passwordDatabase = resultSetLogin.getString("password");
                            String nameDatabase = resultSetLogin.getString("name");
                            if(passwordLogin.equals(passwordDatabase))
                            {
                                System.out.println("LOGIN SUCCESSFULL");
                                //create a authentication token
                                String token = getAlphaNumericString(TOKEN_DIMENSION);
                                while(SolomonServer.webClientsTokensMap.containsKey(token))
                                    token = getAlphaNumericString(TOKEN_DIMENSION);
                                SolomonServer.webClientsTokensMap.put(token, usernameLogin);
                                String jsonResponseLogin = "{\"authToken\":\"" + token + "\"}";
                                writeResponse(jsonResponseLogin, outputStream);
                            }
                            else
                            {
                                System.out.println("LOGIN FAILED");
                                String jsonResponseLogin = "{\"authToken\":null}";
                                writeResponse(jsonResponseLogin, outputStream);
                            }
                        }
                        break;
                    case "logout":
                        String token = (String)jsonObject.get("authToken");
                        if(SolomonServer.webClientsTokensMap.containsKey(token))//user logged in
                        {
                            String username = SolomonServer.webClientsTokensMap.get(token);
                            System.out.println("User with username: " + username + " logged out!");
                            SolomonServer.webClientsTokensMap.remove(token);
                            String jsonResponseLogout = "{\"logout\":true}";
                            writeResponse(jsonResponseLogout, outputStream);
                        }
                        else//user is not logged in or token expired
                        {
                            System.out.println("User with token: " + token + " has token expired or logged out already!");
                            String jsonResponseLogout = "{\"logout\":false}";
                            writeResponse(jsonResponseLogout, outputStream);
                        }
                        break;
                    case "register":
                        String usernameRegister = (String)jsonObject.get("username");
                        String passwordRegister = (String)jsonObject.get("password");
                        String nameRegister = (String)jsonObject.get("name");
                        ResultSet resultSetRegister = SolomonServer.getUserDataFromDatabase("companies", usernameRegister);
                        if(!resultSetRegister.isBeforeFirst())//the user isn't in the database
                        {
                            System.out.println("REGISTER SUCCESFULL");
                            SolomonServer.addCompany(usernameRegister, passwordRegister, nameRegister);
                            SolomonServer.companiesMap.put(usernameRegister, nameRegister);
                            String jsonResponseRegister = "{\"success\":true}";
                            writeResponse(jsonResponseRegister, outputStream);
                        }
                        else
                        {
                            System.out.println("REGISTER FAILED");
                            String jsonResponseRegister = "{\"success\":false}";
                            writeResponse(jsonResponseRegister, outputStream);
                        }
                        break;
                    case "campaigns"://send the active campains
                        String response = null;
                        authToken = (String)jsonObject.get("authToken");
                        if(!SolomonServer.webClientsTokensMap.containsKey(authToken))
                        {
                            response = "{\"success\":false,\"campaigns\":null}";
                            writeResponse(response, outputStream);
                            break;
                        }
                        ResultSet campainsResultSet = SolomonServer.getCampains(SolomonServer.webClientsTokensMap.get(authToken));
                        if(!campainsResultSet.isBeforeFirst())//no campain available
                        {
                            response = "{\"success\":true,\"campaigns\":null}";
                        }
                        else//campains available
                        {
                            response = "{\"success\":true,\"campaigns\":[";
                            int validCampains = 0;
                            while(campainsResultSet.next())
                            {
                                String startDate = campainsResultSet.getString("startDate");
                                String endDate = campainsResultSet.getString("endDate");
                                String currentDate = dateFormat.format(cal.getTime());
                                if(currentDate.compareTo(startDate) >= 0 && currentDate.compareTo(endDate) <= 0) //the campain is still active
                                {
                                    validCampains++;
                                    if(validCampains > 1) response += ',';
                                    response += campainsResultSet.getString("idCampaign");
                                }
                            }
                            if(validCampains == 0)
                                response = "{\"success\":true,\"campaigns\":null}";
                            else
                                response += "]}";
                        }
                        writeResponse(response, outputStream);
                        break;
                    case "oldCampaigns"://send the active campains
                        String responseOldCampains = null;
                        String authTokenOldCampains = (String)jsonObject.get("authToken");
                        if(!SolomonServer.webClientsTokensMap.containsKey(authTokenOldCampains))
                        {
                            responseOldCampains = "{\"success\":false,\"oldCampaigns\":null}";
                            writeResponse(responseOldCampains, outputStream);
                            break;
                        }
                        ResultSet oldCampainsResultSet = SolomonServer.getCampains(SolomonServer.webClientsTokensMap.get(authTokenOldCampains));
                        if(!oldCampainsResultSet.isBeforeFirst())//no campain available
                        {
                            responseOldCampains = "{\"success\":true,\"oldCampaigns\":null}";
                        }
                        else//campains available
                        {
                            responseOldCampains = "{\"success\":true,\"oldCampaigns\":[";
                            int oldCampains = 0;
                            while(oldCampainsResultSet.next())
                            {
                                String startDate = oldCampainsResultSet.getString("startDate");
                                String endDate = oldCampainsResultSet.getString("endDate");
                                String currentDate = dateFormat.format(cal.getTime());
                                if(currentDate.compareTo(startDate) > 0 && currentDate.compareTo(endDate) > 0) //the campain is still active
                                {
                                    oldCampains++;
                                    if(oldCampains > 1) responseOldCampains += ',';
                                    responseOldCampains += oldCampainsResultSet.getString("idCampaign");
                                }
                            }
                            if(oldCampains == 0)
                                responseOldCampains = "{\"success\":true,\"oldCampaigns\":null}";
                            else
                                responseOldCampains += "]}";
                        }
                        writeResponse(responseOldCampains, outputStream);
                    break;
                    case "addCampaign":
                        String authTokenAddCampaign = (String)jsonObject.get("authToken");
                        String responseAddCampaign;
                        if(SolomonServer.webClientsTokensMap.containsKey(authTokenAddCampaign))
                        {
                            String title = (String)jsonObject.get("title");
                            String description = (String)jsonObject.get("description");
                            String startDate = (String)jsonObject.get("startDate");
                            String endDate = (String)jsonObject.get("endDate");
                            byte[] imageBytes = Base64.getDecoder().decode((String)jsonObject.get("image"));
                            String idCampain = getAlphaNumericString(10);
                            while(SolomonServer.campaignsMapById.containsKey(idCampain))
                                idCampain = getAlphaNumericString(10);
                            String idCompany = SolomonServer.webClientsTokensMap.get(authTokenAddCampaign);
                            String path = campaignsPhotoPath + idCampain + ".jpg";
                            SolomonServer.addCampain(idCampain, idCompany, title, description, startDate, endDate, path);
                            SolomonServer.campaignsMapById.put(idCampain, new Campaign(idCampain, idCompany, title, description, startDate, endDate, path));
                            System.out.println("Company '" + idCompany + " inserted campaign with id " + idCampain);
                            responseAddCampaign = "{\"success\":true}";
                            writeResponse(responseAddCampaign, outputStream);
                            
                            //save the photo on the disk(ideally a new thread)
                            File file = new File(path);
                            Files.write(file.toPath(), imageBytes);
                        }
                        else//wrong auth token
                        {
                            System.out.println("wrong token");
                            responseAddCampaign = "{\"success\":false}";
                            writeResponse(responseAddCampaign, outputStream);
                        }
                        break;
                    case "getCampaign":
                        authToken = (String)jsonObject.get("authToken");
                        campaignId = (String)jsonObject.get("campaignID");
                        String responseGetCampaign;
                        if(SolomonServer.webClientsTokensMap.containsKey(authToken))
                        {
                            if(SolomonServer.campaignsMapById.containsKey(campaignId))
                            {
                                Campaign campaign = SolomonServer.campaignsMapById.get(campaignId);
                                responseGetCampaign = "{\"campaignID\":\"" + campaignId 
                                                        + ",\"title\":\"" + campaign.getTitle() 
                                                        + ",\"description\":\"" + campaign.getDescription() 
                                                        + ",\"startDate\":\"" + campaign.getStartDate() 
                                                        + ",\"endDate\":\"" + campaign.getEndDate() 
                                                        + ",\"image\":\"" + Base64.getEncoder().encodeToString(getImageFromDisk(campaign.getPhotoPath())) + "\"}";
                            }
                            else
                            {
                                responseGetCampaign = "{\"campaignID\":null}";
                            }
                        }
                        else//the user isn't logged in
                        {
                            responseGetCampaign = "{\"campaignID\":null}";
                        }
                        writeResponse(responseGetCampaign, outputStream);
                        break;
                    case "updateCampaign":
                        String authTokenUpdateCampaign = (String)jsonObject.get("authToken");
                        String responseUpdateCampaign;
                        if(SolomonServer.webClientsTokensMap.containsKey(authTokenUpdateCampaign))
                        {
                            String campaignID = (String)jsonObject.get("campaignID");
                            String title = (String)jsonObject.get("title");
                            String description = (String)jsonObject.get("description");
                            String startDate = (String)jsonObject.get("startDate");
                            String endDate = (String)jsonObject.get("endDate");
                            byte[] imageBytes = Base64.getDecoder().decode((String)jsonObject.get("image"));
                            String idCompany = SolomonServer.webClientsTokensMap.get(authTokenUpdateCampaign);
                            String path = campaignsPhotoPath + campaignID + ".jpg";
                            SolomonServer.updateCampain(campaignID, title, description, startDate, endDate);
                            Campaign campaign = SolomonServer.campaignsMapById.get(campaignID);
                            campaign.update(title, description, startDate, endDate);
                            System.out.println("Company '" + idCompany + " updated campaign with id " + campaignID);
                            responseAddCampaign = "{\"success\":true}";
                            writeResponse(responseAddCampaign, outputStream);

                            //save the photo on the disk(ideally a new thread)
                            File file = new File(path);
                            Files.write(file.toPath(), imageBytes);
                        }
                        else//wrong auth token
                        {
                            System.out.println("wrong token");
                            responseAddCampaign = "{\"success\":false}";
                            writeResponse(responseAddCampaign, outputStream);
                        }
                        break;
                    case "removeCampaign":
                        String authTokenRemoveCampaign = (String)jsonObject.get("authToken");
                        String responseRemoveCampaign;
                        if(SolomonServer.webClientsTokensMap.containsKey(authTokenRemoveCampaign))
                        {
                            String campaignID = (String)jsonObject.get("campaignID");
                            SolomonServer.removeCampaign(campaignID);
                            SolomonServer.campaignsMapById.remove(campaignID);
                            responseAddCampaign = "{\"success\":true}";
                            writeResponse(responseAddCampaign, outputStream);
                            System.out.println("Company '" + SolomonServer.webClientsTokensMap.get(authTokenRemoveCampaign) + " removed campaign with id " + campaignID);
                        }
                        else//wrong auth token
                        {
                            System.out.println("wrong token");
                            responseAddCampaign = "{\"success\":false}";
                            writeResponse(responseAddCampaign, outputStream);
                        }
                        break;
                    default:
                        System.out.println("FORMAT NOT CORRECT");
                        break;
                }
                System.out.println("SOCKET CLOSED");
           }
                catch (IOException ex) {
                    ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
            
        }   catch (SQLException ex) {
                ex.printStackTrace();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
    }
    public void writeResponse(String response, OutputStream outputStream)
    {
        //write the response
        PrintWriter out = new PrintWriter(outputStream);
        out.print("HTTP/1.1 200 \r\n"); // Version & status code
        out.print("Content-Type: application/json\r\n"); // The type of data
        out.print("Connection: close\r\n"); // Will close stream
        out.print("\r\n"); // End of headers
        //write the body of the response
        out.print(response);
        out.close();
    }
    public String getAlphaNumericString(int n) 
    {
        // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz"; 
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
        return sb.toString(); 
    }
    public static byte[] getImageFromDisk(String path)
    {
        File file = new File(path);
        byte[] imageBytes = new byte[(int) file.length()];
        try
        {
            //read file into bytes[]
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(imageBytes);
            fileInputStream.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return imageBytes;
    }
}
