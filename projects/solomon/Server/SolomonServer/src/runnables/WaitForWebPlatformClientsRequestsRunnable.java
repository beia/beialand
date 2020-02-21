/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author beia
 */
public class WaitForWebPlatformClientsRequestsRunnable implements Runnable {
    
    private ServerSocket serverSocket;
    public WaitForWebPlatformClientsRequestsRunnable(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }
    @Override
    public void run() {
        try
        {
            while(true)
            {
                System.out.println("Waiting for web plaform clients http requests...");
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //read the request
                String line;
                String body = "";
                while ((line = in.readLine()) != null)
                {
                    if (line.length() == 0)
                        break;
                    System.out.println(line);
                }
                byte[] bytes = new byte[1000];
                socket.getInputStream().read(bytes);
                System.out.print(new String(bytes, StandardCharsets.UTF_8));
                
                //write the response
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.print("HTTP/1.1 200 \r\n"); // Version & status code
                out.print("Content-Type: application/json\r\n"); // The type of data
                out.print("Connection: close\r\n"); // Will close stream
                out.print("\r\n"); // End of headers
                //write the body of the response
               out.close();
               in.close();
               socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(WaitForWebPlatformClientsRequestsRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
