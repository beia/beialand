/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SolomonPartnersNetworkObjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author beia
 */
public class User implements Serializable
{
    private int idUser;
    private String username;
    private String lastName;
    private String firstName;
    private int age;
    private ArrayList<String> preferences;
    private ArrayList<UserStoreTime> storesTime;
    public User(int idUser, String username, String lastName, String firstName, int age, ArrayList<String> preferences, ArrayList<UserStoreTime> storesTime)
    {
        this.idUser = idUser;
        this.username = username;
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.preferences = preferences;
        this.storesTime = storesTime;
    }
    public int getId()
    {
        return this.idUser;
    }
    public String getUsername()
    {
        return this.username;
    }
    public String getLastName()
    {
        return this.lastName;
    }
    public String getFirstName()
    {
        return this.firstName;
    }
    public int getAge()
    {
        return this.age;
    }
    public ArrayList<String> getPreferences()
    {
        return this.preferences;
    }
    public ArrayList<UserStoreTime> getStoresTime()
    {
        return this.storesTime;
    }
    @Override
    public String toString()
    {
        String userInfo = "userId: " + this.idUser + "\nusername: " + this.username + "\nlast name: " + this.lastName + "\nfirst name: " + this.firstName + "\nage: " + this.age + "\n";
        userInfo += "preferinte: \n";
        for(String preference : this.preferences)
            userInfo += preference + "\n";
        return userInfo;
    }
}
