using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class HeatMapManagingScript : MonoBehaviour
{
    public Text lastNameText;
    public Text firstNameText;
    public Text ageText;
    public Text room1TimeText;
    public Text room2TimeText;
    public Text room3TimeText;
    public Text room4TimeText;
    // Start is called before the first frame update
    void Start()
    {
        lastNameText.text = "Last name: " + StaticData.userHeatMapLastName;
        firstNameText.text = "First name: " + StaticData.userHeatMapFirstName;
        ageText.text = "Age: " + StaticData.userHeatMapAge.ToString();
        room1TimeText.text = "Room1 time: " + StaticData.userHeatMapRoom1Time;
        room2TimeText.text = "Room2 time: " + StaticData.userHeatMapRoom2Time;
        room3TimeText.text = "Room3 time: " + StaticData.userHeatMapRoom3Time;
        room4TimeText.text = "Room4 time: " + StaticData.userHeatMapRoom4Time;
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
