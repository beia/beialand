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
            //82.78.81.162:480000 (virtual machine adress)
            LoginActivity.socket = new Socket("192.168.43.194", 8000);
            LoginActivity.objectOutputStream = new ObjectOutputStream(LoginActivity.socket.getOutputStream());
            LoginActivity.objectInputStream = new ObjectInputStream(LoginActivity.socket.getInputStream());
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
}