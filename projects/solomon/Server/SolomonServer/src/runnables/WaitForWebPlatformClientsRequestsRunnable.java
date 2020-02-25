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
            Socket socket = null;
            while(true)
            {
                try
                {
                socket = serverSocket.accept();
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
                    boolean dataReceived = false;
                    while(scan.hasNextLine())
                    {
                        String line = scan.nextLine();
                        if(line.length() > 0 && line.charAt(line.length() - 1) == '}')
                        {
                            System.out.println(line);
                            body = line;
                            dataReceived = true;
                            break;
                        }
                    }
                    if(dataReceived == true)
                        break;
                }
                //System.out.println(body);
                buffer.flush();
                
                //PARSE JSON
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(body.trim());
                String requestType = (String)jsonObject.get("requestType");
                switch(requestType)
                {
                    case "login":
                        String usernameLogin = (String)jsonObject.get("username");
                        String passwordLogin = (String)jsonObject.get("password");
                        ResultSet resultSetLogin = SolomonServer.getUserDataFromDatabase("companies", usernameLogin);
                        if(!resultSetLogin.isBeforeFirst())//the user isn't in the database
                        {
                            System.out.println("LOGIN FAILED");
                            //write the response
                            PrintWriter out = new PrintWriter(outputStream);
                            out.print("HTTP/1.1 200 \r\n"); // Version & status code
                            out.print("Content-Type: application/json\r\n"); // The type of data
                            out.print("Connection: close\r\n"); // Will close stream
                            out.print("\r\n"); // End of headers
                            //write the body of the response
                            String jsonResponseLogin = "{\"authToken\":null}";
                            out.print(jsonResponseLogin);
                            out.close();
                        }
                        else
                        {
                            resultSetLogin.next();
                            String usernameDatabase = resultSetLogin.getString("username");
                            String passwordDatabase = resultSetLogin.getString("password");
                            String nameDatabase = resultSetLogin.getString("name");
                            if(passwordLogin.equals(passwordDatabase))
                            {
                                System.out.println("LOGIN SUCCESSFULL");
                                //create a authentication token
                                String token = getAlphaNumericString(TOKEN_DIMENSION);
                                while(SolomonServer.webClientsTokensMap.containsKey(token))
                                    token = getAlphaNumericString(TOKEN_DIMENSION);
                                SolomonServer.webClientsTokensMap.put(token, usernameLogin);
                                String jsonResponseLogin = "{\"authToken\":\"" + token + "\"}";
                                writeResponse(jsonResponseLogin, outputStream);
                            }
                            else
                            {
                                System.out.println("LOGIN FAILED");
                                String jsonResponseLogin = "{\"authToken\":null}";
                                writeResponse(jsonResponseLogin, outputStream);
                            }
                        }
                        break;
                    case "logout":
                        String token = (String)jsonObject.get("authToken");
                        if(SolomonServer.webClientsTokensMap.containsKey(token))//user logged in
                        {
                            String username = SolomonServer.webClientsTokensMap.get(token);
                            System.out.println("User with username: " + username + " logged out!");
                            SolomonServer.webClientsTokensMap.remove(token);
                            String jsonResponseLogout = "{\"logout\":true}";
                            writeResponse(jsonResponseLogout, outputStream);
                        }
                        else//user is not logged in or token expired
                        {
                            System.out.println("User with token: " + token + " has token expired or logged out already!");
                            String jsonResponseLogout = "{\"logout\":false}";
                            writeResponse(jsonResponseLogout, outputStream);
                        }
                        break;
                    case "register":
                        String usernameRegister = (String)jsonObject.get("username");
                        String passwordRegister = (String)jsonObject.get("password");
                        String nameRegister = (String)jsonObject.get("name");
                        ResultSet resultSetRegister = SolomonServer.getUserDataFromDatabase("companies", usernameRegister);
                        if(!resultSetRegister.isBeforeFirst())//the user isn't in the database
                        {
                            System.out.println("REGISTER SUCCESFULL");
                            SolomonServer.addCompany(usernameRegister, passwordRegister, nameRegister);
                            String jsonResponseRegister = "{\"success\":true}";
                            writeResponse(jsonResponseRegister, outputStream);
                        }
                        else
                        {
                            System.out.println("REGISTER FAILED");
                            String jsonResponseRegister = "{\"success\":false}";
                            writeResponse(jsonResponseRegister, outputStream);
                        }
                        break;
                    default:
                        System.out.println("FORMAT NOT CORRECT");
                        break;
                }
                System.out.println("SOCKET CLOSED");
           }
                catch (IOException ex) {
                    ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
            
        }   catch (SQLException ex) {
                ex.printStackTrace();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
    }
    public void writeResponse(String response, OutputStream outputStream)
    {
        //write the response
        PrintWriter out = new PrintWriter(outputStream);
        out.print("HTTP/1.1 200 \r\n"); // Version & status code
        out.print("Content-Type: application/json\r\n"); // The type of data
        out.print("Connection: close\r\n"); // Will close stream
        out.print("\r\n"); // End of headers
        //write the body of the response
        out.print(response);
        out.close();
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
