package com.example.solomon.networkPackets;

import java.io.Serializable;

public class UpdateUserData implements Serializable
{
    private int id;
    private String username;
    private String password;
    private int age;
    public UpdateUserData(int id, String username, String password, int age)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
    }
    public int getId() { return this.id; }
    public String getUsername()
    {
        return this.username;
    }
    public String getPassword()
    {
        return this.password;
    }
    public int getAge()
    {
        return this.age;
    }
}
