/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import SolomonPartnersNetworkObjects.Mall;
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
                //finished getting the users data from the database
                
                //get the malls data from the database
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
    
