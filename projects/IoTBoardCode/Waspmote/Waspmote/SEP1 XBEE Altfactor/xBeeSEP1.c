#include <WaspXBee802.h>
#include <WaspFrame.h>
#include <WaspSensorGas_Pro.h>
#include <WaspOPC_N2.h>

// PAN (Personal Area Network) Identifier
uint8_t  panID[2] = {0x20,0x07};  

// Define Freq Channel to be set: 
// Center Frequency = 2.405 + (CH - 11d) * 5 MHz
//   Range: 0x0B - 0x1A (XBee)
//   Range: 0x0C - 0x17 (XBee-PRO)
uint8_t  channel = 0x0C;

// Define the Encryption mode: 1 (enabled) or 0 (disabled)
uint8_t encryptionMode = 1;

// Define the AES 16-byte Encryption Key
char  encryptionKey[] = "libelium2015MVXB"; 

// Destination MAC address
//////////////////////////////////////////
char RX_ADDRESS[] = "0013A20040D878D1";
//////////////////////////////////////////

// Define the Waspmote ID
char NODE_ID[] = "SEP1";


// define variable
uint8_t error;

// messure every 10 minutes
unsigned long delay_interval = 900000;

//Gas CO2(SOCKET_B);
Gas CO(SOCKET_F);
Gas O3(SOCKET_A);
//Gas O2(SOCKET_4);
//Gas NO(SOCKET_5);
Gas NO2(SOCKET_B);

float temperature; 
float humidity; 
float pressure;
//float concCO2;
float concCO;
float concO3;
//float concO2;
//float concNO;
float concNO2;

char info_string[61];
int status;
int measure;




void setup()
{
  // open USB port
  USB.ON();

  USB.println(F("-------------------------------"));
  USB.println(F("Configure XBee 802.15.4"));
  USB.println(F("-------------------------------"));

  // init XBee 
  xbee802.ON();

  delay(1000);


  /////////////////////////////////////
  // 1. set channel 
  /////////////////////////////////////
  xbee802.setChannel( channel );

  // check at commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.print(F("1. Channel set OK to: 0x"));
    USB.printHex( xbee802.channel );
    USB.println();
  }
  else 
  {
    USB.println(F("1. Error calling 'setChannel()'"));
  }


  /////////////////////////////////////
  // 2. set PANID
  /////////////////////////////////////
  xbee802.setPAN( panID );

  // check the AT commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.print(F("2. PAN ID set OK to: 0x"));
    USB.printHex( xbee802.PAN_ID[0] ); 
    USB.printHex( xbee802.PAN_ID[1] ); 
    USB.println();
  }
  else 
  {
    USB.println(F("2. Error calling 'setPAN()'"));  
  }

  /////////////////////////////////////
  // 3. set encryption mode (1:enable; 0:disable)
  /////////////////////////////////////
  xbee802.setEncryptionMode( encryptionMode );

  // check the AT commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.print(F("3. AES encryption configured (1:enabled; 0:disabled):"));
    USB.println( xbee802.encryptMode, DEC );
  }
  else 
  {
    USB.println(F("3. Error calling 'setEncryptionMode()'"));
  }

  /////////////////////////////////////
  // 4. set encryption key
  /////////////////////////////////////
  xbee802.setLinkKey( encryptionKey );

  // check the AT commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.println(F("4. AES encryption key set OK"));
  }
  else 
  {
    USB.println(F("4. Error calling 'setLinkKey()'")); 
  }

  /////////////////////////////////////
  // 5. write values to XBee module memory
  /////////////////////////////////////
  xbee802.writeValues();

  // check the AT commmand execution flag
  if( xbee802.error_AT == 0 ) 
  {
    USB.println(F("5. Changes stored OK"));
  }
  else 
  {
    USB.println(F("5. Error calling 'writeValues()'"));   
  }
  
  
  USB.println(F("-------------------------------")); 


//pm
USB.ON();
  USB.println(F("Frame Utility Example for Gases Pro Sensor Board"));

  // Set the Waspmote ID
   USB.println(F("Particle Matter Sensor example"));
    // Set the Waspmote ID
    frame.setID(NODE_ID);

    status = OPC_N2.ON();
    if (status == 1)
    {
        status = OPC_N2.getInfoString(info_string);
        if (status == 1)
        {
            USB.println(F("Information string extracted:"));
            USB.println(info_string);

        }
        else
        {
            USB.println(F("Error reading the particle sensor"));
        }

        OPC_N2.OFF();
    }
    else
    {
        USB.println(F("Error starting the particle sensor"));
    }

}



void loop()
{
  
  ///////////////////////////////////////////
  // 1. Turn on sensors and wait
  ///////////////////////////////////////////
 // Power on the OPC_N2 sensor. 
    // If the gases PRO board is off, turn it on automatically.
    status = OPC_N2.ON();
    if (status == 1)
    {
        USB.println(F("Particle sensor started"));

    }
    else
    {
        USB.println(F("Error starting the particle sensor"));
    }

    ///////////////////////////////////////////
    // 2. Read sensors
    ///////////////////////////////////////////  

    if (status == 1)
    {
        // Power the fan and the laser and perform a measure of 5 seconds
        measure = OPC_N2.getPM(10000);
        if (measure == 1)
        {
            USB.println(F("Measure performed"));
            USB.print(F("PM 1: "));
            USB.print(OPC_N2._PM1);
            USB.println(F(" ug/m3"));
            USB.print(F("PM 2.5: "));
            USB.print(OPC_N2._PM2_5);
            USB.println(F(" ug/m3"));
            USB.print(F("PM 10: "));
            USB.print(OPC_N2._PM10);
            USB.println(F(" ug/m3"));

        }
        else
        {
            USB.print(F("Error performing the measure. Error code:"));
            USB.println(measure, DEC);
        }
    }
    
OPC_N2.OFF();

    delay(15000);
    ///////////////////////////////////////////
    // 3. Turn off the sensors
    /////////////////////////////////////////// 

    // Power off the OPC_N2 sensor. If there aren't other sensors powered, 
    // turn off the board automatically
  //Power on sensors
 // CO2.ON();
 CO.ON();
 O3.ON();
 // O2.ON();
 // NO.ON();
 NO2.ON();
  
  // Sensors need time to warm up and get a response from gas
  // To reduce the battery consumption, use deepSleep instead delay
  // After 2 minutes, Waspmote wakes up thanks to the RTC Alarm  
  PWR.deepSleep("00:00:02:00", RTC_OFFSET, RTC_ALM1_MODE1, ALL_ON);


  ///////////////////////////////////////////
  // 2. Read sensors
  ///////////////////////////////////////////  
  
  // Read the CO2 sensor and compensate with the temperature internally
 // concCO2 = CO2.getConc();
  // Read the CO sensor and compensate with the temperature internally
  concCO = CO.getConc();
  // Read the O3 sensor and compensate with the temperature internally
  concO3 = O3.getConc();
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

  ///////////////////////////////////////////
  // 3. Turn off the sensors
  /////////////////////////////////////////// 

  //Power off sensors
  //CO2.OFF();
  CO.OFF();
O3.OFF();
 // O2.OFF();
 // NO.OFF();
NO2.OFF();

  ///////////////////////////////////////////
  // 1. Create ASCII frame
  ///////////////////////////////////////////  

  // create new frame
  frame.createFrame(BINARY, NODE_ID);  
  

  // add frame fields
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel()); 
  // Add temperature
  frame.addSensor(SENSOR_GP_TC, temperature);
  // Add humidity
  frame.addSensor(SENSOR_GP_HUM, humidity);
  // Add pressure value
  frame.addSensor(SENSOR_GP_PRES, pressure);
  // Add CO2 value
//  frame.addSensor(SENSOR_GP_CO2, concCO2);
  // Add CO value
  frame.addSensor(SENSOR_GP_CO, concCO);
  // Add O3 value
frame.addSensor(SENSOR_GP_O3, concO3);
  // Add O2 value
 // frame.addSensor(SENSOR_GP_O2, concO2);
  // Add NO value
 // frame.addSensor(SENSOR_GP_NO, concNO);
  // Add NO2 value
frame.addSensor(SENSOR_GP_NO2, concNO2);
// Add PM 1
    frame.addSensor(SENSOR_OPC_PM1,OPC_N2._PM1); 
    // Add PM 2.5
    frame.addSensor(SENSOR_OPC_PM2_5,OPC_N2._PM2_5); 
    // Add PM 10
    frame.addSensor(SENSOR_OPC_PM10,OPC_N2._PM10); 
    // Show the frame
  frame.showFrame();

  ///////////////////////////////////////////
  // 2. Send packet
  ///////////////////////////////////////////  

  USB.println(F("sending..."));
  // send XBee packet
  error = xbee802.send( RX_ADDRESS, frame.buffer, frame.length );   
  
  // check TX flag
  if( error == 0 )
  {
    USB.println(F("send ok"));
    
    // blink green LED
    Utils.blinkGreenLED();
    
  }
  else 
  {
    USB.println(F("send error"));
    
    // blink red LED
    Utils.blinkRedLED();
  }

  USB.println(F("-------------------------------"));
  USB.print(F("Going to sleep for "));
  USB.print(delay_interval / 1000);
  USB.println(F(" secounds."));
  USB.println(F("-------------------------------"));

  // sleep
  delay(delay_interval);
}