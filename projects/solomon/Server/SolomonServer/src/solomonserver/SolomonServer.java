/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonserver;

import com.example.solomon.networkPackets.Beacon;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
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
    public static HashMap<String, Beacon> beacons;
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
        //init variables
        beacons = new HashMap<>();
        
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
    
    public static void addEstimoteBeacon(String id, String label, String company) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String roomInsertionStatement = "insert into beacons(id, label, company) values(?,?,?)";
                PreparedStatement updateRooms = con.prepareStatement(roomInsertionStatement);
                updateRooms.setString(1, id);
                updateRooms.setString(2, label);
                updateRooms.setString(3, company);
                updateRooms.executeUpdate();
                System.out.println("Inserted Estimote beacon into the database:\nid: " + id + "\nlabel: " + label + "\ncompany: " + company + "\n\n");
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
    
    public static void addKontaktBeacon(String id, String label, String company, String major, String minor) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String roomInsertionStatement = "insert into beacons(id, label, company, major, minor) values(?,?,?,?,?)";
                PreparedStatement updateRooms = con.prepareStatement(roomInsertionStatement);
                updateRooms.setString(1, id);
                updateRooms.setString(2, label);
                updateRooms.setString(3, company);
                updateRooms.setString(4, major);
                updateRooms.setString(5, minor);
                updateRooms.executeUpdate();
                System.out.println("Inserted Kontakt beacon into the database:\nid: " + id + "\nlabel: " + label + "\ncompany: " + company + "\nmajor: " + major + "\nminor: " + minor +"\n\n");
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
    
    
    
    public static void addZoneTimeData(int idUser, int idStore, String roomName, long timeSeconds) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String statementString = "insert into userroomtime(idUser, idStore, roomName, timeSeconds) values(?, ?, ?, ?)";
                PreparedStatement addZoneTimeStatement = con.prepareStatement(statementString);
                addZoneTimeStatement.setInt(1, idUser);
                addZoneTimeStatement.setInt(2, idStore);
                addZoneTimeStatement.setString(3, roomName);
                addZoneTimeStatement.setLong(4, timeSeconds);
                addZoneTimeStatement.executeUpdate();
                System.out.println("Inserted user room time into the database\n idUser: " + idUser + "\nidStore: " + idStore + "\nroomName: " + roomName + "\ntimeSeconds: " + timeSeconds + "\n\n");
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
    
    
    public static void updateZoneTimeData(int idUser, int idStore, String roomName, long timeSeconds) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                Statement updateStatement = con.createStatement();
                String userRoomTimeUpdateStatement = "update userroomtime set timeSeconds = '" + timeSeconds + "' where idUser = '" + idUser + "' and idStore = '" + idStore + "' and roomName = '" + roomName + "';";
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
    
    
    public static ResultSet getRoomsDataByUserId(String tableName, int idUser, int idStore) throws SQLException, Exception
    {
        ResultSet rs = null;
        try
        {
            // Execute query
            String queryString = ("select * from " + tableName + " where idUser = '" + idUser + "' and idStore = '" + idStore + "';");
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
    
    
    public static ResultSet getRoomDataByUserId(String tableName, int idUser, int idStore, String roomName) throws SQLException, Exception
    {
        ResultSet rs = null;
        try
        {
            // Execute query
            String queryString = ("select * from " + tableName + " where idUser = '" + idUser + "' and idStore = '" + idStore + "' and roomName = '" + roomName + "';");
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
    
    public static void deleteTableData(String tableName) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                Statement deleteStatement = con.createStatement();
                String statementString = "delete from " + tableName;
                deleteStatement.executeUpdate(statementString);
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
    
    public static void deleteBeacon(String label) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                Statement deleteStatement = con.createStatement();
                String statementString = "delete from beacons where label = '" + label + "'";
                deleteStatement.executeUpdate(statementString);
                System.out.println("Removed beacon '" + label + "' from the database");
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
    
    
}