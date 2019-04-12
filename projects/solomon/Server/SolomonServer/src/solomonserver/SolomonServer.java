/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import runnables.ConnectClientsRunnable;
import runnables.ConnectToUnityDemoRunnable;
import runnables.ProcessDatabaseDataRunnable;

/**
 *
 * @author beia
 */
public class SolomonServer {
    
    //solomon server variables 
    public static ServerSocket serverSocket;
    public static Thread connectClients;
    public static Thread processDatabaseData;
    //unity demo server variables
    public static ServerSocket unityDemoServerSocket;
    public static Socket unityDemoSocket;
    public static Thread connectToUnityDemoThread;
    //sql server variables
    public static String error;
    public static Connection con;
    //data processing variables
    public static volatile int lastLocationEntryId = 1;
    
    
    public static void main(String[] args) throws IOException, SQLException, Exception
    {
        //connect to a mySql database
        connectToDatabase();
        
        //create a tcp serverSocket and wait for client connections
        serverSocket = new ServerSocket(8000);
        connectClients = new Thread(new ConnectClientsRunnable(serverSocket));
        connectClients.start();
        
        unityDemoServerSocket = new ServerSocket(7000);
        connectToUnityDemoThread = new Thread(new ConnectToUnityDemoRunnable(unityDemoServerSocket));
        connectToUnityDemoThread.start();
        
        //extract user location data from the database and process it at a fixed amount of time
        processDatabaseData = new Thread(new ProcessDatabaseDataRunnable());
        processDatabaseData.start();
    }
    
    public static void connectToDatabase() throws ClassNotFoundException, SQLException, Exception 
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/solomondb?autoReconnect=true&useJDBCCompliantTimezoneShift=true&useJDBCCompliantTimezoneShift=true&serverTimezone=UTC&useSSL=false", "root", "Puihoward_1423"); // nu uitati sa puneti parola corecta de root pe care o aveti setata pe serverul vostru de MySql.
            System.out.println("Successfully connected to the database!");
        }
        catch (ClassNotFoundException cnfe)
        {
            error = "ClassNotFoundException: Can't find the driver for the database.";
            throw new ClassNotFoundException(error);
        }
        catch (SQLException cnfe)
        {
            cnfe.printStackTrace();
            error = "SQLException: Can't connect to the database.";
            throw new SQLException(error);
        }
        catch (Exception e)
        {
            error = "Exception: Unexpected exception occured while we tried to connect to the database.";
            throw new Exception(error);
        }
    }
    
    public static void addUser(String username, String password, String lastName, String firstName, int age) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String userInsertionStatement = "insert into users(username, password, lastName, firstName, age) values(?,?,?,?,?)";
                PreparedStatement updateUsers = con.prepareStatement(userInsertionStatement);
                updateUsers.setString(1, username);
                updateUsers.setString(2, password);
                updateUsers.setString(3, lastName);
                updateUsers.setString(4, firstName);
                updateUsers.setInt(5, age);
                updateUsers.executeUpdate();
                System.out.println("Inserted user '" + username + "'\n password: " + password + "\nlast name: " + lastName + "\nfirst name: " + firstName + "\nage: " + age + " into the database\n\n");
            }
            catch (SQLException sqle)
            {
                error = "SqlException: Update failed; duplicates may exist.";
                throw new SQLException(error);
            }
        } 
        else
        {
            error = "Exception : Database connection was lost.";
            throw new Exception(error);
        }
    }
    
    
    public static void addLocationData(int idUser, int idStore, String zoneName, boolean zoneEntered, String time) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String userLocationInsertionStatement = "insert into userlocations(idUser, idStore, zoneName, zoneEntered, time) values(?,?,?,?,?)";
                PreparedStatement updateUserLocation = con.prepareStatement(userLocationInsertionStatement);
                updateUserLocation.setInt(1, idUser);
                updateUserLocation.setInt(2, idStore);
                updateUserLocation.setString(3, zoneName);
                updateUserLocation.setBoolean(4, zoneEntered);
                updateUserLocation.setString(5, time);
                updateUserLocation.executeUpdate();
                System.out.println("Inserted userLocation into the database\nuser id: " + idUser + "\nstore id: " + idStore + "\nzone name: " + zoneName + "\nzone entered: " + zoneEntered + "\ntime: " + time + "\n\n");
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
            }
        } 
        else
        {
            error = "Exception : Database connection was lost.";
            throw new Exception(error);
        }
    }
    
    
    
    public static void addZoneTimeData(int idUser, int idStore, String[] zonesTime) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String userRoomTimeInsertionStatementFirstPart = "insert into userroomtime(idUser, idStore";
                String userRoomTimeInsertionStatementLastPart = "values(" + idUser + ", " + idStore;
                String outputFeedBackString;
                Statement updateRoomTimeData = con.createStatement();
                outputFeedBackString = "Inserted user room time data ";
                for(int i = 0; i < zonesTime.length; i++)
                {
                    userRoomTimeInsertionStatementFirstPart += ", room" + (i + 1) + "Time";
                    userRoomTimeInsertionStatementLastPart += ", '" + zonesTime[i] + "'";
                    outputFeedBackString += "room" + (i + 1) + " time = " + zonesTime[i];
                }
                userRoomTimeInsertionStatementFirstPart += ") ";
                userRoomTimeInsertionStatementLastPart += ")";
                
                String statementString = userRoomTimeInsertionStatementFirstPart + userRoomTimeInsertionStatementLastPart;
                updateRoomTimeData.executeUpdate(statementString);
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
            }
        } 
        else
        {
            error = "Exception : Database connection was lost.";
            throw new Exception(error);
        }
    }
    
    
    public static void updateZoneTimeData(int idUser, int idStore, String zoneName, String zoneTime) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                Statement updateStatement = con.createStatement();
                String userRoomTimeUpdateStatement = "update userroomtime set " + zoneName + "='" + zoneTime + "' where idUser=" + idUser + " and idStore=" + idStore;
                updateStatement.executeUpdate(userRoomTimeUpdateStatement);
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
            }
        } 
        else
        {
            error = "Exception : Database connection was lost.";
            throw new Exception(error);
        }
    }
    
    
    public static ResultSet getUserDataFromDatabase(String tabelName, String username) throws SQLException, Exception
    {
        ResultSet rs = null;
        try
        {
            // Execute query
            String queryString = ("select * from " + tabelName + " where username = '" + username + "';");
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(queryString); //sql exception
        } 
        catch (SQLException sqle)
        {
            error = "SQLException: Query was not possible.";
            sqle.printStackTrace();
            throw new SQLException(error);
        }
        catch (Exception e)
        {
            error = "Exception occured when we extracted the data.";
            throw new Exception(error);
        }
        return rs;
    }
    
    
    public static ResultSet getRoomTimeDataFromDatabase(String tableName, int idUser, int idStore) throws SQLException, Exception
    {
        ResultSet rs = null;
        try
        {
            // Execute query
            String queryString = ("select * from " + tableName + " where idUser=" + idUser + " and idStore=" + idStore);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(queryString); //sql exception
        } 
        catch (SQLException sqle)
        {
            error = "SQLException: Query was not possible.";
            sqle.printStackTrace();
            throw new SQLException(error);
        }
        catch (Exception e)
        {
            error = "Exception occured when we extracted the data.";
            throw new Exception(error);
        }
        return rs;
    }
    
    
    public static ResultSet getTableData(String tabelName) throws SQLException, Exception
    {
        ResultSet rs = null;
        try
        {
            // Execute query
            String queryString = ("select * from " + tabelName + ";");
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(queryString); //sql exception
        } 
        catch (SQLException sqle)
        {
            error = "SQLException: Query was not possible.";
            sqle.printStackTrace();
            throw new SQLException(error);
        }
        catch (Exception e)
        {
            error = "Exception occured when we extracted the data.";
            throw new Exception(error);
        }
        return rs;
    }
    
    
    public static ResultSet getNewTableData(String tabelName, String idName, int lastId) throws SQLException, Exception
    {
        ResultSet rs = null;
        try
        {
            // Execute query
            String queryString = ("select * from "+ tabelName + " where " + idName + " > " + lastId);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(queryString); //sql exception
        } 
        catch (SQLException sqle)
        {
            error = "SQLException: Query was not possible.";
            sqle.printStackTrace();
            throw new SQLException(error);
        }
        catch (Exception e)
        {
            error = "Exception occured when we extracted the data.";
            throw new Exception(error);
        }
        return rs;
    }

}