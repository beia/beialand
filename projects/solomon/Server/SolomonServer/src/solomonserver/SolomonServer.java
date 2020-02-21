/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonserver;

import SolomonPartnersNetworkObjects.Mall;
import SolomonPartnersNetworkObjects.User;
import SolomonPartnersNetworkObjects.UserStoreTime;
import com.beia.solomon.networkPackets.Beacon;
import com.beia.solomon.networkPackets.Coordinates;
import com.beia.solomon.networkPackets.Store;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import runnables.ConnectClientsRunnable;
import runnables.ConnectToUnityDemoRunnable;
import runnables.ManageDataTransferedToSolomonPartnersRunnable;
import runnables.ProcessDatabaseDataRunnable;
import runnables.WaitForPartnersConnectionRunnable;
import runnables.WaitForWebPlatformClientsRequestsRunnable;

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
    public static HashMap<Integer, com.beia.solomon.networkPackets.Mall> malls;
    
    //Solomon partners variables
    public static ServerSocket partnersServerSocket;
    public static volatile ArrayList<User> partnersDataUsers;
    public static volatile ArrayList<UserStoreTime> partnersDataUsersStoreTime;
    public static volatile ArrayList<Mall> partnersDataMalls;    
    public static Thread manageDataTransferedToSolomonPartners;
    public static Thread waitForPartnersHttpRequests;
    
    //Solomon web platform variables
    public static ServerSocket webPlatformServerSocket;
    public static Thread waitForSolomonWebClientsRequests;
    public static HashSet<String> webClientsTokensSet;
    public static HashMap<Integer, String> webClientsTokensMap;
    
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
        malls = new HashMap<>();
        partnersDataUsers = new ArrayList<>();
        partnersDataUsersStoreTime = new ArrayList<>();
        partnersDataMalls = new ArrayList<>();
        
        //connect to a mySql database
        connectToDatabase();
        
        //create a tcp server socket and wait for client connections
        serverSocket = new ServerSocket(8000);
        connectClients = new Thread(new ConnectClientsRunnable(serverSocket));
        connectClients.start();
        
        //cerate a tcp server socket and wait for web clients connections
        webPlatformServerSocket = new ServerSocket(7000);
        waitForSolomonWebClientsRequests = new Thread(new WaitForWebPlatformClientsRequestsRunnable(webPlatformServerSocket));
        waitForSolomonWebClientsRequests.start();
        
        //create a tcp server socket and wait for Solomon partners connection
        partnersServerSocket = new ServerSocket(9000);
        waitForPartnersHttpRequests = new Thread(new WaitForPartnersConnectionRunnable(partnersServerSocket));
        waitForPartnersHttpRequests.start();
        
        //create the Unity tcp server socket and wait for client connections
        unityDemoServerSocket = new ServerSocket(10000);
        connectToUnityDemoThread = new Thread(new ConnectToUnityDemoRunnable(unityDemoServerSocket));
        connectToUnityDemoThread.start();
        
        //extract user location data from the database and process it at a fixed amount of time
        processDatabaseData = new Thread(new ProcessDatabaseDataRunnable());
        processDatabaseData.start();
        
        //get the data to be shared with the Solomon partners from the database
        manageDataTransferedToSolomonPartners = new Thread(new ManageDataTransferedToSolomonPartnersRunnable(partnersDataUsers, partnersDataUsersStoreTime, partnersDataMalls));
        manageDataTransferedToSolomonPartners.start();
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
    
    public static void addUserPreference(int userId, String preference) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String userPreferenceInsertionStatement = "insert into userpreferences(idUser,category) values(?,?)";
                PreparedStatement updateUsersPreferences = con.prepareStatement(userPreferenceInsertionStatement);
                updateUsersPreferences.setInt(1, userId);
                updateUsersPreferences.setString(2, preference);
                updateUsersPreferences.executeUpdate();
                System.out.println("Inserted user preference for idUser:'" + userId + "'\n category: " + preference + " into the database\n\n");
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
    
    public static void addEstimoteBeacon(String id, String label, int mallId, String company, Coordinates coordinates, int layer, int floor) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                System.out.println(mallId);
                // create a prepared SQL statement
                String roomInsertionStatement = "insert into beacons(id, label, idMall, company, latitude, longitude, layer, floor) values(?,?,?,?,?,?,?,?)";
                PreparedStatement updateRooms = con.prepareStatement(roomInsertionStatement);
                updateRooms.setString(1, id);
                updateRooms.setString(2, label);
                updateRooms.setInt(3, mallId);
                updateRooms.setString(4, company);
                updateRooms.setDouble(5, coordinates.getLatitude());
                updateRooms.setDouble(6, coordinates.getLongitude());
                updateRooms.setInt(7, layer);
                updateRooms.setInt(8, floor);
                updateRooms.executeUpdate();
                System.out.println("Inserted Estimote beacon into the database:\nid: " + id + "\nlabel: " + label + "\nidMall: " + mallId + "\ncompany: " + company + "\n\n");
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
    
    public static void addKontaktBeacon(String id, String label, int mallId, String company, String major, String minor, Coordinates coordinates, int layer, int floor) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String beaconInsertionStatement = "insert into beacons(id, label, idMall, company, major, minor, latitude, longitude, layer, floor) values(?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement updateBeacons = con.prepareStatement(beaconInsertionStatement);
                updateBeacons.setString(1, id);
                updateBeacons.setString(2, label);
                updateBeacons.setInt(3, mallId);
                updateBeacons.setString(4, company);
                updateBeacons.setString(5, major);
                updateBeacons.setString(6, minor);
                updateBeacons.setDouble(7, coordinates.getLatitude());
                updateBeacons.setDouble(8, coordinates.getLongitude());
                updateBeacons.setInt(9, layer);
                updateBeacons.setInt(10, floor);
                updateBeacons.executeUpdate();
                System.out.println("Inserted Kontakt beacon into the database:\nid: " + id + "\nlabel: " + label + "\nidMall: " + mallId +"\ncompany: " + company + "\nmajor: " + major + "\nminor: " + minor + "\nlatitude: " + coordinates.getLatitude() + "\nlongitude: " + coordinates.getLongitude() + "\nlayer: " + layer + "\nfloor: " + floor + "\n\n");
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
    
    
    public static void addLocationData(int idUser,String beaconId, String beaconLabel, int idMall, boolean zoneEntered, String time) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String userLocationInsertionStatement = "insert into userlocations(idUser, idBeacon, beaconLabel, idMall, zoneEntered, time) values(?,?,?,?,?,?)";
                PreparedStatement updateUserLocation = con.prepareStatement(userLocationInsertionStatement);
                updateUserLocation.setInt(1, idUser);
                updateUserLocation.setString(2, beaconId);
                updateUserLocation.setString(3, beaconLabel);
                updateUserLocation.setInt(4, idMall);
                updateUserLocation.setBoolean(5, zoneEntered);
                updateUserLocation.setString(6, time);
                updateUserLocation.executeUpdate();
                System.out.println("Inserted user location into the database\nuser id: " + idUser + "\nbeacon id: " + beaconId +  "\nbeacon label: " + beaconLabel + "\nmall id: " + idMall + "\nzone entered: " + zoneEntered + "\ntime: " + time + "\n\n");
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
    
    
    
    public static void addBeaconTimeData(int idUser, String beaconId, String beaconLabel, int idMall, long timeSeconds) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String statementString = "insert into userbeacontime(idUser, idBeacon, beaconLabel, idMall, timeSeconds) values(?, ?, ?, ?, ?)";
                PreparedStatement addZoneTimeStatement = con.prepareStatement(statementString);
                addZoneTimeStatement.setInt(1, idUser);
                addZoneTimeStatement.setString(2, beaconId);
                addZoneTimeStatement.setString(3, beaconLabel);
                addZoneTimeStatement.setInt(4, idMall);
                addZoneTimeStatement.setLong(5, timeSeconds);
                addZoneTimeStatement.executeUpdate();
                System.out.println("Inserted user room time into the database\n idUser: " + idUser + "\nbeacon id: " + beaconId + "\nbeaconLabel: " + beaconLabel + "\nidMall: " + idMall + "\ntimeSeconds: " + timeSeconds + "\n\n");
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
    
    
    public static void updateBeaconTimeData(int idUser, String beaconLabel, int mallId, long timeSeconds) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                Statement updateStatement = con.createStatement();
                String userRoomTimeUpdateStatement = "update userbeacontime set timeSeconds = '" + timeSeconds + "' where idUser = '" + idUser + "' and beaconLabel = '" + beaconLabel + "' and idMall = '" + mallId + "';";
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
    
    
    public static ResultSet getBeaconTimeByUserId(int idUser, String idBeacon, int idMall) throws SQLException, Exception
    {
        ResultSet rs = null;
        try
        {
            // Execute query
            String queryString = ("select * from userbeacontime where idUser = '" + idUser + "' and idBeacon = '" + idBeacon + "' and  idMall = '" + idMall + "';");
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
    
    public static ResultSet getBeaconsTimeByUserId(int idUser, int idMall) throws SQLException, Exception
    {
        ResultSet rs = null;
        try
        {
            // Execute query
            String queryString = ("select * from userbeacontime where idUser = '" + idUser + "' and  idMall = '" + idMall + "';");
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
    
    public static ResultSet getTableDataById(String tableName, String idColumnName, int id)
    {
        ResultSet resultSet = null;
        if(con != null)
        {
            try
            {
                String queryString = "select * from " + tableName + " where " + idColumnName + " = '" + id + "';";
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = stmt.executeQuery(queryString);
            }
            catch(SQLException sqle)
            {
                sqle.printStackTrace();
            }
        }
        return resultSet;
    }
    public static ResultSet getTableDataByIds(String tableName, String[] idsColumnName, int[] ids)
    {
        ResultSet resultSet = null;
        if(con != null)
        {
            try
            {
                String queryString = "select * from " + tableName + " where ";
                for(int i = 0; i < idsColumnName.length - 1; i++)
                {
                    queryString += idsColumnName[i] + " = '" + ids[i] + "' AND ";
                }
                queryString += idsColumnName[idsColumnName.length - 1] + " = '" + ids[ids.length - 1] + "';";
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = stmt.executeQuery(queryString);
            }
            catch(SQLException sqle)
            {
                sqle.printStackTrace();
            }
        }
        return resultSet;
    }
    
    
    public static ResultSet getNewLocationData(String tabelName, String idName, int lastId) throws SQLException, Exception
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
    
    public static ResultSet getStoreTimeByUserId(int idUser)
    {
        ResultSet resultSet= null;
        if(con != null)
        {
            try
            {
                String queryString = "SELECT a.idMall idMall, a.idStores idStore, a.name storeName, b.timeseconds timeseconds FROM stores a, userbeacontime b WHERE b.idUser = '" + idUser + "' AND a.idBeacon = b.idBeacon;";
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = stmt.executeQuery(queryString); //sql exception
            }
            catch(SQLException sqle)
            {
                sqle.printStackTrace();
            }
        }
        return resultSet;
    }
    
    public static ResultSet getPreferencesByUserId(int idUser) throws Exception
    {
        ResultSet resultSet = null;
        if(con != null)
        {
            try
            {
                String gueryString = "SELECT * FROM userpreferences WHERE idUser = '" + idUser + "';";
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = stmt.executeQuery(gueryString); //sql exception
            }
            catch(SQLException sqle)
            {
                
            }
        }
        else
        {
            error = "Exception : Database connection was lost.";
            throw new Exception(error);
        }
        return resultSet;
    }
    
    public static void deleteTableData(String tableName) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
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
    
    public static void updateUsername(int id, String username) throws SQLException, Exception
    {
        if(con != null)
        {
            try
            {
                // create a prepared SQL statement
                Statement usernameUpdateStatement = con.createStatement();
                String updateStatementString = "update users set username = '" + username + "' where idusers = '" + id +"';";
                usernameUpdateStatement.executeUpdate(updateStatementString);
                System.out.println("------------------------------\nUpdated username:\n userId: " + id + "\nusername: " + username + "\n------------------------------");
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
    
    public static void updatePassword(int id, String password) throws SQLException, Exception
    {
        if(con != null)
        {
            try
            {
                // create a prepared SQL statement
                Statement usernameUpdateStatement = con.createStatement();
                String updateStatementString = "update users set password = '" + password + "' where idusers = '" + id +"';";
                usernameUpdateStatement.executeUpdate(updateStatementString);
                System.out.println("------------------------------\nUpdated password:\n userId: " + id + "\npassword: " + password + "\n------------------------------");
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
    
    public static void updateAge(int id, int age) throws SQLException, Exception
    {
        if(con != null)
        {
            try
            {
                // create a  SQL statement
                Statement usernameUpdateStatement = con.createStatement();
                String updateStatementString = "update users set age = '" + age + "' where idusers = '" + id +"';";
                usernameUpdateStatement.executeUpdate(updateStatementString);
                System.out.println("------------------------------\nUpdated age:\n userId: " + id + "\nage: " + age + "\n------------------------------");
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
    
    public static void saveImagePath(String picturePath, int userId) throws SQLException, Exception
    {
        if(con != null)
        {
            try
            {
                //create a update SQl statement
                Statement savePictureStatement = con.createStatement();
                //replace the slashes with double slashes in the update statement because the mysql interprets only \\ as \
                picturePath = picturePath.replace("\\", "\\\\");
                System.out.println(picturePath);
                String statementString = "update users set picture = '" + picturePath + "' where idusers = '" + userId + "';";
                savePictureStatement.executeUpdate(statementString);
            }
            catch(SQLException sqle)
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
    
    public static String getUserProfilePicturePath(int userId) throws Exception
    {
        ResultSet resultSet = null;
        if(con != null)
        {
            try
            {
                //create a sql query statement
                Statement getPicturePathStatement = con.createStatement();
                String queryString = "select picture from users where idusers = '" + userId + "';";
                resultSet = getPicturePathStatement.executeQuery(queryString);
                if (!resultSet.isBeforeFirst())
                {    
                    //no profile picture
                    return "No profile picture";
                }
                else
                {
                    resultSet.next();
                    return resultSet.getString("picture");
                }
                
            }
            catch(SQLException sqle)
            {
                sqle.printStackTrace();
            }
        }
        else
        {
            error = "Exception : Database connection was lost.";
            throw new Exception(error);
        }
        return null;
    }
}