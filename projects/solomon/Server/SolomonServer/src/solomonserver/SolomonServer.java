/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonserver;

import com.beia.solomon.networkPackets.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.CampaignReaction;
import data.Notification;
import runnables.*;

import javax.swing.plaf.nimbus.State;

/**
 *
 * @author beia
 */
public class SolomonServer {
    
    //solomon server variables 
    public static ServerSocket serverSocket;
    public static Thread connectClients;
    public static HashMap<String, Beacon> beaconsMap;
    public static HashMap<Integer, HashMap<String, BeaconTime>> usersBeaconTimeMap;//key:userId value:hashmap:key:beaconId value:beaconTime
    public static volatile HashMap<String, Long> totalBeaconTime;//key:beaconId value:beaconTime
    public static HashMap<Integer, Mall> mallsMap;
    public static volatile HashMap<String, String> companiesMap;//key:id value:companyName
    public static volatile HashMap<String, Campaign> campaignsMapById;//key:id value:campaign
    public static volatile HashMap<String, ArrayList<Campaign>> campaignsMapByCompanyId;//key:companyId value:array of campaigns
    public static volatile HashMap<Integer, Queue<Notification>> notificationsMap;//key:userId value:notifications
    public static volatile HashMap<Integer, Integer> parkingSpacesAvailableMap;//key:mallId value:free parking spaces percentage
    
    //Solomon web platform variables
    public static ServerSocket webPlatformServerSocket;
    public static Thread waitForSolomonWebClientsRequests;
    public static HashMap<String, String> webClientsTokensMap;//key: token, value: username
    public static HashMap<String, ArrayList<CampaignReaction>> campaignReactionsMap;
    
    //sql server variables
    public static String error;
    public static Connection con;

    
    public static void main(String[] args) throws IOException, SQLException, Exception
    {
        //init variables
        beaconsMap = new HashMap<>();
        usersBeaconTimeMap = new HashMap<>();
        totalBeaconTime = new HashMap<>();
        mallsMap = new HashMap<>();
        companiesMap = new HashMap<>();
        campaignsMapById = new HashMap<>();
        campaignsMapByCompanyId = new HashMap<>();
        notificationsMap = new HashMap<>();
        webClientsTokensMap = new HashMap<>();
        parkingSpacesAvailableMap = new HashMap<>();
        campaignReactionsMap = new HashMap<>();
        parkingSpacesAvailableMap.put(1, 20);//20 percent free parking spaces
        parkingSpacesAvailableMap.put(2, 30);
        parkingSpacesAvailableMap.put(3, 50);
        parkingSpacesAvailableMap.put(4, 40);
        parkingSpacesAvailableMap.put(5, 70);

        //connect to a mySql database
        connectToDatabase();

        //get the data from the database
        getBeacons();
        getUserBeaconTime();
        getMalls();
        getCompanies();
        getCampaigns();
        getCampaignsReactions();

        //update campaigns
        new Thread(new UpdateCampaignsForUsersRunnable(companiesMap, campaignsMapById, campaignsMapByCompanyId)).start();

        //update notifications
        new Thread(new UpdateNotificationsRunnable(notificationsMap)).start();
        
        //create a tcp server socket and wait for client connections
        serverSocket = new ServerSocket(7000);
        connectClients = new Thread(new ConnectClientsRunnable(serverSocket));
        connectClients.start();
        
        //cerate a tcp server socket and wait for web clients connections
        webPlatformServerSocket = new ServerSocket(8000);
        waitForSolomonWebClientsRequests = new Thread(new WaitForWebPlatformClientsRequestsRunnable(webPlatformServerSocket));
        waitForSolomonWebClientsRequests.start();
    }
    
    public static void connectToDatabase() throws ClassNotFoundException, SQLException, Exception 
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/solomondb?autoReconnect=true&useJDBCCompliantTimezoneShift=true&useJDBCCompliantTimezoneShift=true&serverTimezone=UTC&useSSL=false", "root", "root"); // nu uitati sa puneti parola corecta de root pe care o aveti setata pe serverul vostru de MySql.
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
    
    public static void addUser(String username, String password, String lastName, String firstName, String gender, int age) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String userInsertionStatement = "insert into users(username, password, lastName, firstName, gender, age) values(?,?,?,?,?,?)";
                PreparedStatement updateUsers = con.prepareStatement(userInsertionStatement);
                updateUsers.setString(1, username);
                updateUsers.setString(2, password);
                updateUsers.setString(3, lastName);
                updateUsers.setString(4, firstName);
                updateUsers.setString(5, gender);
                updateUsers.setInt(6, age);
                updateUsers.executeUpdate();
                System.out.println("Inserted user '" + username + "'\n password: " + password + "\nlast name: " + lastName + "\nfirst name: " + firstName + "\ngender: " + gender + "\nage: " + age + " into the database\n\n");
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
    
    public static void addCompany(String username, String password, String name) throws SQLException, Exception
    {
        if (con != null)
        {
            try
            {
                // create a prepared SQL statement
                String userInsertionStatement = "insert into companies(username, password, name) values(?,?,?)";
                PreparedStatement updateUsers = con.prepareStatement(userInsertionStatement);
                updateUsers.setString(1, username);
                updateUsers.setString(2, password);
                updateUsers.setString(3, name);
                updateUsers.executeUpdate();
                System.out.println("Inserted company '" + username + "'\n password: " + password + "\nname:  " + name + " into the database\n\n");
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
    
    
    
    public static void dbInsertBeaconTime(int idUser, String idBeacon, long timeSeconds) throws SQLException, Exception {
        if (con != null) {
            try {
                String stmtString = "INSERT INTO userbeacontime(idUser, idBeacon, timeSeconds) VALUES(?, ?, ?)";
                PreparedStatement preparedStatement = con.prepareStatement(stmtString);
                preparedStatement.setInt(1, idUser);
                preparedStatement.setString(2, idBeacon);
                preparedStatement.setLong(3, timeSeconds);
                preparedStatement.executeUpdate();
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        } 
        else {
            error = "Exception : Database connection was lost.";
            throw new Exception(error);
        }
    }

    public static void dbUpdateBeaconTimeData(int idUser, String idBeacon, long timeSeconds) throws SQLException, Exception {
        if (con != null) {
            try {
                String stmtString = "UPDATE userbeacontime SET timeSeconds = ? WHERE idUser = ? AND idBeacon = ?;";
                PreparedStatement preparedStatement = con.prepareStatement(stmtString);
                preparedStatement.setLong(1, timeSeconds);
                preparedStatement.setInt(2, idUser);
                preparedStatement.setString(3, idBeacon);
                preparedStatement.executeUpdate();
            }
            catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        } 
        else {
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

    public static ResultSet dbGetCampaignsReactions() {
        ResultSet resultSet = null;
        try {
            if(con != null) {
                String stmtString = "SELECT campaignsreactions.idCampaign, campaignsReactions.idUser, users.gender, users.age, campaignsreactions.viewDate " +
                        "FROM campaignsreactions INNER JOIN users ON(campaignsreactions.idUser = users.idusers);";
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = stmt.executeQuery(stmtString);
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return resultSet;
    }

    public static ResultSet getCampains(String idCompany)
    {
        ResultSet resultSet = null;
        try
        {
            if(con != null)
            {
                String queryString = "SELECT * FROM campaigns WHERE idCompany = '" + idCompany + "';";
                Statement getCampainsStatement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultSet = getCampainsStatement.executeQuery(queryString);
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
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
                sqle.printStackTrace();
            }
        }
        else
        {
            error = "Exception : Database connection was lost.";
            throw new Exception(error);
        }
        return resultSet;
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
    
    public static void addCampain(String idCampain, String idCompany, String title, String category, String description, String startDate, String endDate, String photoPath) throws SQLException
    {
        String statementString = "INSERT INTO campaigns(idCampaign, idCompany, title, category, description, startDate, endDate, photoPath) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement statement = con.prepareStatement(statementString);
        statement.setString(1, idCampain);
        statement.setString(2, idCompany);
        statement.setString(3, title);
        statement.setString(4, category);
        statement.setString(5, description);
        statement.setString(6, startDate);
        statement.setString(7, endDate);
        statement.setString(8, photoPath);
        statement.executeUpdate();
    }

    public static void dbAddCampaignReaction(String idCampaign, Integer idUser, String viewDate) {
        ResultSet resultSet = null;
        try {
            if(con != null) {
                String stmtString = "INSERT INTO campaignsreactions(idCampaign, idUser, viewDate) VALUES(?,?,?);";
                PreparedStatement preparedStatement = con.prepareStatement(stmtString);
                preparedStatement.setString(1, idCampaign);
                preparedStatement.setInt(2, idUser);
                preparedStatement.setString(3, viewDate);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dbAddBeaconDistance(String idBeacon, Double distance) {
        ResultSet resultSet = null;
        try {
            if(con != null) {
                String stmtString = "INSERT INTO beaconDistances(idBeacon, distance) VALUES(?,?);";
                PreparedStatement preparedStatement = con.prepareStatement(stmtString);
                preparedStatement.setString(1, idBeacon);
                preparedStatement.setDouble(2, distance);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateCampain(String idCampain, String title, String category, String description, String startDate, String endDate) throws SQLException
    {
        if(con != null) {
            String statementString = "UPDATE campaigns SET title = ?, category = ?,  description = ?, startDate = ?, endDate = ? WHERE idCampaign = ?;";
            PreparedStatement statement = con.prepareStatement(statementString);
            statement.setString(1, title);
            statement.setString(2, category);
            statement.setString(3, description);
            statement.setString(4, startDate);
            statement.setString(5, endDate);
            statement.setString(6, idCampain);
            statement.executeUpdate();
        }
    }

    public static void removeCampaign(String idCampain) throws SQLException
    {
        if(con != null) {
            String statementString = "DELETE FROM campaigns WHERE idCampaign = ?;";
            PreparedStatement statement = con.prepareStatement(statementString);
            statement.setString(1, idCampain);
            statement.executeUpdate();
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
    public static byte[] getImageFromDisk(String path) throws Exception
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
            Logger.getLogger(ManageClientAppInteractionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imageBytes;
    }

    public static void getBeacons() throws Exception {
        String beaconImagePath = "BeaconsPictures\\";
        ResultSet beaconsResultSet = SolomonServer.getTableData("beacons");
        if(beaconsResultSet.isBeforeFirst())
        {
            while (beaconsResultSet.next()) {
                String beaconId = beaconsResultSet.getString("id");
                String label = beaconsResultSet.getString("label");
                Integer idMall = beaconsResultSet.getInt("idMall");
                String idCompany = beaconsResultSet.getString("idCompany");
                String major = beaconsResultSet.getString("major");
                String minor = beaconsResultSet.getString("minor");
                Double latitude = beaconsResultSet.getDouble("latitude");
                Double longitude = beaconsResultSet.getDouble("longitude");
                Integer layer = beaconsResultSet.getInt("layer");
                Integer floor = beaconsResultSet.getInt("floor");
                String manufacturer = beaconsResultSet.getString("manufacturer");
                switch (manufacturer) {
                    case "Estimote":
                        EstimoteBeacon estimoteBeacon = new EstimoteBeacon(beaconId, label, idMall, idCompany, new Coordinates(latitude, longitude), layer, floor);
                        try {
                            estimoteBeacon.setImage(getImageFromDisk(beaconImagePath + beaconId + ".jpg"));
                        }
                        catch(Exception ex) {}

                        SolomonServer.beaconsMap.put(beaconId, estimoteBeacon);
                        break;
                    case "Kontakt":
                        KontaktBeacon kontaktBeacon = new KontaktBeacon(beaconId, label, idMall, idCompany, major, minor, new Coordinates(latitude, longitude), layer, floor);
                        try {
                            kontaktBeacon.setImage(getImageFromDisk(beaconImagePath + beaconId + ".jpg"));
                        }
                        catch (Exception ex){}

                        SolomonServer.beaconsMap.put(beaconId, kontaktBeacon);
                        break;
                }
            }
        }
    }

    public static void getUserBeaconTime() throws Exception {
        ResultSet beaconTimeResultSet = getTableData("userbeacontime");
        if(beaconTimeResultSet.isBeforeFirst())
        {
            while (beaconTimeResultSet.next()) {
                Integer idUser = beaconTimeResultSet.getInt("idUser");
                String idBeacon = beaconTimeResultSet.getString("idBeacon");
                Long timeSeconds = beaconTimeResultSet.getLong("timeSeconds");
                if (!usersBeaconTimeMap.containsKey(idUser))
                    usersBeaconTimeMap.put(idUser, new HashMap<>());
                else
                    usersBeaconTimeMap.get(idUser).put(idBeacon, new BeaconTime(idUser, beaconsMap.get(idBeacon), timeSeconds));

                //add the time to the total beacon time
                if(!totalBeaconTime.containsKey(idBeacon))
                    totalBeaconTime.put(idBeacon, timeSeconds);
                else
                    totalBeaconTime.put(idBeacon, totalBeaconTime.get(idBeacon) + timeSeconds);
            }
        }
    }

    public static void getMalls() throws Exception {
        //MALLS
        //get malls from the database
        ResultSet mallsResultSet = SolomonServer.getTableData("malls");
        while(mallsResultSet.next())
        {
            int mallId = mallsResultSet.getInt("idMalls");
            String name = mallsResultSet.getString("name");
            String photoPath = "MallPictures\\" + mallId + ".jpg";
            double latitude = mallsResultSet.getDouble("latitude");
            double longitude = mallsResultSet.getDouble("longitude");
            Mall mall = new Mall(mallId, name, getImageFromDisk(photoPath), new Coordinates(latitude, longitude));
            SolomonServer.mallsMap.put(mallId, mall);
            System.out.println("Created mall with id: " + mall.getMallId() + " and name: " + name);
        }
    }


    public static void getCompanies() throws Exception {
        ResultSet companiesResultSet = SolomonServer.getTableData("companies");
        if(companiesResultSet.isBeforeFirst())
        {
            while(companiesResultSet.next())
            {
                String idCompany = companiesResultSet.getString("username");
                String companyName = companiesResultSet.getString("name");
                companiesMap.put(idCompany, companyName);
            }
        }

    }
    public static void getCampaigns() throws Exception {

        ResultSet campaignsResultSet = SolomonServer.getTableData("campaigns");
        if(campaignsResultSet.isBeforeFirst())
        {
            while(campaignsResultSet.next())
            {
                String idCampaign = campaignsResultSet.getString("campaigns.idCampaign");
                String idCompany = campaignsResultSet.getString("campaigns.idCompany");
                String title = campaignsResultSet.getString("campaigns.title");
                String category = campaignsResultSet.getString("campaigns.category");
                String description = campaignsResultSet.getString("campaigns.description");
                String startDate = campaignsResultSet.getString("campaigns.startDate");
                String endDate = campaignsResultSet.getString("campaigns.endDate");
                String photoPath = campaignsResultSet.getString("campaigns.photoPath");
                campaignsMapById.put(idCampaign, new Campaign(idCampaign, idCompany, companiesMap.get(idCompany), title, category, description, startDate, endDate, photoPath));
            }
        }
    }
    public static void getCampaignsReactions() throws Exception {
        ResultSet resultSet = SolomonServer.dbGetCampaignsReactions();
        if(resultSet.isBeforeFirst()) {
            while(resultSet.next()) {
                String idCampaign = resultSet.getString("campaignsreactions.idCampaign");
                Integer idUser = resultSet.getInt("campaignsreactions.idUser");
                String gender = resultSet.getString("users.gender");
                Integer age = resultSet.getInt("users.age");
                String viewDate = resultSet.getString("campaignsreactions.viewDate");
                CampaignReaction campaignReaction = new CampaignReaction(idCampaign, idUser, gender, age, viewDate);
                if (campaignReactionsMap.containsKey(idCampaign)) {
                    campaignReactionsMap.get(idCampaign).add(campaignReaction);
                } else {
                    ArrayList<CampaignReaction> campaignReactions = new ArrayList<>();
                    campaignReactions.add(campaignReaction);
                    campaignReactionsMap.put(idCampaign, campaignReactions);
                }
            }
        }
    }
}