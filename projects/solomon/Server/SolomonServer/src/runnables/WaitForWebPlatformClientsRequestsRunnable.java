/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runnables;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class WaitForWebPlatformClientsRequestsRunnable implements Runnable {
    
    private ServerSocket serverSocket;
    private final int TOKEN_DIMENSION = 14;
    public WaitForWebPlatformClientsRequestsRunnable(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }
    @Override
    public void run() {
            while(true)
            {
                try(Socket socket = serverSocket.accept()){
                //REQUEST
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                System.out.println("Waiting for web plaform clients http requests...");
                StringBuilder content;
                String body = ""; 
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                    byte[] byteArray = buffer.toByteArray();
                    body = new String(byteArray, StandardCharsets.UTF_8);
                    Scanner scan = new Scanner(body);
                    int contentLength = 0;
                    while(scan.hasNextLine())
                    {
                        String line = scan.nextLine();
                        String[] header = line.split(":");
                        if(header[0].trim().equals("Content-Length"))
                            contentLength = Integer.parseInt(header[1].trim());
                        System.out.println(line);
                        if(line.equals("\n"))
                        {
                            body = scan.nextLine();
                            break;
                        }
                    }
                }
                System.out.println(body);
                buffer.flush();
                
                //PARSE JSON
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(body);
                String requestType = (String)jsonObject.get("requestType");
                switch(requestType)
                {
                    case "login":
                        String username = (String)jsonObject.get("username");
                        String password = (String)jsonObject.get("password");
                        ResultSet resultSet = SolomonServer.getUserDataFromDatabase("users", username);
                        if(!resultSet.isBeforeFirst())//the user isn't in the database
                        {
                            System.out.println("DEBUG1");
                            //write the response
                            PrintWriter out = new PrintWriter(outputStream);
                            out.print("HTTP/1.1 200 \r\n"); // Version & status code
                            out.print("Content-Type: application/json\r\n"); // The type of data
                            out.print("Connection: close\r\n"); // Will close stream
                            out.print("\r\n"); // End of headers
                            //write the body of the response
                            String jsonResponseBody = "{\"authToken\":null}";
                            out.print(jsonResponseBody);
                            out.close();
                        }
                        else
                        {
                            System.out.println("DEBUG2");
                            //write the response
                            PrintWriter out = new PrintWriter(outputStream);
                            out.print("HTTP/1.1 200 \r\n"); // Version & status code
                            out.print("Content-Type: application/json\r\n"); // The type of data
                            out.print("Connection: close\r\n"); // Will close stream
                            out.print("\r\n"); // End of headers
                            //create a authentication token
                            String token = getAlphaNumericString(TOKEN_DIMENSION);
                            while(SolomonServer.webClientsTokensMap.containsKey(token))
                                token = getAlphaNumericString(TOKEN_DIMENSION);
                            resultSet.next();
                            SolomonServer.webClientsTokensMap.put(token, resultSet.getInt("idusers"));
                            //write the body of the response
                            String jsonResponseBody = "{\"authToken\":" + token + "}";
                            out.print(jsonResponseBody);
                            out.close();
                        }
                        break;
                    case "logout":
                        break;
                    case "register":
                        break;
                    default:
                        System.out.println("FORMAT NOT CORRECT");
                        break;
                }
                socket.close();
           }
                catch (IOException ex) {
                    ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
            
        }       catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        } 
    }
    public String getAlphaNumericString(int n) 
    { 
  
        // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz"; 
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
  
        return sb.toString(); 
    }
}
