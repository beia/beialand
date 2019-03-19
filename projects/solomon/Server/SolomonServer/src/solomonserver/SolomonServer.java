/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solomonserver;

import java.io.IOException;
import java.net.ServerSocket;
import runnables.ConnectClientsRunnable;

/**
 *
 * @author beia
 */
public class SolomonServer {

    
    /**
     * @param args the command line arguments
     */
    public static ServerSocket serverSocket;
    public static Thread connectClients;
    
    public static void main(String[] args) throws IOException
    {
        serverSocket = new ServerSocket(8000);
        
        connectClients = new Thread(new ConnectClientsRunnable(serverSocket));
        connectClients.start();
    }
    
}
