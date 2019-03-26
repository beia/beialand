/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import runnables.ConnectClientsRunnable;

/**
 *
 * @author beia
 */
public class SolomonServer {
    
    //solomon server variables 
    public static ServerSocket serverSocket;
    public static Thread connectClients;
    //sql server variables
    public static String error;
    public static Connection con;
    
    
    public static void main(String[] args) throws IOException, SQLException, Exception
    {
        //connect to a mySql database
        connectToDatabase();
        
        //create a tcp serverSocket and wait for client connections
        serverSocket = new ServerSocket(8000);
        connectClients = new Thread(new ConnectClientsRunnable(serverSocket));
        connectClients.start();
    }
    
    public static void connectToDatabase() throws ClassNotFoundException, SQLException, Exception 
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/solomondatabase?autoReconnect=true&useSSL=false", "root", "Puihoward_1423"); // nu uitati sa puneti parola corecta de root pe care o aveti setata pe serverul vostru de MySql.
            System.out.println("Successfully connected to the database!");
        }
        catch (ClassNotFoundException cnfe)
        {
            error = "ClassNotFoundException: Can't find the driver for the database.";
            throw new ClassNotFoundException(error);
        }
        catch (SQLException cnfe)
        {
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

}