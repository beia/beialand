package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class UserData implements Serializable
{
    private int userId;
    private String username;
    private String lastName;
    private String firstName;
    private int age;
    public UserData(int userId, String username, String lastName, String firstName, int age)
    {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public int getUserId() {
        return userId;
    }
    public String getUsername() { return this.username; }
    public String getLastName() { return this.lastName; }
    public String getFirstName() { return this.firstName; }
    public int getAge() { return this.age; }
}
