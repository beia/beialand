// Put your libraries here (#include ...)
#include <WaspWIFI_PRO.h> 
#include <WaspFrame.h>
#include <WaspSensorGas_v30.h>

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
  

// CO Sensor must be connected physically in SOCKET_4
CO2SensorClass CO2Sensor(SOCKET_2);

// VOC Sensor must be connected in SOCKET_5
VOCSensorClass VOCSensor(SOCKET_5);

// O2 Sensor must be connected in SOCKET_1
//APSensorClass APPSensor(SOCKET_7);



// Concentratios used in calibration process
#define POINT1_PPM_VOC 100.0   //  <--- Ro value at this concentration
#define POINT2_PPM_VOC 300.0
#define POINT3_PPM_VOC 1000.0
// Calibration resistances obtained during calibration process
#define POINT1_RES_VOC 230.30 // <-- Ro Resistance at 100 ppm. Necessary value.
#define POINT2_RES_VOC 40.665 // 
#define POINT3_RES_VOC 20.300 // 

// Concentratios used in calibration process (PPM Values)
#define POINT1_PPM_CO2 350.0  //   <-- Normal concentration in air
#define POINT2_PPM_CO2 1000.0
#define POINT3_PPM_CO2 3000.0

// Calibration vVoltages obtained during calibration process (Volts)
#define POINT1_VOLT_CO2 0.300
#define POINT2_VOLT_CO2 0.350
#define POINT3_VOLT_CO2 0.380


  
// Define the number of calibration points
#define numPoints 3

float temperature; // Stores the temperature in ºC
float humidity;     // Stores the realitve humidity in %RH
float pressure;    // Stores the pressure in Pa

float VOCconcentrations[] = {POINT1_PPM_VOC, POINT2_PPM_VOC, POINT3_PPM_VOC};
float VOCresValues[] =      {POINT1_RES_VOC, POINT2_RES_VOC, POINT3_RES_VOC};

float concentrations[] = { POINT1_PPM_CO2, POINT2_PPM_CO2, POINT3_PPM_CO2 };
float voltages[] =       { POINT1_VOLT_CO2, POINT2_VOLT_CO2, POINT3_VOLT_CO2 };

  
// define the Waspmote ID 
char node_ID[] = "GAS_WiFi4";

 // 2. Read sensors
 //////////////////////////////////////////
// Turn on the sensor board
void setup()
{
  USB.println(F("Start program"));

CO2Sensor.setCalibrationPoints(voltages, concentrations, numPoints);
// Calculate the slope and the intersection of the logarithmic function
VOCSensor.setCalibrationPoints(VOCresValues, VOCconcentrations, numPoints);

//turn on gas and sensors
  Gases.ON();
  delay(100);   
  CO2Sensor.ON();
  VOCSensor.ON();
  //APPSensor.ON();
}
// define the Waspmote ID 
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
  

  
  // Read the concentration value
  temperature = Gases.getTemperature();
  humidity = Gases.getHumidity();
  pressure = Gases.getPressure();
 
  float CO2Vol = CO2Sensor.readVoltage();
  float CO2PPM = CO2Sensor.readConcentration();
  float vocVol = VOCSensor.readVoltage();
  float vocRes = VOCSensor.readResistance(vocVol);    // Resistance of the senso
  float vocPPM = VOCSensor.readConcentration(vocRes); // PPM value of VOC
 // float APPVol = APPSensor.readVoltage();         // Voltage value of the sensor
 // float APPRes = APPSensor.readResistance();      // Resistance of the sensor
  //float APP_PPM = APPSensor.readConcentration();  // PPM value of AP1
  
  // Print of the results
  USB.print(F(" Temperature: "));
  USB.print(temperature);
  USB.println(F(" Celsius Degrees |"));

  USB.print(F(" Humidity : "));
  USB.print(humidity);
  USB.println(F(" %RH"));

  USB.print(F(" Pressure : "));
  USB.print(pressure);
  USB.println(F(" Pa"));

  USB.print(F("CO2 Sensor Voltage: "));
  USB.print(CO2Vol);
  USB.print(F("volts |"));

  USB.print(F(" VOC Sensor Voltage: "));
  USB.print(vocVol);
  USB.println(F(" V |"));

  //USB.print(F("Air Pollutans Sensor Voltage: "));
 // USB.print(APPVol);
 // USB.print(F(" V |"));

  USB.print(F(" VOC Sensor Resistance: "));
  USB.print(vocRes);
  USB.println(F(" Ohms |"));

  // Print of the results
 // USB.print(F(" Air Pollutans Sensor Resistance: "));
 // USB.print(APPRes);
 // USB.print(F(" Ohms |"));

  USB.print(F(" CO2 concentration estimated: "));
  USB.print(CO2PPM);
  USB.println(F(" ppm"));


  USB.print(F(" VOC concentration Estimated: "));
  USB.print(vocPPM);
  USB.println(F(" ppm")); 

 // USB.print(F(" Air Pollutans concentration Estimated: "));
 // USB.print(APP_PPM);
 // USB.println(F(" ppm"));

  USB.println();

  ///////////////////////////////////////////
  // 3. Create BINARY frame
  ///////////////////////////////////////////
  //frame.createFrame(ASCII, node_ID);
  frame.createFrame(BINARY, node_ID); 

  frame.addSensor(SENSOR_GASES_TC, Gases.getTemperature());

  frame.addSensor(SENSOR_GASES_HUM, Gases.getHumidity());

  frame.addSensor(SENSOR_GASES_PRES, Gases.getPressure());

  frame.addSensor(SENSOR_GASES_CO2, CO2PPM);
 
  frame.addSensor(SENSOR_GASES_VOC, VOCSensor.readConcentration(vocRes));

 // frame.addSensor(SENSOR_GASES_AP1, APP_PPM);
  
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());

    ///////////////////////////////
    // 3.1. Create a new Frame 
    ///////////////////////////////
    // print frame
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
  delay(30000);
}
