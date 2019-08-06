/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SolomonPartnersNetworkObjects;

import java.io.Serializable;

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
    public User(int idUser, String username, String lastName, String firstName, int age)
    {
        this.idUser = idUser;
        this.username = username;
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
    }
    private int getId()
    {
        return this.idUser;
    }
    private String getUsername()
    {
        return this.username;
    }
    private String getLastName()
    {
        return this.lastName;
    }
    private String getFirstName()
    {
        return this.firstName;
    }
    private int getAge()
    {
        return this.age;
    }
}
