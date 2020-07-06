/*  
 *  --------------- 3G_15b - Sending a frame to Meshlium  --------------- 
 *  
 *  Explanation: This example shows how to send frame to Meshlium
 *  
 *  Copyright (C) 2016 Libelium Comunicaciones Distribuidas S.L. 
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
 *  Version:           1.4
 *  Design:            David Gascón 
 *  Implementation:    Alejandro Gállego
 */

#include <WaspFrame.h>
#include "Wasp3G.h"
#include <WaspSensorGas_Pro.h>


char url[] = "82.78.81.171";
int port = 80;

char apn[] = "live.vodafone.com";
char login[] = "live";
char password[] = "vodafone";

int8_t answer;
char moteID[] = "SEP2";

char GPS_data[250];
int GPS_status = 0;
int GPS_fix = 0;
int GPRS_status = 0;

//Gas CO2(SOCKET_B);
Gas H2(SOCKET_3);
Gas CO(SOCKET_4);
//Gas O2(SOCKET_4);
//Gas NO(SOCKET_5);
Gas NO2(SOCKET_6);

float temperature; 
float humidity; 
float pressure;
//float concCO2;
float concCO;
float concH2;
//float concO2;
//float concNO;
float concNO2;

void setup()
{    
    USB.println(F("USB port started..."));

    USB.println(F("---******************************************************************************---"));
    USB.println(F("GET method to the libelium's test url with frame..."));
    USB.println(F("You can use this php to test the HTTP connection of the module."));
    USB.println(F("The php returns the parameters that the user sends with the URL."));
    USB.println(F("In this case the RTC time and the battery level."));
    USB.println(F("---******************************************************************************---"));

    // 1. sets operator parameters
    _3G.set_APN(apn, login, password);
    // And shows them
    _3G.show_APN();
    USB.println(F("---******************************************************************************---"));
   // Set the Waspmote ID
    frame.setID(moteID);
    // 2. activates the 3G+GPS module:
    answer = _3G.ON();
    if ((answer == 1) || (answer == -3))
    {

        USB.println(F("3G+GPS module ready..."));

        // 3. starts the GPS:
        USB.println(F("Starting in stand-alone mode")); 
        GPS_status = _3G.startGPS();
        if (GPS_status == 1)
        { 
            USB.println(F("GPS started"));
        }
        else
        {
            USB.println(F("GPS NOT started"));   
        }

    }
    else
    {
        // Problem with the communication with the 3G+GPS module
        USB.println(F("3G+GPS module not started")); 
    }
    
}

void loop()
{

    // 2. activates the 3G module:
    answer = _3G.ON();
    if ((answer == 1) || (answer == -3))
    {
        USB.println(F("3G + GPS module ready..."));
/*
        // 3. set pin code:
        USB.println(F("Setting PIN code..."));
        // **** must be substituted by the SIM code
        if (_3G.setPIN("****") == 1) 
        {
            USB.println(F("PIN code accepted"));
        }
        else
        {
            USB.println(F("PIN code incorrect"));
        }
*/
        // 4. wait for connection to the network:
        answer = _3G.check(180);    
        if (answer == 1)
        { 
            USB.println(F("3G module connected to the network..."));     
            USB.println(F("Power on sensors..."));     
            // CO2.ON();
            CO.ON();
            H2.ON();
            // O2.ON();
            // NO.ON();
            NO2.ON();
            // Sensors need time to warm up and get a response from gas
            // To reduce the battery consumption, use deepSleep instead delay
            // After 2 minutes, Waspmote wakes up thanks to the RTC Alarm  
            USB.println(F("Deep sleep 2 minutes to heat time sensors ..."));     

          // PWR.deepSleep("00:00:02:00", RTC_OFFSET, RTC_ALM1_MODE1, ALL_ON);
            USB.println(F("Power on, reading sensors ..."));     
            
            // Read the CO sensor and compensate with the temperature internally
            concCO = CO.getConc();
            // Read the O3 sensor and compensate with the temperature internally
            concH2 = H2.getConc();
            // Read the O2 sensor and compensate with the temperature internally
            //  concO2 = O2.getConc();
            // Read the NO sensor and compensate with the temperature internally
            // concNO = NO.getConc();
            // Read the NO2 sensor and compensate with the temperature internally
            concNO2 = NO2.getConc();
  
            // Read enviromental variables
            temperature = CO.getTemp();
            humidity = CO.getHumidity();
            pressure = CO.getPressure();
            
            //Power off sensors
            //CO2.OFF();
            CO.OFF();
            H2.OFF();
            //O2.OFF();
            // NO.OFF();
            NO2.OFF();
            
            memset(GPS_data, '\0', sizeof(GPS_data));
    // 5. checks the status of the GPS
    if ((GPS_status == 1) && (_3G.getGPSinfo() == 1))
    {
        GPS_fix = 1;
        Utils.setLED(LED0, LED_ON);
        // when it's available, shows it
        USB.print(F("Latitude (degrees):"));
        USB.println(_3G.convert2Degrees(_3G.latitude));
        USB.print(F("Longitude (degrees):"));
        USB.println(_3G.convert2Degrees(_3G.longitude));
        USB.print(F("Speed over the ground"));
        USB.println(_3G.speedOG);
        USB.print(F("Course over the ground:"));
        USB.println(_3G.course);
        USB.print(F("altitude (m):"));
        USB.println(_3G.altitude);

       /* // 6a. add GPS data 
        // add GPS position field
        snprintf(GPS_data, sizeof(GPS_data), "GET /demo_sim908.php?%svisor=false&latitude=%s&longitude=%s&altitude=%s&time=20%c%c%c%c%c%c%s&satellites=7&speedOTG=%s&course=%s HTTP/1.1\r\nHost: %s\r\nContent-Length: 0\r\n\r\n",
            url,
            _3G.latitude,
            _3G.longitude,
            _3G.altitude,
            _3G.date[4], _3G.date[5], _3G.date[2], _3G.date[3], _3G.date[0], _3G.date[1], _3G.UTC_time,
            _3G.speedOG,
            _3G.course,
            url);

        USB.print(F("Data string: "));  
        */
        USB.println(GPS_data);  
    }
     else if((GPS_status == 1) && (_3G.getGPSinfo() != 1))
    {
        GPS_fix = 0;
        Utils.setLED(LED0, LED_OFF);
        // 6c. add not GPS fixed string
        USB.println(F("GPS not fixed")); 		
    }
    else
    {
        GPS_fix = 0;
        Utils.setLED(LED0, LED_OFF);
        // 6d. add not GPS started string
        USB.println(F("GPS not started. Restarting"));
        GPS_status = _3G.startGPS();
    }
    
            // switch on RTC
            RTC.ON();
            // Create new frame (ASCII)
            frame.createFrame(BINARY); 
            // set frame fields (Time from RTC)
            frame.addSensor(SENSOR_TIME, RTC.getTime());
              // set frame fields (Battery sensor - uint8_t)
            frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());
            // Add temperature
            frame.addSensor(SENSOR_GP_TC, temperature);
            // Add humidity
            frame.addSensor(SENSOR_GP_HUM, humidity);
            // Add pressure value
            frame.addSensor(SENSOR_GP_PRES, pressure);
            // Add CO2 value
             //frame.addSensor(SENSOR_GP_CO2, concCO2);
            // Add CO value
            frame.addSensor(SENSOR_GP_CO, concCO);
            // Add O3 value
            frame.addSensor(SENSOR_GP_H2, concH2);
            // Add O2 value
            // frame.addSensor(SENSOR_GP_O2, concO2);
            // Add NO value
            // frame.addSensor(SENSOR_GP_NO, concNO);
            // Add NO2 value
             frame.addSensor(SENSOR_GP_NO2, concNO2);
             
             // 7a. add GPS data 
            // add GPS position field
            frame.addSensor(SENSOR_GPS, _3G.convert2Degrees(_3G.latitude), _3G.convert2Degrees(_3G.longitude));
           // add GPS speed over the ground field
            frame.addSensor(SENSOR_SPEED, _3G.speedOG); 
            // add GPS course over the ground field
            frame.addSensor(SENSOR_COURSE, _3G.course); 
            // add GPS altitude field
            frame.addSensor(SENSOR_ALTITUDE, _3G.altitude); 
             
            // show Frame
            frame.showFrame();            
            // switch off RTC
            RTC.OFF();            

            // 5. Sends the frame to Meshlium with GET
            USB.print(F("Sending the frame to Meshlium with GET method..."));

            answer = _3G.sendHTTPframe( url, port, frame.buffer, frame.length, GET);

            // Checks the answer
            if ( answer == 1)
            {
                USB.println(F("Done"));  
                USB.println(_3G.buffer_3G);
            }
            else if (answer < -14)
            {
                USB.print(F("Failed. Error code: "));
                USB.println(answer, DEC);
                USB.print(F("CME error code: "));
                USB.println(_3G.CME_CMS_code, DEC);
            }
            else 
            {
                USB.print(F("Failed. Error code: "));
                USB.println(answer, DEC);
            } 
        }
        else
        {
            USB.println(F("3G module cannot connect to the network..."));
        }  
    }
    else
    {
        // Problem with the communication with the 3G module
        USB.println(F("3G module not started"));
    }

    // 7. powers off the 3G module
    _3G.OFF(); 

    USB.println(F("Sleeping..."));

    // 8. sleeps one hour
    PWR.deepSleep("00:00:00:10", RTC_OFFSET, RTC_ALM1_MODE1, ALL_OFF);

}