/*
    --------------- 4G_08 - Send frames to Meshlium  ---------------

    Explanation: This example shows how to use HTTP requests to send
    Waspmote frames from Waspmote to Meshlium. Therefore, the Meshlium
    will be able to parse the sensor data and inset it into the database

    NOTE: This example is valid for Meshlium Manager System versions
    prior to v4.0.9 which only supports HTTPS.

    Copyright (C) 2018 Libelium Comunicaciones Distribuidas S.L.
    http://www.libelium.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program.  If not, see <http://www.gnu.org/licenses/>.

    Version:           3.0
    Design:            David Gascón
    Implementation:    Alejandro Gállego
*/

#include <Wasp4G.h>
#include <WaspFrame.h>
#include <WaspSensorSW.h>
#include "WaspFrameConstantsv15.h"

// APN settings
///////////////////////////////////////
char apn[] = "net";
char login[] = "";
char password[] = "";
///////////////////////////////////////

// SERVER settings
///////////////////////////////////////
char host[] = "82.78.81.178";
uint16_t port = 80;
///////////////////////////////////////

// define the Waspmote ID
char moteID[] = "SW1";
int error;


// This variable will store the temperature value
float temp;
float VOLT;

float value_orp;
float value_orp_calculated;

float value_do;
float value_do_calculated;

float pHVol;
float pHValue;

float ECRes;
float ECValue;

// Temperature at which calibration was carried out
#define cal_temp 27.3
// Offset obtained from sensor calibration
#define calibration_offset 0.018
// Calibration of the sensor in normal air
#define air_calibration 2.517
// Calibration of the sensor under 0% solution
#define zero_calibration 0.0

// Calibration values
#define cal_point_10  1.94
#define cal_point_7   2.050
#define cal_point_4   2.1165

// Value 1 used to calibrate the sensor
#define point1_cond 10500
// Value 2 used to calibrate the sensor
#define point2_cond 40000

// Point 1 of the calibration 
#define point1_cal 197.00
// Point 2 of the calibration 
#define point2_cal 150.00


ORPClass ORPSensor;
DOClass DOSensor;
pHClass pHSensor;
conductivityClass ConductivitySensor;
pt1000Class TemperatureSensor;


uint16_t CHRG;
// Create an instance of the class


void setup()
{
  USB.ON();
  USB.println(F("Start program"));

  // set the Waspmote ID
  frame.setID(moteID);

  // Configure the calibration values
  pHSensor.setCalibrationPoints(cal_point_10, cal_point_7, cal_point_4, cal_temp);
  DOSensor.setCalibrationPoints(air_calibration, zero_calibration);
  ConductivitySensor.setCalibrationPoints(point1_cond, point1_cal, point2_cond, point2_cal);  

  
  //////////////////////////////////////////////////
  // 1. sets operator parameters
  //////////////////////////////////////////////////
  _4G.set_APN(apn, login, password);

  //////////////////////////////////////////////////
  // 2. Show APN settings via USB port
  //////////////////////////////////////////////////
  _4G.show_APN();

  USB.println(F("This is a Smart Water data frame..."));
  
  ///////////////////////////////////////////
  // 1. Turn ON the Smart Water sensor board 
  ///////////////////////////////////////////  

}



void loop()
{
  //////////////////////////////////////////////////
  // 1. Switch ON
  //////////////////////////////////////////////////
  error = _4G.ON();

  if (error == 0)
  {
    USB.println(F("1. 4G module ready..."));


    ////////////////////////////////////////////////
    // 2. Create new frame
    ////////////////////////////////////////////////

    RTC.ON();
    RTC.getTime();
      
    Water.ON(); 
    delay(2000);

    ///////////////////////////////////////////
  // 2. read the sensors
  ///////////////////////////////////////////
  
  // Reading of the Temperature sensor
  temp = TemperatureSensor.readTemperature();
  VOLT = PWR.getBatteryVolts();
  CHRG = PWR.getBatteryCurrent();

  // Reading of the ORP sensor
  value_orp = ORPSensor.readORP();
  // Apply the calibration offset
  value_orp_calculated = value_orp - calibration_offset;
  // Reading of the ORP sensor
  value_do = DOSensor.readDO();
  // Conversion from volts into dissolved oxygen percentage
  value_do_calculated = DOSensor.DOConversion(value_do);
 // Read the ph sensor (voltage value)
  pHVol = pHSensor.readpH();
  // Read the temperature sensor
  // Convert the value read with the information obtained in calibration
  pHValue = pHSensor.pHConversion(pHVol, temp);
   // Reading of the Conductivity sensor
  ECRes = ConductivitySensor.readConductivity();
  // Conversion from resistance into us/cm
  ECValue = ConductivitySensor.conductivityConversion(ECRes);

  ///////////////////////////////////////////
  // 3. Print the output values
  ///////////////////////////////////////////

  // Print of the results
  USB.print(F("Temperature (celsius degrees): "));
  USB.println(temp);

  // Show the remaining battery level
  USB.print(F("Battery Level: "));
  USB.print(PWR.getBatteryLevel(),DEC);
  USB.print(F(" %"));
  
  // Show the battery Volts
  USB.print(F(" | Battery (Volts): "));
  USB.print(PWR.getBatteryVolts());
  USB.println(F(" V"));

  // Show the battery charging current (only from solar panel)
  USB.print(F("Battery charging current (only from solar panel): "));
  USB.print(CHRG, DEC);
  USB.println(F(" mA"));

  USB.print(F("ORP Estimated: "));
  USB.print(value_orp_calculated);
  USB.println(F(" volts"));  

  // Print of the results
  USB.print(F("DO Output Voltage: "));
  USB.print(value_do);
  
  // Print of the results
  USB.print(F(" DO Percentage: "));
  USB.println(value_do_calculated);

  USB.print(F("pH value: "));
  USB.print(pHVol);
  USB.println(F("volts  | "));
  USB.print(F(" pH Estimated: "));
  USB.println(pHValue);

  // Print of the results
  USB.print(F("Conductivity Output Resistance: "));
  USB.println(ECRes);
  
  // Print of the results
  USB.print(F(" Conductivity of the solution (uS/cm): "));
  USB.println(ECValue);
  
   // 3. Turn off the sensors
  /////////////////////////////////////////// 

   Water.OFF();

    // Create new frame (ASCII)
    frame.createFrame(ASCII);
    // set frame fields (Time from RTC)
    frame.addSensor(SENSOR_TIME, RTC.hour, RTC.minute, RTC.second);
     // Add temperature
  //  USB.print(F(" a crapat iar "));
    frame.addSensor(SENSOR_WATER_WT, temp);
   // USB.print(F(" a crapat si din nou"));
    frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());
    //USB.print(F(" a crapat iarrrrr"));
    frame.addSensor(SENSOR_BAT_VOLT, VOLT);
    frame.addSensor(SENSOR_BAT_CURR, CHRG);
     // Add PH
    frame.addSensor(SENSOR_WATER_PH, pHValue);
     // Add ORP value
    frame.addSensor(SENSOR_WATER_ORP, value_orp_calculated);
    // Add DO value
    frame.addSensor(SENSOR_WATER_DO, value_do_calculated);
    // Add conductivity value
    frame.addSensor(SENSOR_WATER_COND, ECValue);
    
   // USB.print(F(" a crapat de tot "));
    // Show the frame
    frame.showFrame();

    ////////////////////////////////////////////////
    // 3. Send to Meshlium
    ////////////////////////////////////////////////
    USB.print(F("Sending the frame..."));
    error = _4G.sendFrameToMeshlium( host, port, frame.buffer, frame.length);

    // check the answer
    if ( error == 0)
    {
      USB.print(F("Done. HTTP code: "));
      USB.println(_4G._httpCode);
      USB.print("Server response: ");
      USB.println(_4G._buffer, _4G._length);
    }
    else
    {
      USB.print(F("Failed. Error code: "));
      USB.println(error, DEC);
    }
  }
  else
  {
    // Problem with the communication with the 4G module
    USB.println(F("4G module not started"));
    USB.print(F("Error code: "));
    USB.println(error, DEC);
  }


  ////////////////////////////////////////////////
  // 4. Powers off the 4G module
  ////////////////////////////////////////////////
  USB.println(F("4. Switch OFF 4G module"));
  _4G.OFF();


  ////////////////////////////////////////////////
  // 5. Sleep
  ////////////////////////////////////////////////
  USB.println(F("5. Enter deep sleep..."));
  PWR.deepSleep("00:00:10:00", RTC_OFFSET, RTC_ALM1_MODE1, ALL_OFF);

  USB.ON();
  USB.println(F("6. Wake up!!\n\n"));

}