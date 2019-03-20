/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author beia
 */
public class ConnectClientsRunnable  implements Runnable
{
    public ServerSocket serverSocket;
    public ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;
    public ConnectClientsRunnable(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }
    @Override
    public void run() {
        
        while(true)
        {
            System.out.println("Waiting for connection");
            try 
            {
                Socket socket = serverSocket.accept();
                System.out.println("Connected!");
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                Thread manageClientConnection = new Thread(new ManageClientAuthenticationRunnable(objectOutputStream, objectInputStream));
                manageClientConnection.start();
            }   
            catch (IOException ex)
            {
                Logger.getLogger(ConnectClientsRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}