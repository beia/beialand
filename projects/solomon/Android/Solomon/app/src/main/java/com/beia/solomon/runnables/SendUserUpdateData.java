package com.beia.solomon.runnables;

import com.beia.solomon.networkPackets.UpdateUserData;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SendUserUpdateData implements Runnable
{
    private ObjectOutputStream objectOutputStream;
    private UpdateUserData updateUserData;
    public SendUserUpdateData(ObjectOutputStream objectOutputStream, UpdateUserData updateUserData)
    {
        this.objectOutputStream = objectOutputStream;
        this.updateUserData = updateUserData;
    }
    @Override
    public void run() {
        try
        {
            //send the user update data
            synchronized (objectOutputStream) {
                objectOutputStream.writeObject(updateUserData);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
