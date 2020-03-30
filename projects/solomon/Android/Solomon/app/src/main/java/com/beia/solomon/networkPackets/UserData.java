package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class UserData implements Serializable
{
    private int userId;
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    private int age;
    private boolean isFirstLogin;
    public UserData(int userId, String username, String password, String lastName, String firstName, int age, boolean isFirstLogin)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.isFirstLogin = isFirstLogin;
    }

    public UserData(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
    public String getLastName() { return this.lastName; }
    public String getFirstName() { return this.firstName; }
    public int getAge() { return this.age; }
    public boolean isFirstLogin() { return this.isFirstLogin; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setAge(int age) { this.age = age; }
}
