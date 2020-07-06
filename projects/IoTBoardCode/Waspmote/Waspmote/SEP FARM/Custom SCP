/*
    ------------  [SCP_v30_09] - Frame Class Utility  --------------

    Explanation: This is the basic code to create a frame with some
    Smart Cities Pro Sensor Board sensors

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

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Version:        3.3
    Design:         David Gascón
    Implementation: Alejandro Gállego
*/
#include <Wasp4G.h>
#include <WaspSensorCities_PRO.h>
#include <WaspFrame.h>
#include <WaspPM.h>
#include "WaspFrameConstantsv15.h"


// APN settings
///////////////////////////////////////
char apn[] = "";
char login[] = "";
char password[] = "";
///////////////////////////////////////

// SERVER settings
///////////////////////////////////////
char host[] = "82.78.81.178";
uint16_t port = 80;
///////////////////////////////////////

// define the Waspmote ID
int error;

/*
   Define objects for sensors
   Imagine we have a P&S! with the next sensors:
    - SOCKET_A: BME280 sensor (temperature, humidity & pressure)
    - SOCKET_B: Electrochemical gas sensor (O3)
    - SOCKET_C: Electrochemical gas sensor (NO2)
    - SOCKET_D: Particle matter sensor (dust)
    - SOCKET_E: Luxes sensor
    - SOCKET_F: Pellistor sensor (CO2)
*/
bmeCitiesSensor bme(SOCKET_E);
//luxesCitiesSensor luxes(SOCKET_A);
ultrasoundCitiesSensor  ultrasound(SOCKET_A);
Gas sensor_ch4(SOCKET_B);
Gas sensor_h2s(SOCKET_C);
Gas sensor_nh3(SOCKET_F);


// define vars for sensor values
float temperature;
float humidity;
float pressure;
//uint32_t luminosity;
uint16_t range;


float concentration_ch4;
float concentration_h2s;
float concentration_nh3;
//int OPC_status;
//int OPC_measure;

float VOLT;
uint16_t CHRG;
char moteID[] = "FARM";
uint8_t connection_status;
void setup()
{
  USB.ON();
  USB.println(F("Frame Utility Example for Cities Pro Board"));
  USB.println(F("Sensors used:"));
  USB.println(F("- SOCKET_A: Ultrasound sensor"));
  USB.println(F("- SOCKET_E: BME280 sensor (temperature, humidity & pressure)"));
  USB.println(F("- SOCKET_B: Electrochemical gas sensor (CH4)"));
  USB.println(F("- SOCKET_C: Electrochemical gas sensor (H2S)"));
 // USB.println(F("- SOCKET_D: Particle matter sensor (dust)"));
 // USB.println(F("- SOCKET_E: Luxes sensor"));
  USB.println(F("- SOCKET_F: Electrochemical sensor (NH3)"));

  // Set the Waspmote ID
  frame.setID(moteID);

  //////////////////////////////////////////////////
  // 1. sets operator parameters
  //////////////////////////////////////////////////
  _4G.set_APN(apn, login, password);

  //////////////////////////////////////////////////
  // 2. Show APN settings via USB port
  //////////////////////////////////////////////////
  _4G.show_APN();

  
  ///////////////////////////////////////////
  // 1. Turn ON the Smart Water sensor board 
  ///////////////////////////////////////////  
}


void loop()
{
  
    ////////////////////////////////////////////////
    // 2. Create new frame
    ////////////////////////////////////////////////

    RTC.ON();
    RTC.getTime();
      
  ///////////////////////////////////////////
  // 1. Read BME and luxes sensors
  ///////////////////////////////////////////

  // switch off all gas sensors for better performance
  sensor_ch4.OFF();
  sensor_h2s.OFF();
  sensor_nh3.OFF();

  // switch on BME sensor
  // read temperature, humidity and pressure
  // switch off BME sensor
  bme.ON();
  temperature = bme.getTemperature();
  humidity = bme.getHumidity();
  pressure = bme.getPressure();
  bme.OFF();

  // switch on luxes sensor
  // read luminosity
  // switch off luxes sensor
  //luxes.ON();
 // luminosity = luxes.getLuminosity();
 // luxes.OFF();
 
  ultrasound.ON();
  // switch on all gas sensor again
  sensor_ch4.ON();
  sensor_h2s.ON();
  sensor_nh3.ON();


  ///////////////////////////////////////////
  // 2. Wait heating time
  ///////////////////////////////////////////

  // Sensors need time to warm up and get a response from gas
  // To reduce the battery consumption, use deepSleep instead delay
  // After 2 minutes, Waspmote wakes up thanks to the RTC Alarm
  USB.println(RTC.getTime());
  USB.println(F("Enter deep sleep mode to wait for sensors heating time..."));
  PWR.deepSleep("00:00:02:00", RTC_OFFSET, RTC_ALM1_MODE1, ALL_ON);
  USB.ON();
  USB.println(RTC.getTime());
  USB.println(F("wake up!!\r\n"));


  ///////////////////////////////////////////
  // 3. Read gas sensors
  ///////////////////////////////////////////

  // Read the sensors and compensate with the temperature internally
  concentration_ch4 = sensor_ch4.getConc(temperature);
  concentration_h2s = sensor_h2s.getConc(temperature);
  concentration_nh3 = sensor_nh3.getConc(temperature);

  // Read the ultrasound sensor
  range = ultrasound.getDistance();
  // switch off CO2 sensor
  // Pellistor and NDIR sensors must be switched off after
  // reading because they present a high power consumption
  //sensor_co2.OFF();

 // switch off all gas sensor again
  sensor_ch4.OFF();
  sensor_h2s.OFF();
  sensor_nh3.OFF();
 ///////////////////////////////////////////
  ultrasound.OFF();
  
  ///////////////////////////////////////////
  // 4. Read particle matter sensor
  ///////////////////////////////////////////
/*
  // Turn on the particle matter sensor
  OPC_status = PM.ON();
  if (OPC_status == 1)
  {
    USB.println(F("Particle sensor started"));
  }
  else
  {
    USB.println(F("Error starting the particle sensor"));
  }

  // Get measurement from the particle matter sensor
  if (OPC_status == 1)
  {
    // Power the fan and the laser and perform a measure of 5 seconds
    OPC_measure = PM.getPM(5000, 5000);
  }

  PM.OFF();
*/

  VOLT = PWR.getBatteryVolts();
  CHRG = PWR.getBatteryCurrent();
  ///////////////////////////////////////////
  // 5. Print sensor values
  ///////////////////////////////////////////

  USB.println(F("***********************************************"));
  USB.print(F("SOCKET_E -- > Temperature : "));
  USB.printFloat(temperature, 2);
  USB.println(F(" Celsius degrees"));
  USB.print(F("SOCKET_E -- > Humidity : "));
  USB.printFloat(humidity, 2);
  USB.println(F(" % "));
  USB.print(F("SOCKET_E -- > Pressure : "));
  USB.printFloat(pressure, 2);
  USB.println(F(" Pa"));
  USB.print(F("SOCKET_B -- > CH4 concentration : "));
  USB.printFloat(concentration_ch4, 3);
  USB.println(F(" ppm"));
  USB.print(F("SOCKET_C -- > H2S concentration : "));
  USB.printFloat(concentration_h2s, 3);
  USB.println(F(" ppm"));
  USB.print(F("SOCKET_F -- > NH3 concentration : "));
  USB.printFloat(concentration_nh3, 3);
  USB.println(F(" ppm"));
  USB.print(F("SOCKET_A -- > Distance : "));
  USB.print(range);
  USB.println(F(" cm"));
  // Show the remaining battery level
  USB.print(F("Battery Level: "));
  USB.print(PWR.getBatteryLevel(),DEC);
  USB.println(F(" %"));
  // Show the battery Volts
  USB.print(F("Battery (Volts): "));
  USB.print(PWR.getBatteryVolts());
  USB.println(F(" V"));
  // Show the battery charging current (only from solar panel)
  USB.print(F("Battery charging current (only from solar panel): "));
  USB.print(CHRG, DEC);
  USB.println(F(" mA"));
  USB.println(F("***********************************************"));

/*
  // check answer
  if (OPC_measure == 1)
  {
    USB.print(F("SOCKET_D -- > PM 1 : "));
    USB.printFloat(PM._PM1, 3);
    USB.println(F(" ug / m3"));
    USB.print(F("SOCKET_D -- > PM 2.5 : "));
    USB.printFloat(PM._PM2_5, 3);
    USB.println(F(" ug / m3"));
    USB.print(F("SOCKET_D -- > PM 10 : "));
    USB.printFloat(PM._PM10, 3);
    USB.println(F(" ug / m3"));
  }
  else
  {
    USB.print(F("SOCKET_D -- > Error performing the measure. Error code : "));
    USB.println(OPC_measure, DEC);
  }
*/
  //USB.print(F("SOCKET_E -- > Luminosity : "));
 // USB.print(luminosity);
  //USB.println(F(" luxes"));
    



  ///////////////////////////////////////////
  // 6. Create ASCII frame
  ///////////////////////////////////////////

  // Create new frame (ASCII)
  frame.createFrame(ASCII);

  // Add sensor values
  frame.addSensor(SENSOR_CITIES_PRO_TC, temperature);
  frame.addSensor(SENSOR_CITIES_PRO_HUM, humidity);
  frame.addSensor(SENSOR_CITIES_PRO_PRES, pressure);
  frame.addSensor(SENSOR_CITIES_PRO_CH4, concentration_ch4);
  frame.addSensor(SENSOR_CITIES_PRO_H2S, concentration_h2s);
  frame.addSensor(SENSOR_CITIES_PRO_NH3, concentration_nh3);
  frame.addSensor(SENSOR_CITIES_PRO_US, range);
 // frame.addSensor(SENSOR_CITIES_PRO_LUXES, luminosity);
  //frame.addSensor(SENSOR_CITIES_PRO_PM1, PM._PM1);
 // frame.addSensor(SENSOR_CITIES_PRO_PM2_5, PM._PM2_5);
 // frame.addSensor(SENSOR_CITIES_PRO_PM10, PM._PM10);
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());
 //frame.addSensor(SENSOR_RSSI, _4G.getRSSI());
  frame.addSensor(SENSOR_BAT_VOLT, VOLT);
  frame.addSensor(SENSOR_BAT_CURR, CHRG);
  
  // Show the frame
  frame.showFrame();

  //////////////////////////////////////////////////
  // 1. Switch ON
  //////////////////////////////////////////////////
  error = _4G.ON();

  if (error == 0)
  {
    USB.println(F("1. 4G module ready..."));

    connection_status = _4G.checkDataConnection(30);
    
    if (connection_status == 0)
    {
      USB.println(F("1.1. Module connected to network"));

      // delay for network parameters stabilization
      delay(5000);

      //////////////////////////////////////////////
      // 1.2. Get RSSI
      //////////////////////////////////////////////
      error = _4G.getRSSI();
      if (error == 0)
      {
        USB.print(F("1.2. RSSI: "));
        USB.print(_4G._rssi, DEC);
        USB.println(F(" dBm"));
      }
      else
      {
        USB.println(F("1.2. Error calling 'getRSSI' function"));
      }
    }
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
      //frame.createFrame(ASCII);
      //frame.addSensor(SENSOR_BAT_VOLT, VOLT);
     // frame.addSensor(SENSOR_BAT_CURR, CHRG); 
     // _4G.sendFrameToMeshlium( host, port, frame.buffer, frame.length);      
     // USB.println(F("ASCII FRAME SEND OK")); 
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

  ///////////////////////////////////////////
  // 7. Sleep
  ///////////////////////////////////////////

  // Go to deepsleep
  // After 30 seconds, Waspmote wakes up thanks to the RTC Alarm
  USB.println(F("Enter deep sleep mode"));
  PWR.deepSleep("00:00:15:10", RTC_OFFSET, RTC_ALM1_MODE1, ALL_ON);
  USB.ON();
  USB.println(F("wake up!!"));

}