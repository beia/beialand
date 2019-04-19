using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;
using System;

public class HeatMapManagingScript : MonoBehaviour
{
    public Text lastNameText;
    public Text firstNameText;
    public Text ageText;
    public Text room1TimeText;
    public Text room2TimeText;
    public Text room3TimeText;
    public Text room4TimeText;
    public Text room1TimePercentageText;
    public Text room2TimePercentageText;
    public Text room3TimePercentageText;
    public Text room4TimePercentageText;
    // Start is called before the first frame update
    void Start()
    {
        //initialize the UI with the user data
        lastNameText.text = "Last name: " + StaticData.userHeatMapLastName;
        firstNameText.text = "First name: " + StaticData.userHeatMapFirstName;
        ageText.text = "Age: " + StaticData.userHeatMapAge.ToString();
        room1TimeText.text = "Room1: " + StaticData.userHeatMapRoom1Time;
        room2TimeText.text = "Room2: " + StaticData.userHeatMapRoom2Time;
        room3TimeText.text = "Room3: " + StaticData.userHeatMapRoom3Time;
        room4TimeText.text = "Room4: " + StaticData.userHeatMapRoom4Time;

        //extract the number of seconds from each room time string - time string format : "x hours y minutes z seconds"
        string[] timeData = StaticData.userHeatMapRoom1Time.Split(' ');
        int hours = Int32.Parse(timeData[0]);
        int minutes = Int32.Parse(timeData[2]);
        int seconds = Int32.Parse(timeData[4]);
        int totalSecondsRoom1 = hours * 3600 + minutes * 60 + seconds;

        timeData = StaticData.userHeatMapRoom2Time.Split(' ');
        hours = Int32.Parse(timeData[0]);
        minutes = Int32.Parse(timeData[2]);
        seconds = Int32.Parse(timeData[4]);
        int totalSecondsRoom2 = hours * 3600 + minutes * 60 + seconds;

        timeData = StaticData.userHeatMapRoom3Time.Split(' ');
        hours = Int32.Parse(timeData[0]);
        minutes = Int32.Parse(timeData[2]);
        seconds = Int32.Parse(timeData[4]);
        int totalSecondsRoom3 = hours * 3600 + minutes * 60 + seconds;

        timeData = StaticData.userHeatMapRoom4Time.Split(' ');
        hours = Int32.Parse(timeData[0]);
        minutes = Int32.Parse(timeData[2]);
        seconds = Int32.Parse(timeData[4]);
        int totalSecondsRoom4 = hours * 3600 + minutes * 60 + seconds;

        //calculate the room time percentage for the user
        int roomSecondsSum = totalSecondsRoom1 + totalSecondsRoom2 + totalSecondsRoom3 + totalSecondsRoom4;
        double room1TimePercentage = Math.Round(((double)totalSecondsRoom1 / roomSecondsSum) * 100, 2);
        double room2TimePercentage = Math.Round(((double)totalSecondsRoom2 / roomSecondsSum) * 100, 2);
        double room3TimePercentage = Math.Round(((double)totalSecondsRoom3 / roomSecondsSum) * 100, 2);
        double room4TimePercentage = Math.Round(((double)totalSecondsRoom4 / roomSecondsSum) * 100, 2);

        //initialize the room time percentage UI
        room1TimePercentageText.text = room1TimePercentage.ToString() + " %";
        room2TimePercentageText.text = room2TimePercentage.ToString() + " %";
        room3TimePercentageText.text = room3TimePercentage.ToString() + " %";
        room4TimePercentageText.text = room4TimePercentage.ToString() + " %";
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void onBackButtonClick()
    {
        SceneManager.LoadScene(0);
    }
}
