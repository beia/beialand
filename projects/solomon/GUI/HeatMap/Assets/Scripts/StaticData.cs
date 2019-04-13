using System;
using System.Collections;
using System.Collections.Generic;
using System.Net.Sockets;
using UnityEngine;

public class StaticData : MonoBehaviour
{

    //network data
    public static TcpClient socketConnection;
    public static NetworkStream stream;

    //heat map data
    public static String userHeatMapLastName;
    public static String userHeatMapFirstName;
    public static int userHeatMapAge;
    public static String userHeatMapRoom1Time;
    public static String userHeatMapRoom2Time;
    public static String userHeatMapRoom3Time;
    public static String userHeatMapRoom4Time;

    
}
