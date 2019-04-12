using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;
using UnityEngine.UI;

[Serializable]
public class UserDataUnityPacket
{
    public String lastName;
    public String firstName;
    public int age;
    public String room1Time;
    public String room2Time;
    public String room3Time;
    public String room4Time;
    public String error;
    public UserDataUnityPacket(String lastName, String firstName, int age, String room1Time, String room2Time, String room3Time, String room4Time)
    {
        this.lastName = lastName;
        this.firstName = firstName;
        this.age = age;
        this.room1Time = room1Time;
        this.room2Time = room2Time;
        this.room3Time = room3Time;
        this.room4Time = room4Time;
    }
    public UserDataUnityPacket(String error)
    {
        this.error = error;
    }
}

[Serializable]
public class UserUnityCommand
{
    public String command;
    public String username;
    public UserUnityCommand(String command, String username)
    {
        this.command = command;
        this.username = username;
    }
}

public class Client : MonoBehaviour
{
    public InputField usernameInputField;
    #region private members 	
    private TcpClient socketConnection;
    private Thread clientReceiveThread;
    #endregion
    // Use this for initialization 	
    void Start()
    {
        ConnectToTcpServer();
    }
    // Update is called once per frame
    void Update()
    {
    }
    /// <summary> 	
    /// Setup socket connection. 	
    /// </summary> 	
    private void ConnectToTcpServer()
    {
        try
        {
            socketConnection = new TcpClient("localhost", 7000);
            //clientReceiveThread = new Thread(new ThreadStart(ListenForData));
            //clientReceiveThread.IsBackground = true;
            //clientReceiveThread.Start();
        }
        catch (Exception e)
        {
            Debug.Log("On client connect exception " + e);
        }
    }
    /// <summary> 	
    /// Runs in background clientReceiveThread; Listens for incomming data. 	
    /// </summary>     
    private void ListenForData()
    {
        try
        {
            socketConnection = new TcpClient("localhost", 7000);
            Byte[] bytes = new Byte[1024];
            while (true)
            {
                // Get a stream object for reading 				
                using (NetworkStream stream = socketConnection.GetStream())
                {
                    int length;
                    // Read incomming stream into byte arrary. 					
                    while ((length = stream.Read(bytes, 0, bytes.Length)) != 0)
                    {
                        var incommingData = new byte[length];
                        Array.Copy(bytes, 0, incommingData, 0, length);
                        // Convert byte array to string message. 						
                        string serverMessage = Encoding.ASCII.GetString(incommingData);
                        //get the server message
                        Debug.Log("server message received as: " + serverMessage);
                    }
                }
            }
        }
        catch (SocketException socketException)
        {
            Debug.Log("Socket exception: " + socketException);
        }
    }
    /// <summary> 	
    /// Send message to server using socket connection. 	
    /// </summary> 	
    private void SendMessage(String message)
    {
        if (socketConnection == null)
        {
            return;
        }
        try
        {
            // Get a stream object for writing. 			
            NetworkStream stream = socketConnection.GetStream();
            if (stream.CanWrite)
            {
                // Convert string message to byte array.                 
                byte[] clientMessageAsByteArray = Encoding.ASCII.GetBytes(message);
                // Write byte array to socketConnection stream.                 
                stream.Write(clientMessageAsByteArray, 0, clientMessageAsByteArray.Length);
                Debug.Log("Client sent his message - should be received by server");
            }
        }
        catch (SocketException socketException)
        {
            Debug.Log("Socket exception: " + socketException);
        }
    }

    public void getUserData()
    {
        //get data from the inputField
        String username = usernameInputField.text;
        //usernameInputField.text = "";

        //send a command to the server
        string jsonString = JsonUtility.ToJson(new UserUnityCommand("get heatmap", username));
        SendMessage(jsonString);


        //get server respnse	
        Byte[] bytes = new Byte[1024];			
        using (NetworkStream stream = socketConnection.GetStream())
        {
            int length;
            // Read incomming stream into byte arrary. 					
            while ((length = stream.Read(bytes, 0, bytes.Length)) != 0)
            {
                var incommingData = new byte[length];
                Array.Copy(bytes, 0, incommingData, 0, length);
                // Convert byte array to string message. 						
                string serverMessage = Encoding.ASCII.GetString(incommingData);
                //get the server message
                Debug.Log("server message received as: " + serverMessage);
            }
        }
    }

    public void onButtonClicked()
    {
        Thread getUserDataThread = new Thread(new ThreadStart(getUserData));
        getUserDataThread.IsBackground = true;
        getUserDataThread.Start();
    }
}