/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.solomon.networkPackets;

import java.io.Serializable;

/**
 *
 * @author beia
 */
public class UserDataUnityPacket implements Serializable
{
    public String lastName;
    public String firstName;
    public int age;
    public String room1Time;
    public String room2Time;
    public String room3Time;
    public String room4Time;
    public String error;
    public UserDataUnityPacket(String lastName, String firstName, int age, String room1Time, String room2Time, String room3Time, String room4Time)
    {
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.room1Time = room1Time;
        this.room2Time = room2Time;
        this.room3Time = room3Time;
        this.room4Time = room4Time;
    }
    public UserDataUnityPacket(String error)
    {
        this.error = error;
    }
}
