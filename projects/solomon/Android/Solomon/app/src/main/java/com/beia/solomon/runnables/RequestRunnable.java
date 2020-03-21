package com.beia.solomon.runnables;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class RequestRunnable implements Runnable
{
    private String request;
    private ObjectOutputStream objectOutputStream;
    public RequestRunnable(String request, ObjectOutputStream objectOutputStream)
    {
        this.request = request;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run() {
        try
        {
            this.objectOutputStream.writeObject(request);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
