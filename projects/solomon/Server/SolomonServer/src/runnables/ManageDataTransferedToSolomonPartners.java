/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

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
public class ManageDataTransferedToSolomonPartners implements Runnable
{
    private HashMap<String, ArrayList<Object>> solomonPartnersData;
    public ManageDataTransferedToSolomonPartners(HashMap<String, ArrayList<Object>> solomonPartnersData)
    {
        this.solomonPartnersData = solomonPartnersData;
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
                ResultSet usersResultSet = SolomonServer.getTableData("users");
                UserData 
                Thread.sleep(300000);
            }
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(ManageDataTransferedToSolomonPartners.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ManageDataTransferedToSolomonPartners.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
    
