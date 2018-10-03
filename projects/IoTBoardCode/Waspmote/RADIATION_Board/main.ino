#include <WaspXBee802.h>
#include <WaspFrame.h>
#include <WaspSensorRadiation.h>


// PAN (Personal Area Network) Identifier
uint8_t  panID[2] = {0xbe,0x1a}; 

// Define Freq Channel to be set: 
// Center Frequency = 2.405 + (CH - 11d) * 5 MHz
//   Range: 0x0B - 0x1A (XBee)
//   Range: 0x0C - 0x17 (XBee-PRO)
uint8_t  channel = 0x17;

// Define the Encryption mode: 1 (enabled) or 0 (disabled)
uint8_t encryptionMode = 0;

// Define the AES 16-byte Encryption Key
char  encryptionKey[] = "Beia123456789012"; 

// Destination MAC address
//////////////////////////////////////////
char RX_ADDRESS[] = "0013A20041678B8C";
//////////////////////////////////////////

// Define the Waspmote ID
char WASPMOTE_ID[] = "RADIATION_Board";


// define variable
uint8_t error;

// messure every 10 minutes
unsigned long delay_interval = 600000;


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

  RadiationBoard.ON();
}



void loop()
{
  ///////////////////////////////////////////
  // 1. Create ASCII frame
  ///////////////////////////////////////////  

  // create new frame
  frame.createFrame(ASCII, WASPMOTE_ID);  

  USB.println(F("Measuring radiation"));
  float radiation = RadiationBoard.getRadiation();

  USB.print(F("radiation[uSv/h]: "));
  USB.println(radiation);
  USB.println();

  
  
  // add frame fields
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel()); 
  frame.addSensor(SENSOR_RADIATION, radiation);
  

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
