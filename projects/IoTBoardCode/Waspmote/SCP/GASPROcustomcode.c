/*
 *  ------------  [GP_v30_08] - Frame Class Utility  --------------
 *
 *  Explanation: This is the basic code to create a frame with some
 *   Gases Pro Sensor Board sensors
 *
 *  Copyright (C) 2019 Libelium Comunicaciones Distribuidas S.L.
 *  http://www.libelium.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Version:        3.2
 *  Design:             David Gascón
 *  Implementation:     Alejandro Gállego
 */

#include <WaspSensorGas_Pro.h>
#include <WaspFrame.h>
//#include <WaspPM.h>
#include <WaspWIFI_PRO.h> 
#include "WaspFrameConstantsv15.h"


// choose socket (SELECT USER'S SOCKET)
///////////////////////////////////////
uint8_t socket = SOCKET0;
///////////////////////////////////////


// choose URL settings
///////////////////////////////////////
char type[] = "http";
char host[] = "82.78.81.171";
char port[] = "80";
///////////////////////////////////////

uint8_t error;
uint8_t status;
unsigned long previous;

float VOLT;
uint16_t CHRG;


Gas O3(SOCKET_4);
Gas SO2(SOCKET_6);
Gas NO2(SOCKET_2);


float temperature;
float humidity;
float pressure;

float concO3;
float concSO2;
float concNO2;


char node_ID[] = "TELMONAER2";


void setup()
{
    USB.ON();
    USB.println(F("Frame Utility Example for Gases Pro Sensor Board"));

    // Set the Waspmote ID
    frame.setID(node_ID);
  USB.println(F("Frame Gases_Pro_Board"));
  USB.println(F("Sensors used:"));
  USB.println(F("- SOCKET_1:  sensor"));
  USB.println(F("- SOCKET_2: NO2 sensor)"));
  USB.println(F("- SOCKET_3:  sensor"));
  USB.println(F("- SOCKET_4: O3 sensor"));
  USB.println(F("- SOCKET_5: sensor"));
  USB.println(F("- SOCKET_6: SO2 sensor"));
  USB.println(F("- SOCKET_7: sensor"));
  USB.println(F("- SOCKET_8: BME280 sensor (temperature, humidity & pressure"));
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
    // 1. Turn on sensors and wait
    ///////////////////////////////////////////

    //Power on gas sensors
    O3.ON();
    SO2.ON();
    NO2.ON();

    // Sensors need time to warm up and get a response from gas
    // To reduce the battery consumption, use deepSleep instead delay
    // After 2 minutes, Waspmote wakes up thanks to the RTC Alarm
    PWR.deepSleep("00:00:02:00", RTC_OFFSET, RTC_ALM1_MODE1, ALL_ON);


    ///////////////////////////////////////////
    // 2. Read sensors
    ///////////////////////////////////////////

    // Read the sensors and compensate with the temperature internally
    concO3 = O3.getConc();
    concSO2 = SO2.getConc();
    concNO2 = NO2.getConc();

    // Read enviromental variables
    temperature = NO2.getTemp();
    humidity = NO2.getHumidity();
    pressure = NO2.getPressure();

    VOLT = PWR.getBatteryVolts();
    
    ///////////////////////////////////////////
    // 3. Turn off the sensors
    ///////////////////////////////////////////

    //Power off sensors
    O3.OFF();
    SO2.OFF();
    NO2.OFF();

   // Print of the results
    USB.print(F("Temperature: "));
    USB.print(temperature);
    USB.println(F(" Celsius Degrees"));
    
    USB.print(F("Humidity : "));
    USB.print(humidity);
    USB.println(F(" %RH"));
  
    USB.print(F("Pressure : "));
    USB.print(pressure);
    USB.println(F(" Pa"));
    
      // Print of the results
     USB.print(F("NO2 concentration : "));
     USB.print(concNO2);
     USB.println(F(" PPM"));
     
      // Print of the results
     USB.print(F("SO2 concentration : "));
     USB.print(concSO2);
     USB.println(F(" PPM"));

       // Print of the results
     USB.print(F("O3 concentration : "));
     USB.print(concO3);
     USB.println(F(" PPM"));

      // Show the remaining battery level
    USB.print(F("Battery Level: "));
    USB.print(PWR.getBatteryLevel(),DEC);
    USB.println(F(" %"));
    
    // Show the battery Volts
    USB.print(F("Battery (Volts): "));
    USB.print(PWR.getBatteryVolts());
    USB.println(F(" V"));
     
    ///////////////////////////////////////////
    // 5. Create ASCII frame
    ///////////////////////////////////////////

    // Create new frame (ASCII)
    frame.createFrame(BINARY);

    // Add temperature
    frame.addSensor(SENSOR_GASES_PRO_TC, temperature);
    // Add humidity
    frame.addSensor(SENSOR_GASES_PRO_HUM, humidity);
    // Add pressure value
    frame.addSensor(SENSOR_GASES_PRO_PRES, pressure);
    // Add O3 value
    frame.addSensor(SENSOR_GASES_PRO_O3, concO3);
    // Add NO value
    frame.addSensor(SENSOR_GASES_PRO_SO2, concSO2);
    // Add NO2 value
    frame.addSensor(SENSOR_GASES_PRO_NO2, concNO2);

    // Show the frame
    frame.showFrame();


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
 
      frame.createFrame(ASCII, node_ID);
      frame.addSensor(SENSOR_BAT_VOLT, VOLT);
      frame.addSensor(SENSOR_BAT_CURR, CHRG); 
      WIFI_PRO.sendFrameToMeshlium( type, host, port, frame.buffer, frame.length);
      USB.println(F("ASCII FRAME SEND OK")); 
    
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
  // Go to deepsleep  
    // After 30 seconds, Waspmote wakes up thanks to the RTC Alarm
   USB.println(F("enter deep sleep"));
  // Go to sleep disconnecting all switches and modules
  // After 10 seconds, Waspmote wakes up thanks to the RTC Alarm
    USB.println(RTC.getTime());

  PWR.deepSleep("00:00:18:10",RTC_OFFSET,RTC_ALM1_MODE1,ALL_ON);
       USB.ON();
       USB.println(RTC.getTime());
  USB.println(F("\nwake up"));

  // After wake up check interruption source
  if( intFlag & RTC_INT )
  {
    // clear interruption flag
    intFlag &= ~(RTC_INT);
    
    USB.println(F("---------------------"));
    USB.println(F("RTC INT captured"));
    USB.println(F("---------------------"));
    Utils.blinkLEDs(300);
    Utils.blinkLEDs(300);
    Utils.blinkLEDs(300);
  }
 //delay (60000);
}