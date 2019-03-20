package com.example.solomon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectToJavaServerRunnable implements Runnable
{
    @Override
    public void run() {
        try
        {
            LoginActivity.socket = new Socket("172.20.10.13", 8000);
            LoginActivity.objectOutputStream = new ObjectOutputStream(LoginActivity.socket.getOutputStream());
            LoginActivity.objectInputStream = new ObjectInputStream(LoginActivity.socket.getInputStream());
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
