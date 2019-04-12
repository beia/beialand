/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author beia
 */
public class ConnectToUnityDemoRunnable implements Runnable
{
    private ServerSocket serverSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    public ConnectToUnityDemoRunnable(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }
    @Override
    public void run()
    {
        while(true)
        {
            System.out.println("Waiting for unity demo connection...");
            try 
            {
                Socket socket = serverSocket.accept();
                System.out.println("Connected to unity demo");
                
                outputStream = new DataOutputStream(socket.getOutputStream());
                inputStream = new DataInputStream(socket.getInputStream());
                
                String message = new String("Salut de la server");
                byte[] bytes = message.getBytes();
                outputStream.write(bytes);
                
                /*
                    manage unity demo connection
                    -------------------------------
                    -------------------------------
                */
            }
            catch (IOException ex)
            {
                Logger.getLogger(ConnectToUnityDemoRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
}