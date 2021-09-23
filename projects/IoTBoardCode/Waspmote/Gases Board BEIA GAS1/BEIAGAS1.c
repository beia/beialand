/*
 *   ------------  [Ga_v30_01] - Temperature, Humidty and Pressure  --------------
 *
 *   Explanation: This example read the temperature, humidity and
 *   pressure values from BME280 sensor
 *
 *   Copyright (C) 2016 Libelium Comunicaciones Distribuidas S.L.
 *   http://www.libelium.com
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Version:           3.0
 *   Design:            David Gascón
 *   Implementation:    Ahmad Saad
*/

// Library include
#include <WaspSensorGas_v30.h>
#include <WaspFrame.h>
#include <WaspWIFI_PRO.h> 
#include "WaspFrameConstantsv15.h"

// choose socket (SELECT USER'S SOCKET)
///////////////////////////////////////
uint8_t socket = SOCKET0;
///////////////////////////////////////


// choose URL settings
///////////////////////////////////////
char type[] = "http";
char host[] = "82.78.81.178";
char port[] = "80";
///////////////////////////////////////

uint8_t error;
uint8_t status;
unsigned long previous;

float VOLT;
uint16_t CHRG;

#define NUM_OF_POINTS 3

//*********************************************************************************
// O2 Sensor must be connected in SOCKET1
//*********************************************************************************
O2SensorClass O2Sensor(SOCKET_1);

// Percentage values of Oxygen
#define POINT1_PERCENTAGE 0.0
#define POINT2_PERCENTAGE 5.0
// Calibration Voltage Obtained during calibration process (in mV)
#define POINT1_VOLTAGE 0.35
#define POINT2_VOLTAGE 2.0

float concentrations_o2[] = {POINT1_PERCENTAGE, POINT2_PERCENTAGE};
float voltages_o2[] =       {POINT1_VOLTAGE, POINT2_VOLTAGE};


//*********************************************************************************
// CO2 Sensor must be connected physically in SOCKET2
//*********************************************************************************
CO2SensorClass CO2Sensor(SOCKET_2);

// Concentratios used in calibration process
#define POINT1_PPM_CO2 350.0    // PPM VALUE <-- Normal concentration in air
#define POINT2_PPM_CO2 1000.0   // PPM VALUE
#define POINT3_PPM_CO2 3000.0   // PPM VALUE

// Calibration vVoltages obtained during calibration process
#define POINT1_VOLT_CO2 0.300
#define POINT2_VOLT_CO2 0.350
#define POINT3_VOLT_CO2 0.380

float concentrations_co2[] = {POINT1_PPM_CO2, POINT2_PPM_CO2, POINT3_PPM_CO2};
float voltages_co2[] =       {POINT1_VOLT_CO2, POINT2_VOLT_CO2, POINT3_VOLT_CO2};


//*********************************************************************************
// NO2 Sensor must be connected physically in SOCKET3
//*********************************************************************************
NO2SensorClass NO2Sensor(SOCKET_3);

// Concentratios used in calibration process
#define POINT1_PPM_NO2 10.0   // PPM VALUE <-- Normal concentration in air
#define POINT2_PPM_NO2 50.0   // PPM VALUE
#define POINT3_PPM_NO2 100.0  // PPM VALUE

// Calibration voltages obtained during calibration process (in KOHMs)
#define POINT1_RES_NO2 45.25  // <-- Rs at normal concentration in air
#define POINT2_RES_NO2 25.50
#define POINT3_RES_NO2 3.55

float concentrations_no2[] = {POINT1_PPM_NO2, POINT2_PPM_NO2, POINT3_PPM_NO2};
float voltages_no2[] =       {POINT1_RES_NO2, POINT2_RES_NO2, POINT3_RES_NO2};

//*********************************************************************************
// CO Sensor must be connected physically in SOCKET_4
//*********************************************************************************
COSensorClass COSensor(SOCKET_4); 

// Concentratios used in calibration process
#define POINT1_PPM_CO 100.0   // <--- Ro value at this concentration
#define POINT2_PPM_CO 300.0   // 
#define POINT3_PPM_CO 1000.0  // 

// Calibration resistances obtained during calibration process
#define POINT1_RES_CO 230.30 // <-- Ro Resistance at 100 ppm. Necessary value.
#define POINT2_RES_CO 40.665 //
#define POINT3_RES_CO 20.300 //

float concentrations_co[] = { POINT1_PPM_CO, POINT2_PPM_CO, POINT3_PPM_CO };
float resValues_co[] =      { POINT1_RES_CO, POINT2_RES_CO, POINT3_RES_CO };
//*********************************************************************************
// VOC Sensor must be connected physically in SOCKET5
//*********************************************************************************
VOCSensorClass VOCSensor(SOCKET_5); 

// Concentratios used in calibration process (PPM VALUE)
#define POINT1_PPM_VOC 100.0   //  <--- Ro value at this concentration
#define POINT2_PPM_VOC 300.0   
#define POINT3_PPM_VOC 1000.0 

// Calibration resistances obtained during calibration process
#define POINT1_RES_VOC 230.30 // <-- Ro Resistance at 100 ppm. Necessary value.
#define POINT2_RES_VOC 40.665 // 
#define POINT3_RES_VOC 20.300 // 

float concentrations[] = { POINT1_PPM_VOC, POINT2_PPM_VOC, POINT3_PPM_VOC };
float resValues[] =      { POINT1_RES_VOC, POINT2_RES_VOC, POINT3_RES_VOC };


//*********************************************************************************
// LPG Sensor can be connected in SOCKET6 AND SOCKET7
//*********************************************************************************
LPGSensorClass LPGSensor(SOCKET_6); ; // <---- SOCKET7 Class used

// Concentratios used in calibration process
#define POINT1_PPM_LPG 10.0   // PPM VALUE <-- Normal concentration in air
#define POINT2_PPM_LPG 50.0   // PPM VALUE
#define POINT3_PPM_LPG 100.0  // PPM VALUE

// Calibration voltages obtained during calibration process (in KOHMs)
#define POINT1_RES_LPG 45.25  // <-- Rs at normal concentration in air
#define POINT2_RES_LPG 25.50
#define POINT3_RES_LPG 3.55

float concentrations_lps[] = {POINT1_PPM_LPG, POINT2_PPM_LPG, POINT3_PPM_LPG};
float voltages_lps[] =       {POINT1_RES_LPG, POINT2_RES_LPG, POINT3_RES_LPG};

//*********************************************************************************
// Solvent Vapors Sensor / APP1 must be connected physically in SOCKET_6 or SOCKET_7
//*********************************************************************************
//APSensorClass APPSensor(SOCKET_7); //


SVSensorClass SVSensor(SOCKET_7); 

// Concentratios used in calibration process
#define POINT1_PPM_SV 10.0  // <-- Normal concentration in air
#define POINT2_PPM_SV 50.0 
#define POINT3_PPM_SV 100.0  

// Calibration voltages obtained during calibration process (in KOHMs)
#define POINT1_RES_SV 45.25  // <-- Rs at normal concentration in air
#define POINT2_RES_SV 25.50
#define POINT3_RES_SV 3.55

float concentrations_sv[] = { POINT1_PPM_SV, POINT2_PPM_SV, POINT3_PPM_SV };
float voltages_sv[] =       { POINT1_RES_SV, POINT2_RES_SV, POINT3_RES_SV };

//*********************************************************************************
// BME280 Definitions
//*********************************************************************************
#include <BME280.h>

float temperature; // Stores the temperature in ºC
float humidity;    // Stores the realitve humidity in %RH
float pressure;    // Stores the pressure in Pa

//*********************************************************************************


char node_ID[] = "BEIA_GAS1"; //ex GAS_WiFi

void setup()
{
  // Configure the calibration values
  O2Sensor.setCalibrationPoints(voltages_o2, concentrations_o2);
  // Configure the calibration values
  CO2Sensor.setCalibrationPoints(voltages_co2, concentrations_co2, NUM_OF_POINTS);
  // Configure the calibration values
  NO2Sensor.setCalibrationPoints(voltages_no2, concentrations_no2, NUM_OF_POINTS);
  // Configure the calibration values
  VOCSensor.setCalibrationPoints(resValues, concentrations, NUM_OF_POINTS);
  // Configure the calibration values
  LPGSensor.setCalibrationPoints(voltages_lps, concentrations_lps, NUM_OF_POINTS);
  // Calculate the slope and the intersection of the logarithmic function
  COSensor.setCalibrationPoints(resValues_co, concentrations_co, NUM_OF_POINTS);
  // Calculate the slope and the intersection of the logarithmic function
  SVSensor.setCalibrationPoints(voltages_sv, concentrations_sv, NUM_OF_POINTS);
  // Concentratios used in calibration process (in PPM)
  /*APPSensor.concentrations[POINT_1] = 10.0;  // <--- Ro value at this concentration
  APPSensor.concentrations[POINT_2] = 50.0 ;  
  APPSensor.concentrations[POINT_3] = 100.0;   
  // Calibration resistances obtained during calibration process (in Kohms)
  APPSensor.values[POINT_1] = 45.25; // <-- Ro Resistance at 100 ppm. Necessary value.
  APPSensor.values[POINT_2] = 25.665;  
  APPSensor.values[POINT_3] = 2.300;
  // Define the number of calibration points
  APPSensor.numPoints = 3;
  // Calculate the slope and the intersection of the logarithmic function
  APPSensor.setCalibrationPoints(); */
  USB.ON();
  USB.println(F("Frame Gases_Board"));
  USB.println(F("Sensors used:"));
  USB.println(F("- SOCKET_1: O2 sensor"));
  USB.println(F("- SOCKET_2: CO2 sensor)"));
  USB.println(F("- SOCKET_3: NO2 sensor"));
  USB.println(F("- SOCKET_4: CO sensor"));
  USB.println(F("- SOCKET_5: VOC sensor"));
  USB.println(F("- SOCKET_6: LPG sensor"));
  USB.println(F("- SOCKET_7: APP1 / SV [1] sensor"));
  USB.println(F("- SOCKET_8: BME280 sensor (temperature, humidity & pressure"));

  // Set the Waspmote ID
  frame.setID(node_ID);

  ///////////////////////////////////////////
  // 1. Turn on the board
  ///////////////////////////////////////////
  
  //Power gases board and wait for stabilization and sensor response time
  Gases.ON();
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
    
  //////////////////////////////////////////////////////////////////////
  // 2. Read Sensors
  //////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////
  // 2.1 Read Temperature, Humidity and Pressure Sensor (BME280)
  //////////////////////////////////////////////////////////////////////

  // Read enviromental variables
  temperature = Gases.getTemperature();
  humidity = Gases.getHumidity();
  pressure = Gases.getPressure();
  
  VOLT = PWR.getBatteryVolts();
 // CHRG = PWR.getBatteryCurrent();
  //////////////////////////////////////////////////////////////////////
  // 2.2 Read O2 Sensor - Connected in SOCKET1
  //////////////////////////////////////////////////////////////////////
  O2Sensor.ON();
  delay(1000);
  // O2 Sensor does not need power suplly
  float O2Vol = O2Sensor.readVoltage();
  delay(100);
   // Read the concentration value in %
  float O2Val = O2Sensor.readConcentration();
  O2Sensor.OFF();

  //////////////////////////////////////////////////////////////////////
  // 2.3 Read CO2 Sensor - Connected in SOCKET2
  //////////////////////////////////////////////////////////////////////
  CO2Sensor.ON();
  delay(1000);
  // PPM value of CO2
  // Voltage value of the sensor
  float CO2Vol = CO2Sensor.readVoltage();
  // PPM value of CO2
  float CO2PPM = CO2Sensor.readConcentration();
  CO2Sensor.OFF();

  //////////////////////////////////////////////////////////////////////
  // 2.4 Read NO2 Sensor - Connected in SOCKET3
  //////////////////////////////////////////////////////////////////////
  NO2Sensor.ON();
  delay(1000);
  // PPM value of NO2
  float NO2Vol = NO2Sensor.readVoltage();       // Voltage value of the sensor
  float NO2Res = NO2Sensor.readResistance();    // Resistance of the sensor
  float NO2PPM = NO2Sensor.readConcentration(); // PPM value of NO2
  NO2Sensor.OFF();

  //////////////////////////////////////////////////////////////////////
  // 2.4 Read VOC Sensor - Connected in SOCKET5
  //////////////////////////////////////////////////////////////////////
  VOCSensor.ON();
  delay(1000);
  // PPM value of VOC
  float VOCVol = VOCSensor.readVoltage();       // Voltage value of the sensor
  float VOCRes = VOCSensor.readResistance();    // Resistance of the sensor
  float VOCPPM = VOCSensor.readConcentration(); // PPM value of VOC
  VOCSensor.OFF();

  // 2.5 Read LPG Sensor - Connected in SOCKET7
  //////////////////////////////////////////////////////////////////////
  LPGSensor.ON();
  delay(1000);
  // PPM value of CH4
  float LPGVol = LPGSensor.readVoltage();         // Voltage value of the sensor
  float LPGRes = LPGSensor.readResistance();      // Resistance of the sensor
  float LPGPPM = LPGSensor.readConcentration();   // PPM value of LPGfloat LPGPPM = LPGSensor.readConcentration();
  LPGSensor.OFF();
  
  // 2.6 Read LPG Sensor - Connected in SOCKET6
  //////////////////////////////////////////////////////////////////////
  COSensor.ON();
  // PPM value of CO
  delay(1000);
  float COVol = COSensor.readVoltage();          // Voltage value of the sensor
  float CORes = COSensor.readResistance();       // Resistance of the sensor
  float COPPM = COSensor.readConcentration(); // PPM value of CO
  COSensor.OFF();

  // 2.7 Read SV Sensor - Connected in SOCKET7
  //////////////////////////////////////////////////////////////////////
  SVSensor.ON();
  // PPM value of SV
  float SVVol = SVSensor.readVoltage();       // Voltage value of the sensor
  float SVRes = SVSensor.readResistance();    // Resistance of the sensor
  float SVPPM = SVSensor.readConcentration(); // PPM value of Solvent Vapor sensor
  //SVSensor.OFF();
  
  /*
  // 2.6 Read APP1 Sensor - Connected in SOCKET7
  APPSensor.ON();
  delay(1000);
  // PPM value of APP1
  float APPVol = APPSensor.readVoltage();         // Voltage value of the sensor
  float APPRes = APPSensor.readResistance();      // Resistance of the sensor
  float APP_PPM = APPSensor.readConcentration();  // PPM value of AP1
  */

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
  /////////////////print of results//////////////////////////
  // O2 Sensor does not need power suplly
  USB.print(F("O2 concentration Estimated: "));
  USB.print(O2Vol);
  USB.println(F(" mV"));
  
  USB.print(F("O2 concentration Estimated: "));
  USB.print(O2Val);
  USB.println(F(" %"));  

  // Print of the results
  USB.print(F("NO2 Sensor Voltage: "));
  USB.print(NO2Vol);
  USB.println(F(" V"));
  
  // Print of the results
  USB.print(F("NO2 Sensor Resistance: "));
  USB.print(NO2Res);
  USB.println(F(" Ohms"));

  // Print of the results
  USB.print(F("NO2 concentration Estimated: "));
  USB.print(NO2PPM);
  USB.println(F(" PPM"));
 
  
  // Print of the results
  USB.print(F("CO Sensor Voltage: "));
  USB.print(COVol);
  USB.print(F(" mV"));

  // Print of the results
  USB.print(F("CO Sensor Resistance: "));
  USB.print(CORes);
  USB.print(F(" Ohms"));

  USB.print(F("CO concentration Estimated: "));
  USB.print(COPPM);
  USB.println(F(" ppm"));

  // Print of the results
  USB.print(F("CO2 Sensor Voltage: "));
  USB.print(CO2Vol);
  USB.println(F("volts"));
  
  USB.print(F("CO2 concentration estimated: "));
  USB.print(CO2PPM);
  USB.println(F(" ppm"));

  // Print of the results
  USB.print(F("VOC Sensor Voltage: "));
  USB.print(VOCVol);
  USB.println(F(" V"));

  // Print of the results
  USB.print(F("VOC Sensor Resistance: "));
  USB.print(VOCRes);
  USB.println(F(" Ohms"));

  USB.print(F("VOC concentration Estimated: "));
  USB.print(VOCPPM);
  USB.println(F(" ppm"));

  // Print of the results
  USB.print(F("Solvent Vapors Sensor Voltage: "));
  USB.print(SVVol);
  USB.println(F(" V"));
  
  // Print of the results
  USB.print(F("Solvent Vapors Sensor Resistance: "));
  USB.print(SVRes);
  USB.println(F(" Ohms"));

  // Print of the results
  USB.print(F("Solvent Vapors concentration Estimated: "));
  USB.print(SVPPM);
  USB.println(F(" PPM"));

  // Print of the results
  USB.print(F("LPG Sensor Voltage: "));
  USB.print(LPGVol);
  USB.println(F(" V"));

  // Print of the results
  USB.print(F("LPG Sensor Resistance: "));
  USB.print(LPGRes);
  USB.println(F(" Ohms"));

  USB.print(F("LPG concentration Estimated: "));
  USB.print(LPGPPM);
  USB.println(F(" ppm"));
/*
  // Print of the results
  USB.print(F("Air Pollutans Sensor Voltage: "));
  USB.print(APPVol);
  USB.println(F(" V |"));

  // Print of the results
  USB.print(F(" Air Pollutans Sensor Resistance: "));
  USB.print(APPRes);
  USB.println(F(" Ohms |"));

  USB.print(F(" Air Pollutans concentration Estimated: "));
  USB.print(APP_PPM);
  USB.println(F(" ppm"));
  */

  // Show the remaining battery level
  USB.print(F("Battery Level: "));
  USB.print(PWR.getBatteryLevel(),DEC);
  USB.println(F(" %"));
  
  // Show the battery Volts
  USB.print(F("Battery (Volts): "));
  USB.print(PWR.getBatteryVolts());
  USB.println(F(" V"));

  // Show the battery charging current (only from solar panel)
  //USB.print(F("Battery charging current (only from solar panel): "));
  //USB.print(CHRG, DEC);
 // USB.println(F(" mA"));

  
  // 3. Create ASCII frame
  ///////////////////////////////////////////

   // Create new frame (ASCII)
  frame.createFrame(BINARY, node_ID);
  
  //frame.addSensor(SENSOR_TIME, RTC.hour, RTC.minute, RTC.second);
  
  // Add Oxygen voltage value
  frame.addSensor(SENSOR_GASES_O2, O2Val);
  // Add CO2 PPM value
  //frame.addSensor(SENSOR_GASES_CO2, CO2PPM);
  // Add NO2 PPM value
  frame.addSensor(SENSOR_GASES_NO2, NO2PPM);
  // Add VOC PPM value
  frame.addSensor(SENSOR_GASES_VOC, VOCPPM);
   // Add LPG PPM value
  frame.addSensor(SENSOR_GASES_LPG, LPGPPM);
   // Add CO PPM value
  frame.addSensor(SENSOR_GASES_CO, COPPM);
   // Add SV PPM value
  frame.addSensor(SENSOR_GASES_SV, SVPPM);
  // Add APP1 PPM value
  //frame.addSensor(SENSOR_GASES_AP1, APP_PPM);
   // Add BAT level  value
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());

 
  frame.addSensor(SENSOR_GASES_TC, Gases.getTemperature());
  frame.addSensor(SENSOR_GASES_HUM, Gases.getHumidity());
  frame.addSensor(SENSOR_GASES_PRES, Gases.getPressure());
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

  PWR.deepSleep("00:00:00:10",RTC_OFFSET,RTC_ALM1_MODE1,ALL_ON);
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