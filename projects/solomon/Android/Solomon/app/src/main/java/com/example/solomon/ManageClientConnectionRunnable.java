package com.example.solomon;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ManageClientConnectionRunnable implements Runnable
{
    public ObjectOutputStream objectOutputStream;
    public ObjectInputStream objectInputStream;
    public ManageClientConnectionRunnable(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream)
    {
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }
    @Override
    public void run() {

    }
}
