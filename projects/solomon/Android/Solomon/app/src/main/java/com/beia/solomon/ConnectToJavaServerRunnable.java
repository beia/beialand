package com.beia.solomon;

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
            //solomon-beacon.beia-consult.ro(virtual machine adress)
<<<<<<< HEAD
            LoginActivity.socket = new Socket("solomon-beacon.beia-consult.ro", 48000);
=======
<<<<<<< HEAD
            LoginActivity.socket = new Socket("solomon-beacon.beia-consult.ro", 48000);
=======
            LoginActivity.socket = new Socket("solomon-beacon.beia-consult.ro", 8000);
>>>>>>> 6d03e9e75cfae83fe05848e02296f1abe79d2a6d
>>>>>>> 3c49816a2823ad4caa02cfbc8a48ef09ae8cdb20
            LoginActivity.objectOutputStream = new ObjectOutputStream(LoginActivity.socket.getOutputStream());
            LoginActivity.objectInputStream = new ObjectInputStream(LoginActivity.socket.getInputStream());
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
}