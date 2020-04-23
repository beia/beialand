/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.beia.solomon.networkPackets.Campaign;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import data.CampaignReaction;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class WaitForWebPlatformClientsRequestsRunnable implements Runnable {

    private ServerSocket serverSocket;
    private final int TOKEN_DIMENSION = 14;
    private String campaignsPhotoPath = "CampaignsPictures\\";
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
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

//                        if(line.startsWith("OPTION")){
//                            System.out.println("CORS Check Request");
//                            writeCorsResponse(outputStream);
//                            dataReceived = true;
//                            body = "{\"requestType\":\"CORSCheckRequest\"}";
//                            break;
//                        }

                        String [] lineSplit = line.split(" ");
                        if(lineSplit[0].equals("GET") && lineSplit[1].equals("/register")){
                            String tokenVal = checkAuthToken(lineSplit[1]);
                            System.out.println("Register Page Request");
                            writePageResponse("register", body, outputStream, tokenVal);
                            dataReceived = true;
                            body = "{\"requestType\":\"GETRequestPage\"}";
                            break;
                        }
                        if(lineSplit[0].equals("GET") && lineSplit[1].equals("/login")){
                            String tokenVal = checkAuthToken(lineSplit[1]);
                            System.out.println("LogIN Page Request");
                            writePageResponse("login", body, outputStream, tokenVal);
                            dataReceived = true;
                            body = "{\"requestType\":\"GETLoginPage\"}";
                            break;
                        }
                        if(lineSplit[0].equals("GET") && lineSplit[1].startsWith("/solomon")){
                            String tokenVal = checkAuthToken(lineSplit[1]);
                            if(tokenVal == null){
                                System.out.println("Invalid authToken - Redirect");
                                writeRedirectResponse(outputStream);
                            } else {
                                System.out.println("Main Page Request");
                                writePageResponse("dashboard", body, outputStream, tokenVal);
                            }
                            dataReceived = true;
                            body = "{\"requestType\":\"GETMainPage\"}";
                            break;
                        }

                        if(lineSplit[0].equals("GET") && lineSplit[1].startsWith("/contact")){
                            String tokenVal = checkAuthToken(lineSplit[1]);
                            if(tokenVal == null){
                                System.out.println("Invalid authToken - Redirect");
                                writeRedirectResponse(outputStream);
                            } else {
                                System.out.println("Contact Page Request");
                                writePageResponse("contact", body, outputStream, tokenVal);
                            }
                            dataReceived = true;
                            body = "{\"requestType\":\"GETContactPage\"}";
                            break;
                        }

                        if(lineSplit[0].equals("GET") && lineSplit[1].startsWith("/history")){
                            String tokenVal = checkAuthToken(lineSplit[1]);
                            if(tokenVal == null){
                                System.out.println("Invalid authToken - Redirect");
                                writeRedirectResponse(outputStream);
                            } else {
                                System.out.println("History Page Request");
                                writePageResponse("history", body, outputStream, tokenVal);
                            }
                            dataReceived = true;
                            body = "{\"requestType\":\"GETHistoryPage\"}";
                            break;
                        }

                    }
                    if(dataReceived == true)
                        break;
                }
                //System.out.println(body);
                buffer.flush();

                //PARSE JSON
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(body.trim(), JsonObject.class);
                String requestType = jsonObject.get("requestType").getAsString();
                switch(requestType)
                {
                    case "login":
                        String usernameLogin = jsonObject.get("username").getAsString();
                        String passwordLogin = jsonObject.get("password").getAsString();
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
                        String token = jsonObject.get("authToken").getAsString();
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
                        String usernameRegister = jsonObject.get("username").getAsString();
                        String passwordRegister = jsonObject.get("password").getAsString();
                        String nameRegister = jsonObject.get("name").getAsString();
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
                        authToken = jsonObject.get("authToken").getAsString();
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
                            response = "{\"success\":true,\"campaigns\":\"";
                            int validCampains = 0;
                            while(campainsResultSet.next())
                            {
                                String startDate = campainsResultSet.getString("startDate");
                                String endDate = campainsResultSet.getString("endDate");
                                String currentDate = dateFormat.format(cal.getTime());
                                if(currentDate.compareTo(endDate) <= 0) //the campain is still active
                                {
                                    validCampains++;
                                    if(validCampains > 1) response += ',';
                                    response += campainsResultSet.getString("idCampaign");
                                }
                            }
                            if(validCampains == 0)
                                response = "{\"success\":true,\"campaigns\":null}";
                            else
                                response += "\"}";
                        }
                        writeResponse(response, outputStream);
                        break;
                    case "oldCampaigns"://send the active campains
                        String responseOldCampains = null;
                        String authTokenOldCampains = jsonObject.get("authToken").getAsString();
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
                            responseOldCampains = "{\"success\":true,\"oldCampaigns\":\"";
                            int oldCampains = 0;
                            while(oldCampainsResultSet.next())
                            {
                                String startDate = oldCampainsResultSet.getString("startDate");
                                String endDate = oldCampainsResultSet.getString("endDate");
                                String currentDate = dateFormat.format(cal.getTime());
                                if(currentDate.compareTo(endDate) > 0) //the campain is still active
                                {
                                    oldCampains++;
                                    if(oldCampains > 1) responseOldCampains += ',';
                                    responseOldCampains += oldCampainsResultSet.getString("idCampaign");
                                }
                            }
                            if(oldCampains == 0)
                                responseOldCampains = "{\"success\":true,\"oldCampaigns\":null}";
                            else
                                responseOldCampains += "\"}";
                        }
                        writeResponse(responseOldCampains, outputStream);
                        break;
                    case "addCampaign":
                        String authTokenAddCampaign = jsonObject.get("authToken").getAsString();
                        String responseAddCampaign;
                        if(SolomonServer.webClientsTokensMap.containsKey(authTokenAddCampaign))
                        {
                            String title = jsonObject.get("title").getAsString();
                            String category = jsonObject.get("category").getAsString();
                            String description = jsonObject.get("description").getAsString();
                            String startDate = jsonObject.get("startDate").getAsString();
                            String endDate = jsonObject.get("endDate").getAsString();
                            byte[] imageBytes = Base64.getDecoder().decode(jsonObject.get("image").getAsString());
                            String idCampain = getAlphaNumericString(10);
                            while(SolomonServer.campaignsMapById.containsKey(idCampain))
                                idCampain = getAlphaNumericString(10);
                            String idCompany = SolomonServer.webClientsTokensMap.get(authTokenAddCampaign);
                            String path = campaignsPhotoPath + idCampain + ".jpg";
                            SolomonServer.addCampain(idCampain, idCompany, title, category, description, startDate, endDate, path);
                            SolomonServer.campaignsMapById.put(idCampain, new Campaign(idCampain, idCompany, SolomonServer.companiesMap.get(idCompany), title, category, description, startDate, endDate, path));
                            System.out.println("Company '" + idCompany + " inserted campaign with id " + idCampain);
                            responseAddCampaign = "{\"success\":true, \"campaignID\":\"" + idCampain + "\"}";
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
                        authToken = jsonObject.get("authToken").getAsString();
                        campaignId = jsonObject.get("campaignID").getAsString();
                        String responseGetCampaign;
                        if(SolomonServer.webClientsTokensMap.containsKey(authToken))
                        {
                            if(SolomonServer.campaignsMapById.containsKey(campaignId))
                            {
                                Campaign campaign = SolomonServer.campaignsMapById.get(campaignId);
                                System.out.println(campaign.getPhotoPath());
                                responseGetCampaign = "{\"campaignID\":\"" + campaignId
                                        + "\",\"title\":\"" + campaign.getTitle()
                                        + "\",\"category\":\"" + campaign.getCategory()
                                        + "\",\"description\":\"" + campaign.getDescription()
                                        + "\",\"startDate\":\"" + campaign.getStartDate()
                                        + "\",\"endDate\":\"" + campaign.getEndDate()
                                        + "\",\"image\":\"" + Base64.getEncoder().encodeToString(getImageFromDisk(campaign.getPhotoPath())) + "\"}";
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
                        String authTokenUpdateCampaign = jsonObject.get("authToken").getAsString();
                        String responseUpdateCampaign;
                        if(SolomonServer.webClientsTokensMap.containsKey(authTokenUpdateCampaign))
                        {
                            String campaignID = jsonObject.get("campaignID").getAsString();
                            String title = jsonObject.get("title").getAsString();
                            String category = jsonObject.get("category").getAsString();
                            String description = jsonObject.get("description").getAsString();
                            String startDate = jsonObject.get("startDate").getAsString();
                            String endDate = jsonObject.get("endDate").getAsString();
                            byte[] imageBytes = Base64.getDecoder().decode(jsonObject.get("image").getAsString());
                            String idCompany = SolomonServer.webClientsTokensMap.get(authTokenUpdateCampaign);
                            String path = campaignsPhotoPath + campaignID + ".jpg";
                            SolomonServer.updateCampain(campaignID, title, category, description, startDate, endDate);
                            Campaign campaign = SolomonServer.campaignsMapById.get(campaignID);
                            campaign.update(title, category, description, startDate, endDate);
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
                        String authTokenRemoveCampaign = jsonObject.get("authToken").getAsString();
                        String responseRemoveCampaign;
                        if(SolomonServer.webClientsTokensMap.containsKey(authTokenRemoveCampaign))
                        {
                            String campaignID = jsonObject.get("campaignID").getAsString();
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
                    case "getViewStats":
                        String authTokenGetViewStats = jsonObject.get("authToken").getAsString();
                        String responseGetViewStats = "";
                        if(SolomonServer.webClientsTokensMap.containsKey(authTokenGetViewStats))
                        {
                            responseGetViewStats = "{\"success:\":true, \"campaignViews\":";
                            String campaignID = jsonObject.get("campaignID").getAsString();
                            if(SolomonServer.campaignReactionsMap.containsKey(campaignID)) {
                                ArrayList<CampaignReaction> campaignReactions = SolomonServer.campaignReactionsMap.get(campaignID);
                                responseGetViewStats += gson.toJson(campaignReactions) + "}";
                            }
                            else
                                responseGetViewStats += "null}";
                            writeResponse(responseGetViewStats, outputStream);
                            System.out.println("Company '" + SolomonServer.webClientsTokensMap.get(authTokenGetViewStats) + " requested campaign views for campaign: " + campaignID);
                        }
                        else//wrong auth token
                        {
                            System.out.println("wrong token");
                            responseGetViewStats = "{\"success\":false}";
                            writeResponse(responseGetViewStats, outputStream);
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
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    //    public void writeCorsResponse(OutputStream outputStream)
//    {
//        //write the response
//        PrintWriter out = new PrintWriter(outputStream);
//        out.print("HTTP/1.1 200 \r\n"); // Version & status cod
//        out.print("Access-Control-Allow-Origin: http://localhost:8080\r\n");
//        out.print("Access-Control-Allow-Methods: GET,POST\r\n");
//        out.print("Access-Control-Allow-Headers: Content-Type\r\n");
//        out.print("Connection: Keep-Alive\r\n"); // Will close stream
//        out.print("\r\n"); // End of headers
//        //write the body of the response
//        out.close();
//    }
    public String checkAuthToken(String url){
        String [] parts = url.split("\\?");
        if(parts.length > 1){
            String[] parameters = parts[1].split("&");
            for(String p:parameters){
                if(p.startsWith("authToken")){
                    String tokenValue = p.split("=")[1];
                    if(SolomonServer.webClientsTokensMap.containsKey(tokenValue)){
                        return tokenValue;
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public void writeRedirectResponse(OutputStream outputStream){
        PrintWriter out = new PrintWriter(outputStream);
        out.print("HTTP/1.1 303 \r\n"); // Redirect Status
        out.print("Location: /login\r\n"); // Redirect location
        out.print("Connection: close\r\n"); // Will close stream
        out.print("\r\n"); // End of headers
        out.close();
    }

    public void writePageResponse(String page, String requestBody, OutputStream outputStream, String authToken) throws FileNotFoundException {
        // Get Host from request
        String host = null;
        Scanner bsc = new Scanner(requestBody);
        while(bsc.hasNextLine()){
            String line = bsc.nextLine();
            if(line.startsWith("Host:")){
                String[] elems = line.split(" ");
                host = "http://" + elems[1].substring(0, elems[1].length() - 2) + "80";
            }
        }

        String name = "";
        if(authToken != null){
            String idCompany = SolomonServer.webClientsTokensMap.get(authToken);
            name = SolomonServer.companiesMap.get(idCompany);
        }


        //write the response
        PrintWriter out = new PrintWriter(outputStream);
        out.print("HTTP/1.1 200 \r\n"); // Version & status code
        out.print("Connection: close\r\n"); // Will close stream
        out.print("\r\n"); // End of headers

        File file  =  new File("C:\\Users\\Tehnic\\Desktop\\beialand\\projects\\solomon\\webapp\\templates\\" + page +".html");
        Scanner sc = new Scanner(file);
        while(sc.hasNextLine()){
            out.println(sc.nextLine().replace("<$host$>",host).replace("<$name$>", name));
        }
        out.close();
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