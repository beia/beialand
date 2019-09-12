package com.beia.solomon.runnables;

import com.beia.solomon.networkPackets.UserPreferences;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SendPreferencesRunnable implements Runnable
{
    private UserPreferences userPreferences;
    private ObjectOutputStream objectOutputStream;
    public SendPreferencesRunnable(UserPreferences userPreferences, ObjectOutputStream objectOutputStream)
    {
        this.userPreferences = userPreferences;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run()
    {
        try
        {
            objectOutputStream.writeObject(userPreferences);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
