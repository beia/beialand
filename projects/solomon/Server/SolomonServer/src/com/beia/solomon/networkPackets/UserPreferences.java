package com.beia.solomon.networkPackets;

import java.io.Serializable;
import java.util.ArrayList;

public class UserPreferences implements Serializable
{
    private int userId;
    private ArrayList<String> preferences;
    public UserPreferences(int userId, ArrayList<String> preferences)
    {
        this.userId = userId;
        this.preferences = preferences;
    }
    public int getUserId()
    {
        return this.userId;
    }
    public ArrayList<String> getPreferences()
    {
        return this.preferences;
    }
}
