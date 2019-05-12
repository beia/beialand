using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;
using UnityEngine.SceneManagement;
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
    private Thread clientReceiveThread;
    public NetworkStream stream;
    public static volatile bool loadHeatMap; 
    #endregion
    // Use this for initialization 	
    void Start()
    {
        loadHeatMap = false;
        ConnectToTcpServer();
        //very dumb aproach to the situation - I will rewrite the code for the UI updating
        StartCoroutine(updateUI());
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
            StaticData.socketConnection = new TcpClient("localhost", 7000);
            stream = StaticData.socketConnection.GetStream();
            //clientReceiveThread = new Thread(new ThreadStart(ListenForData));
            //clientReceiveThread.IsBackground = true;
            //clientReceiveThread.Start();
        }
        catch (Exception e)
        {
            Debug.Log("On client connect exception " + e);
        }
    }
 	
    private void SendMessage(String message)
    {
        if (StaticData.socketConnection == null)
        {
            return;
        }
        try
        {
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
        using (stream)
        {
            int length;
            // Read incomming stream into byte arrary. 					
            if((length = stream.Read(bytes, 0, bytes.Length)) != 0)
            {
                var incommingData = new byte[length];
                Array.Copy(bytes, 0, incommingData, 0, length);
                // Convert byte array to string message. 						
                string serverMessage = Encoding.ASCII.GetString(incommingData);
                //get the server message
                UserDataUnityPacket userData = JsonUtility.FromJson<UserDataUnityPacket>(serverMessage);
                Debug.Log("server message received as: " + serverMessage);
                if (userData.error != "user not found" && userData.error != "user never entered the store")
                {
                    StaticData.userHeatMapLastName = userData.lastName;
                    StaticData.userHeatMapFirstName = userData.firstName;
                    StaticData.userHeatMapAge = userData.age;
                    StaticData.userHeatMapRoom1Time = userData.room1Time;
                    StaticData.userHeatMapRoom2Time = userData.room2Time;
                    StaticData.userHeatMapRoom3Time = userData.room3Time;
                    StaticData.userHeatMapRoom4Time = userData.room4Time;
                    stream.Close();
                    loadHeatMap = true;
                }
            }
        }
    }

    public void onButtonClicked()
    {
        Thread getUserDataThread = new Thread(new ThreadStart(getUserData));
        getUserDataThread.IsBackground = true;
        getUserDataThread.Start();
    }

    IEnumerator updateUI()
    {
        while (true)
        {

            //refresh UI every second
            if (Client.loadHeatMap == true)
            {
                SceneManager.LoadScene(1);
                yield return 0;
            }
            yield return new WaitForSeconds(1);
        }
    }
}