package com.example.solomon.networkPackets;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UserData implements Serializable
{
    private int userId;
    public UserData(int userId)
    {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
