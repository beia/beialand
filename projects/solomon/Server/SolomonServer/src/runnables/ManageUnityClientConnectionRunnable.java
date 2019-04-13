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
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import solomonserver.SolomonServer;

/**
 *
 * @author beia
 */
public class ManageUnityClientConnectionRunnable implements Runnable
{
    private OutputStream outputStream;
    private InputStream inputStream;
    private byte[] bytes;
    public ManageUnityClientConnectionRunnable(OutputStream outputStream, InputStream inputStream)
    {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        bytes = new byte[1024];
    }
    @Override
    public void run() {
        try
        {
            while(true)
            {
                //wait for server unity command
                inputStream.read(bytes);
                String jsonString = new String(bytes);
                jsonString = jsonString.trim();
                System.out.println(jsonString);
                JSONObject jsonObject = (JSONObject)new JSONParser().parse(jsonString);
                String command = (String) jsonObject.get("command");
                switch(command)
                {
                    case "get heatmap":
                        String username = (String) jsonObject.get("username");
                        
                        //get room time data and user data from database
                        ResultSet resultSet = SolomonServer.getUserDataFromDatabase("users", username);
                        if (!resultSet.isBeforeFirst() ) 
                        {
                            //the user isn't into the database send the employee a message that the username isn't into the database
                            jsonObject = new JSONObject();
                            jsonObject.put("error", "user not found");
                            outputStream.write(jsonObject.toJSONString().getBytes());
                        }
                        else
                        {
                            //the user is in the database
                            resultSet.next();
                            int userId = resultSet.getInt("idusers");
                            String lastName = resultSet.getString("lastName");
                            String firstName = resultSet.getString("firstName");
                            int age = resultSet.getInt("age");
                            System.out.println(userId + " " + lastName + " " + firstName);
                            resultSet = SolomonServer.getRoomTimeDataFromDatabase("userroomtime", userId, 1);
                            if(!resultSet.isBeforeFirst())
                            {
                                //the user never entered the store send the employee a message the user never entered the store
                                jsonObject = new JSONObject();
                                jsonObject.put("error", "user never entered the store");
                                outputStream.write(jsonObject.toJSONString().getBytes());
                            }
                            else
                            {
                                //the user entered the store at least once and now we extract the room data from the database and send all the data to the employee
                                resultSet.next();
                                String room1Time = resultSet.getString("Room1Time");
                                String room2Time = resultSet.getString("Room2Time");
                                String room3Time = resultSet.getString("Room3Time");
                                String room4Time = resultSet.getString("Room4Time");
                                
                                //put all the data into a JSON object and send it to the employee
                                jsonObject = new JSONObject();
                                jsonObject.put("lastName", lastName);
                                jsonObject.put("firstName", firstName);
                                jsonObject.put("age", age);
                                jsonObject.put("room1Time", room1Time);
                                jsonObject.put("room2Time", room2Time);
                                jsonObject.put("room2Time", room2Time);
                                jsonObject.put("room3Time", room3Time);
                                jsonObject.put("room4Time", room4Time);
                                jsonObject.put("error", "Null");
                                
                                outputStream.write(jsonObject.toJSONString().getBytes());
                                //bytes = new byte[1024];
                            }
                        }
                        break;
                    default:
                        break;
                }
                if(command.equals("get heatmap"))
                    break;
            }
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ManageUnityClientConnectionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ManageUnityClientConnectionRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
