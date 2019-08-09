/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import SolomonPartnersNetworkObjects.Mall;
import SolomonPartnersNetworkObjects.SpecialOffer;
import SolomonPartnersNetworkObjects.Store;
import SolomonPartnersNetworkObjects.User;
import SolomonPartnersNetworkObjects.UserStoreTime;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import solomonserver.SolomonServer;


/**
 *
 * @author beia
 */
public class WaitForPartnersConnectionRunnable implements Runnable
{
    private ServerSocket serverSocket;
    public WaitForPartnersConnectionRunnable(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }
    @Override
    public void run()
    {
        try 
        {
            while(true)
            {
                System.out.println("Waitig for partners http requests...");
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //read the request
                String line;
                String desiredData = "";
                while ((line = in.readLine()) != null)
                {
                    if (line.length() == 0)
                        break;
                    System.out.println(line);
                    String[] data = line.trim().split(":");
                    if(data[0].trim().equals("Data"))
                        desiredData = data[1].trim();
                }
                
                //write the response
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.print("HTTP/1.1 200 \r\n"); // Version & status code
                out.print("Content-Type: application/json\r\n"); // The type of data
                out.print("Connection: close\r\n"); // Will close stream
                out.print("\r\n"); // End of headers
                //write the body of the response
                switch(desiredData)
                {
                    case "users":
                        JSONArray usersJsonArray = new JSONArray();
                        for(User user: SolomonServer.partnersDataUsers)
                        {
                            //create a json object for each user
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("idUser", user.getId());
                            jsonObject.put("username", user.getUsername());
                            jsonObject.put("lastName", user.getLastName());
                            jsonObject.put("firstName", user.getFirstName());
                            jsonObject.put("age", user.getAge());
                            StringBuilder preferencesStringBuilder = new StringBuilder();
                            ArrayList<String> preferences = user.getPreferences();
                            if(!preferences.isEmpty())
                            {
                                for(int i = 0; i < preferences.size() - 1; i++)
                                {
                                    preferencesStringBuilder.append(preferences.get(i));
                                    preferencesStringBuilder.append(", ");
                                }
                                preferencesStringBuilder.append(preferences.get(preferences.size() - 1));
                            }
                            jsonObject.put("preferences", preferencesStringBuilder.toString());
                            JSONArray storesTimeJsonArray = new JSONArray();
                            ArrayList<UserStoreTime> userStoresTime = user.getStoresTime();
                            for(UserStoreTime userStoreTime : userStoresTime)
                            {
                                JSONObject userStoreTimeJsonObject = new JSONObject();
                                userStoreTimeJsonObject.put("idMall", userStoreTime.getIdMall());
                                userStoreTimeJsonObject.put("idStore", userStoreTime.getIdStore());
                                userStoreTimeJsonObject.put("storeName", userStoreTime.getStoreName());
                                userStoreTimeJsonObject.put("timeSeconds", userStoreTime.getSeconds());
                                storesTimeJsonArray.add(userStoreTimeJsonObject);
                            }
                            jsonObject.put("stores time", storesTimeJsonArray);
                            //add the json object into the json array
                            usersJsonArray.add(jsonObject);
                        }
                        //write the json array in the body of the http response
                        out.print(usersJsonArray.toJSONString());
                        System.out.print(usersJsonArray.toJSONString());
                        break;
                    case "malls":
                        JSONArray mallsJsonArray = new JSONArray();
                        for(Mall mall : SolomonServer.partnersDataMalls)
                        {
                            JSONObject mallJsonObject = new JSONObject();
                            mallJsonObject.put("idMall", mall.getMallId());
                            mallJsonObject.put("name", mall.getName());
                            JSONArray storesJsonArray = new JSONArray();
                            ArrayList<Store> stores = mall.getStores();
                            for(Store store : stores)
                            {
                                JSONObject storeJsonObject = new JSONObject();
                                storeJsonObject.put("idStore", store.getIdStore());
                                storeJsonObject.put("idMall", store.getIdMall());
                                storeJsonObject.put("name", store.getName());
                                StringBuilder categoriesStringBuilder = new StringBuilder();
                                ArrayList<String> categories = store.getCategories();
                                if(!categories.isEmpty())
                                {
                                    for(int i = 0; i < categories.size() - 1; i++)
                                    {
                                        categoriesStringBuilder.append(categories.get(i));
                                        categoriesStringBuilder.append(", ");
                                    }
                                    categoriesStringBuilder.append(categories.get(categories.size() - 1));
                                }
                                storeJsonObject.put("categories", categoriesStringBuilder.toString());
                                JSONArray specialOffersJsonArray = new JSONArray();
                                ArrayList<SpecialOffer> specialOffers = store.getSpecialOffers();
                                for(SpecialOffer specialOffer : specialOffers)
                                {
                                    JSONObject specialOfferJsonObject = new JSONObject();
                                    specialOfferJsonObject.put("idStore", specialOffer.getIdStore());
                                    specialOfferJsonObject.put("description", specialOffer.getDescription());
                                    StringBuilder specialOffersCategoriesStringBuilder = new StringBuilder();
                                    ArrayList<String> specialOffersCategories = specialOffer.getCategories();
                                    if(!specialOffersCategories.isEmpty())
                                    {
                                        for(int j = 0; j < specialOffersCategories.size() - 1; j++)
                                        {
                                            specialOffersCategoriesStringBuilder.append(specialOffersCategories.get(j));
                                            specialOffersCategoriesStringBuilder.append(", ");
                                        }
                                        specialOffersCategoriesStringBuilder.append(specialOffersCategories.get(specialOffersCategories.size() - 1));
                                    }
                                    specialOfferJsonObject.put("categories", specialOffersCategoriesStringBuilder.toString());
                                    specialOffersJsonArray.add(specialOfferJsonObject);
                                }
                                storeJsonObject.put("special offers", specialOffersJsonArray);
                                storesJsonArray.add(storeJsonObject);
                            }
                            mallJsonObject.put("stores", storesJsonArray);
                            mallsJsonArray.add(mallJsonObject);
                        }
                        out.print(mallsJsonArray.toJSONString());
                        System.out.print(mallsJsonArray.toJSONString());
                        break;
                    default:
                        break;
                }
                
                
                // Close socket, breaking the connection to the client, and
                // closing the input and output streams
                out.close(); // Flush and close the output stream
                in.close(); // Close the input stream
                socket.close(); // Close the socket itself
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(WaitForPartnersConnectionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
}
