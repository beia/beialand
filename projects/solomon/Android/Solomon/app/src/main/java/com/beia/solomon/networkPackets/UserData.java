package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class UserData implements Serializable
{
    private int userId;
    private String username;
    private String lastName;
    private String firstName;
    private int age;
    private boolean isFirstLogin;
    public UserData(int userId, String username, String lastName, String firstName, int age, boolean isFirstLogin)
    {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.isFirstLogin = isFirstLogin;
    }

    public int getUserId() {
        return userId;
    }
    public String getUsername() { return this.username; }
    public String getLastName() { return this.lastName; }
    public String getFirstName() { return this.firstName; }
    public int getAge() { return this.age; }
    public boolean isFirstLogin() { return this.isFirstLogin; }
}
