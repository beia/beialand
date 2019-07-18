package com.beia.solomon.runnables;

import com.beia.solomon.networkPackets.ImageData;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SendImageRunable implements Runnable
{
    private ImageData imageData;
    private ObjectOutputStream objectOutputStream;
    public SendImageRunable(ImageData imageData, ObjectOutputStream objectOutputStream)
    {
        this.imageData = imageData;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run() {
        try
        {
            objectOutputStream.writeObject(imageData);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}