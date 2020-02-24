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
import java.sql.ResultSet;
import java.sql.SQLException;
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
                System.out.println("Waiting for web plaform clients http requests...");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //HEADERS
                String line;
                int contentLength = 0;
                String body = "";
                while ((line = in.readLine()) != null)
                {
                    if (line.length() == 0)
                        break;
                    System.out.println(line);
                    if(line.split(":")[0].trim().equals("Content-Length"))
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
                System.out.println(contentLength);
                //BODY
                byte[] bytes = new byte[contentLength];
                socket.getInputStream().read(bytes);
                body = new String(bytes, StandardCharsets.UTF_8).trim();
                System.out.println(body);
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
                            PrintWriter out = new PrintWriter(socket.getOutputStream());
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
                            PrintWriter out = new PrintWriter(socket.getOutputStream());
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
                in.close();
                socket.close();
           }
                catch (IOException ex) {
            Logger.getLogger(WaitForWebPlatformClientsRequestsRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(WaitForWebPlatformClientsRequestsRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }       catch (SQLException ex) {
                    Logger.getLogger(WaitForWebPlatformClientsRequestsRunnable.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(WaitForWebPlatformClientsRequestsRunnable.class.getName()).log(Level.SEVERE, null, ex);
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
