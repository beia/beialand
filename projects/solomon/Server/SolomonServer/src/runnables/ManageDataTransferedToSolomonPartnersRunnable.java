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
import static java.lang.Thread.sleep;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class ManageDataTransferedToSolomonPartnersRunnable implements Runnable
{
    private ArrayList<User> partnersDataUsers;
    private ArrayList<UserStoreTime> partnersDataUsersStoreTime;
    private ArrayList<Mall> partnersDataMalls;
    public ManageDataTransferedToSolomonPartnersRunnable(ArrayList<User> partnersDataUsers, ArrayList<UserStoreTime> partnersDataUsersStoreTime, ArrayList<Mall> partnersMalls)
    {
        this.partnersDataUsers = partnersDataUsers;
        this.partnersDataUsersStoreTime = partnersDataUsersStoreTime;
        this.partnersDataMalls = partnersMalls;
    }
    @Override
    public void run() 
    {
        try
        {
            //get the data from the database every 5 minutes and save it into a hashmap
            //hashmap format key: "table name" arrayList<table data objects>
            while(true)
            {
                //get the users data from the database
                this.partnersDataUsers = new ArrayList<>();
                ResultSet userResultSet = SolomonServer.getTableData("users");
                int idUser, age;
                String username, lastName, firstName;
                while(userResultSet.next())
                {
                    idUser = userResultSet.getInt("idusers");
                    username = userResultSet.getString("username");
                    lastName = userResultSet.getString("lastName");
                    firstName = userResultSet.getString("firstName");
                    age = userResultSet.getInt("age");
                    
                    //get user prefeences from the daabase
                    ArrayList<String> preferences = new ArrayList<>();
                    ResultSet userPreferencesResultSet = SolomonServer.getTableDataById("userpreferences", "idUser", idUser);
                    while(userPreferencesResultSet.next())
                    {
                        preferences.add(userPreferencesResultSet.getString("category"));
                    }
                    User user = new User(idUser, username, lastName, firstName, age, preferences);
                    this.partnersDataUsers.add(user);
                }
                SolomonServer.partnersDataUsers = this.partnersDataUsers;
                //finished getting the users data from the database
                
                //get the malls data from the database
                this.partnersDataMalls = new ArrayList<>();
                ResultSet mallResultSet = SolomonServer.getTableData("malls");
                int idMall;
                String mallName;
                while(mallResultSet.next())
                {
                    idMall = mallResultSet.getInt("idMalls");
                    mallName = mallResultSet.getString("name");
                    ResultSet storesResultSet = SolomonServer.getTableData("stores");
                    ArrayList<Store> stores = new ArrayList<>();
                    int idStore;
                    String storeName;
                    while(storesResultSet.next())
                    {
                        idStore = storesResultSet.getInt("idStores");
                        storeName = storesResultSet.getString("name");
                        ResultSet storeCategoriesResultSet = SolomonServer.getTableDataById("storecategories", "idStore", idStore);
                        ArrayList<String> categories = new ArrayList<>();
                        while(storeCategoriesResultSet.next())
                        {
                            categories.add(storeCategoriesResultSet.getString("category"));
                        }
                        ResultSet specialOffersResultSet = SolomonServer.getTableDataById("storespecialoffers", "idStore", idStore);
                        ArrayList<SpecialOffer> specialOffers = new ArrayList<>();
                        while(specialOffersResultSet.next())
                        {
                            int idSpecialOffer = specialOffersResultSet.getInt("idstoreSpecialOffers");
                            String description = specialOffersResultSet.getString("description");
                            ArrayList<String> specialOffersCategories = new ArrayList<>();
                            String[] idsColumnName = new String[2];
                            idsColumnName[0] = "idStore";
                            idsColumnName[1] = "idSpecialOffer";
                            int[] ids = new int[2];
                            ids[0] = idStore;
                            ids[1] = idSpecialOffer;
                            ResultSet specialOffersCategoriesResultSet = SolomonServer.getTableDataByIds("specialofferscategories", idsColumnName, ids);
                            while(specialOffersCategoriesResultSet.next())
                            {
                                specialOffersCategories.add(specialOffersCategoriesResultSet.getString("category"));
                            }
                            SpecialOffer specialoffer = new SpecialOffer(idStore, description, specialOffersCategories);
                            specialOffers.add(specialoffer);
                        }
                        Store store = new Store(idStore, idMall, storeName, categories, specialOffers);
                        stores.add(store);
                    }
                    Mall mall = new Mall(idMall, mallName, stores);
                    this.partnersDataMalls.add(mall);
                }
                SolomonServer.partnersDataMalls = this.partnersDataMalls;
                //finished getting the malls data from the database
                Thread.sleep(300000);
            }
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(ManageDataTransferedToSolomonPartnersRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ManageDataTransferedToSolomonPartnersRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
    
