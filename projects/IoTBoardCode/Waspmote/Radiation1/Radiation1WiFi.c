#include <WaspWIFI_PRO.h>  //modificat
#include <WaspFrame.h>
#include <WaspSensorRadiation.h>

// citeste accelerometru
// choose socket (SELECT USER'S SOCKET)
uint8_t socket = SOCKET0;
///////////////////////////////////////
uint8_t status;
int x_acc;
int y_acc;
int z_acc;


// choose URL settings
///////////////////////////////////////
char type[] = "http";
char host[] = "82.78.81.171";
char port[] = "80";
///////////////////////////////////////

uint8_t error;
unsigned long previous;


char node_ID[] = "RADIATION1";

void setup()
{
    USB.println(F("Start program"));  
    USB.println(F("***************************************"));  
    USB.println(F("Once the module is set with one or more"));
    USB.println(F("AP settings, it attempts to join the AP"));
    USB.println(F("automatically once it is powered on"));    
    USB.println(F("Refer to example 'WIFI_PRO_01' to configure"));  
    USB.println(F("the WiFi module with proper settings"));
    USB.println(F("***************************************"));

    ACC.ON();

  
    // open USB port
    USB.ON();
    USB.println(F(" Radiation board sensor example"));

    frame.setID(node_ID);
    
} 

void loop()

{
   // get actual time
  previous = millis();
  //////////////////////////////////////////////////
  // 1. Switch ON
  //////////////////////////////////////////////////  
  error = WIFI_PRO.ON(socket);

  if (error == 0)
  {    
    USB.println(F("WiFi switched ON"));
  }
  else
  {
    USB.println(F("WiFi did not initialize correctly"));
  }
  //////////////////////////////////////////////////
  // 2. Join AP
  //////////////////////////////////////////////////  
  // check connectivity
  status =  WIFI_PRO.isConnected();

  // check if module is connected
  if (status == true)
  {    
    USB.print(F("WiFi is connected OK"));
    USB.print(F(" Time(ms):"));    
    USB.println(millis()-previous);

  ///////////////////////////////////////////
  // 1. Create ASCII frame
  ///////////////////////////////////////////  
 ///////////////////////////////////////////
 //----------Check Register-----------------------
  // should always answer 0x32, it is used to check
  // the proper functionality of the accelerometer
  status = ACC.check();

  //----------X Value-----------------------
  x_acc = ACC.getX();

  //----------Y Value-----------------------
  y_acc = ACC.getY();

  //----------Z Value-----------------------
  z_acc = ACC.getZ();

  //-------------------------------

  USB.print(F("\n------------------------------\nCheck: 0x")); 
  USB.println(status, HEX);
  USB.println(F("\n \t0X\t0Y\t0Z")); 
  USB.print(F(" ACC\t")); 
  USB.print(x_acc, DEC);
  USB.print(F("\t")); 
  USB.print(y_acc, DEC);
  USB.print(F("\t")); 
  USB.println(z_acc, DEC);

  delay(1000);
  
  //  Turn on the board
  /////////////////////////////////////////// 
  RadiationBoard.ON();
  delay(100);
  
  // create new frame
  frame.createFrame(ASCII, node_ID);  

  USB.println(F("Measuring radiation"));
  float radiation = RadiationBoard.getRadiation();
  float radiationcpm = RadiationBoard.getCPM(5000);
  
  USB.print(F("radiation[uSv/h]: "));
  USB.println(radiation);
  USB.println();

 
  USB.print(F("radiation[cpm]: "));
  USB.println(radiationcpm);
  USB.println();

  
  // add frame fields
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel()); 
  frame.addSensor(SENSOR_RADIATION, radiation);
//  frame.addSensor(SENSOR_RADIATION, radiationcpm);
  frame.addSensor(SENSOR_ACC, ACC.getX(), ACC.getY(), ACC.getZ());

    // Power off the board
  RadiationBoard.OFF();

  ///////////////////////////////////////////
  // 2. Send packet
  ///////////////////////////////////////////  

  // Show the frame
  frame.showFrame();
 ///////////////////////////////
    // 3.2. Send Frame to Meshlium
    ///////////////////////////////

    // http frame
    error = WIFI_PRO.sendFrameToMeshlium( type, host, port, frame.buffer, frame.length);

    // check response
    if (error == 0)
    {
      USB.println(F("HTTP OK"));          
      USB.print(F("HTTP Time from OFF state (ms):"));    
      USB.println(millis()-previous);
    }
    else
    {
      USB.println(F("Error calling 'getURL' function"));
      WIFI_PRO.printErrorCode();
    }
  }
  else
  {
    USB.print(F("WiFi is connected ERROR")); 
    USB.print(F(" Time(ms):"));    
    USB.println(millis()-previous);  
  }

  //////////////////////////////////////////////////
  // 3. Switch OFF
  //////////////////////////////////////////////////  
  WIFI_PRO.OFF(socket);
  USB.println(F("WiFi switched OFF\n\n")); 
  USB.println(F("Go to deepsleep After 30 minutes, Waspmote wakes up thanks to the RTC Alarm")); 

// Go to deepsleep  
    // After 30 seconds, Waspmote wakes up thanks to the RTC Alarm
  PWR.deepSleep("00:00:00:60", RTC_OFFSET, RTC_ALM1_MODE1, ALL_OFF);
}
