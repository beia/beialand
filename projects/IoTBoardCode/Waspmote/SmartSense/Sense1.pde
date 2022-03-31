/*

                    | A | B | C | D | E | F |
                    |-----------------------|
  BME280            |   |   |   |   | X |   |
  CO                |   |   | X |   |   |   |
  SO2               |   | X |   |   |   |   |
  O3                | X |   |   |   |   |   |
  NO2               |   |   |   |   |   | X |
  PM                |   |   |   | X |   |   |
                    |-----------------------|


*/
#include <Wasp4G.h>
#include <WaspFrame.h>
#include <WaspLoRaWAN.h>
#include <WaspSensorGas_Pro.h>
#include <WaspPM.h>

//#include "INIRCH4.h"

// choose socket (SELECT USER'S SOCKET)
///////////////////////////////////////
const uint8_t lora_socket = SOCKET0;
///////////////////////////////////////

//====================================================================
// INSTANCE DEFINITION
//====================================================================
bmeGasesSensor bme;
Gas gas_O3(SOCKET_A);
Gas gas_SO2(SOCKET_B);
Gas gas_CO(SOCKET_C);
Gas gas_NO2(SOCKET_F);

//INIRCH4 CH4 = INIRCH4();

//====================================================================
// PARAMETERS TO CONFIGURE LORAWAN RADIO
//===================================================================
// Device EUI for
char DEVICE_EUI[] = "0004A30B00EF2997";
// Default Application kEY
char APP_KEY[] = "C79671FEC0500D5713C7A9887CC43889";  
// Default Application Eui
char APP_EUI[] = "0004A30B00EF2997";
/*
          SERIAL ID         DEV EUI=APP EUI   APP KEY
Sense1   	48151CE819623C3F	0004A30B00EF2997	C79671FEC0500D5713C7A9887CC43889
Sense 10	5C0A1CE819623C97	0004A30B00EEDBEA	E7688BD6F5E3F51B3E896F164141AE3B
Sense 11	6424BAC2CB018069	0004A30B00EF5BD0	348019C95BE9EBA0B4B352FF8ED39730
Sense 12	29331CE819623CC9	0004A30B00EF0CC9	2FBE14E6202DD27CDE42EEF9183D3353
Sense 3	  191644E819623CEE	0004A30B00EEA8EB	295A46A6A1732A5E0F56E2FAFF535A7B
Sense 14	325044E819623C43	0004A30B00EF4462	958C164C0C98B89EB5190C735FC60436
Sense 13	0A5E1CE819623C81	0004A30B00EF450F	D09922CFF6F832D0E11B377B1ED05073
Sense 15	245B1CE819623C8B	0004A30B00EEF090	C787B5C828F19651F3111AC9382B82BA
Sense 4	  6B5E1CE819623CBA	0004A30B00EED058	E0F0961ED3392F701B91C0BACADA3592
Sense 5 	4A7C1CE819623CAD	0004A30B00EF0E15	11E8770BA537AF5E8F3CD11CEB090544
Sense2  	2B4D1CE819623C1A	0004A30B00EEA4F3	CF14118DB3977592419A809272B4B898
Sense16  	3F6EBAC2CB018018	0004A30B00EF30A0	F9EDE84742753A02E3B71F219303B391
Sense 6	 7D511CE819623C35 	0004A30B00EF0EB5	CF5EB8BAEFA890C8FACBF734C102A88E
Sense 7	 106A1CE819623C00	  0004A30B00EEF6F0	96E5FC154607403C433BCF1085E8F62B
Sense 8	 54281CE819623C14	  0004A30B00EEF51C 	43157B30A446186FB32D978991A8CB78
Sense 9	 25281CE819623CFC	  0004A30B00EEDABA	5EAE95B0C5A341DCA78A28D4F805AA26
*/
// Default port
uint8_t PORT = 3;
// Default device name
char *MOTE_ID = "SENSE1";

//====================================================================
// PARAMETERS FOR SD CARD
//===================================================================

char SD_FILENAME[] = "IOTDATA.TXT";

//====================================================================
// PARAMETERS TO CONFIGURE 4G RADIO
//===================================================================
char apn[] = "broadband";
char login[] = "";
char password[] = "";
char PIN[] = "";

// SERVER settings
///////////////////////////////////////
char host[] = "82.78.81.178";
uint16_t port = 80;
///////////////////////////////////////

// battery control variables
uint8_t battery;
// other variables
uint8_t error;
uint8_t error_flag;
int8_t cont;
uint8_t connection_status;

/////////////////////////////////////////////////
// Define measurement variables
////////////////////////////////////////////////
float concentration_O3, concentration_SO2, concentration_NO2, concentration_CO; // Stores the concentration level in ppm
float temperature;  // Stores the temperature in ºC
float humidity;   // Stores the realitve humidity in %RH
float pressure;   // Stores the pressure in Pa
int status;

/////////////////////////////////////////////////
// Global flags (do not change until you know what are you doing
////////////////////////////////////////////////
bool NTP_IS_SYNC = false;
bool LW_IS_SET = false;

/*
 * Writes the frame to the SD card usind the following format:
 * When RTC is okay (set)
 * +\t<milis>\t<epoch_time>\t<frame>
 * When RTC is not set
 * -\t<milis>\t*\t<frame>
 * Uses NTP_IS_SYNC to see if the RTC is set.
 * Returns true if the write is succesful.
 */
bool writeSD(void) {
  bool ok = true;
  USB.println(F("--------------- Start of writeSD ------------------------"));
  uint8_t sd_status = 0;
  char epoch_time_str[16];
  char millis_str[16];

  // Start SD
  SD.ON();

  // Open file
  SdFile file;
  sd_status = SD.openFile(SD_FILENAME, &file, O_APPEND | O_CREAT | O_RDWR);
  if (sd_status == 1) {
    USB.println(F("Succesfully oppened file"));
  } else {
    USB.println(F("Failed to open file."));
    return false;
  }

  // Add newline
  ok &= file.write("\n") > 0;

  if (NTP_IS_SYNC == true) {
    USB.println(F("NTP is set - I will write the real time on the SD card."));
    ok &= file.write("+\t") > 0;
  } else {
    USB.println(F("No NTP was synced. No time available :("));
    ok &= file.write("-\t") > 0;
  }

  ltoa(millis(), millis_str, 10);
  ok &= file.write(millis_str) > 0;
  ok &= file.write("\t") > 0;

  // Write epoch time (or *)
  if (NTP_IS_SYNC) {
    ltoa(RTC.getEpochTime(), epoch_time_str, 10);
    ok &= file.write(epoch_time_str) > 0;
  } else {
    ok &= file.write("*") > 0;
  }

  // Write \t before frame
  ok &= file.write("\t") > 0;

  ok &= file.write(frame.buffer, frame.length) > 0;

  if (ok) {
    USB.println(F("All write operation were succesful"));
  } else {
    USB.println(F("Some errors when writting."));
  }
  // Close the file
  SD.closeFile(&file);

  // Stop SD
  SD.OFF();
  USB.println(F("--------------- End of writeSD ------------------------"));
  return ok;
}

//====================================================================
// Measure the sensors
//====================================================================
void readSensors()
{
  USB.println(F("MEASURING SENSORS"));
  delay(100);

  // Reading BME
  bme.ON();

  temperature = bme.getTemperature();
  humidity = bme.getHumidity();
  pressure = bme.getPressure();

  bme.OFF();
  USB.println(F("****************************************"));

  // Read sensors
  // *************************
  //Reading electrochemical sensors
  gas_O3.ON();
  gas_SO2.ON();
  gas_CO.ON();
  gas_NO2.ON();

  USB.println(F("... Enter deep sleep mode 4 minutes to warm up sensors"));
//  PWR.deepSleep("00:00:04:00", RTC_OFFSET, RTC_ALM1_MODE1, SENSOR_ON);
  
  concentration_O3 = gas_O3.getConc(temperature);
  concentration_SO2 = gas_SO2.getConc(temperature);
  concentration_NO2 = gas_NO2.getConc(temperature);
  concentration_CO = gas_CO.getConc(temperature);

  gas_O3.OFF();
  gas_SO2.OFF();
  gas_CO.OFF();
  gas_NO2.OFF();

  PM.ON();
    
    //Reading Particle matter 
  USB.print("...Reading PM sensor...");
  status = PM.getPM(8000, 5000);
  if (status == 1) 
  {
    USB.println(F(" OK"));
  }
  else
  {
    USB.println(F(" Error"));  
  }  
  //OFF PARTICLE SENSOR
  PM.OFF();
    
  USB.println(F("****************************************"));


  ////////////////////////////
  //SHOWING RESULTS OF THE MEASURENMENTS
  ////////////////////////////
  //BME280 measure
  USB.println(F("... MEASUREMENT RESULTS..."));
  USB.println(F("... *************************************"));
  USB.print(F("... Ambient temperature --> "));
  USB.print(temperature);
  USB.println(F(" ºC"));
  USB.print(F("... Ambient Humidity --> "));
  USB.print(humidity);
  USB.println(F(" %"));
  USB.print(F("... Ambient pressure --> "));
  USB.print(pressure);
  USB.println(F(" Pa"));

  //Electrochemical sensor measure
  USB.print(F("... O3 concentration: "));
  USB.print(concentration_O3);
  USB.println(F(" ppm"));
  USB.print(F("... SO2 concentration: "));
  USB.print(concentration_SO2);
  USB.println(F(" ppm"));
  USB.print(F("... NO2 concentration: "));
  USB.print(concentration_NO2);
  USB.println(F(" ppm"));
  USB.print(F("... CO concentration: "));
  USB.print(concentration_CO);
  USB.println(F(" ppm"));

  //Particle matter measure
  USB.print(F("...PM 1: "));
  USB.print(PM._PM1);
  USB.println(F(" ug/m3"));
  USB.print(F("...PM 2.5: "));
  USB.print(PM._PM2_5);
  USB.println(F(" ug/m3"));
  USB.print(F("...PM 10: "));
  USB.print(PM._PM10);
  USB.println(F(" ug/m3"));
  USB.println(F("... *************************************"));

}

//====================================================================
// Create a Data Frame Lorawan
//====================================================================
void CreateDataFrame(uint8_t frame_type) {
  USB.println(F("..CREATING LoRAWAN FRAME PROCESS "));
  frame.createFrame(ASCII, MOTE_ID);
  // set frame fields (Sensors Values)
  frame.addSensor(SENSOR_BAT, battery);
  frame.addSensor(SENSOR_GASES_PRO_TC, temperature);
  frame.addSensor(SENSOR_GASES_PRO_HUM, humidity);
  frame.addSensor(SENSOR_GASES_PRO_PRES, pressure);


  //Electrochemical snsors
  frame.addSensor(SENSOR_GASES_PRO_O3, concentration_O3);
  frame.addSensor(SENSOR_GASES_PRO_SO2, concentration_SO2);
  frame.addSensor(SENSOR_GASES_PRO_NO2, concentration_NO2);
  frame.addSensor(SENSOR_GASES_PRO_CO, concentration_CO);
  
  //particle matter
  frame.addSensor(SENSOR_GASES_PRO_PM1, PM._PM1); 
  frame.addSensor(SENSOR_GASES_PRO_PM2_5, PM._PM2_5); 
  frame.addSensor(SENSOR_GASES_PRO_PM10, PM._PM10);

//  //Step 3: send a Libelium frame to the Multitech
//  SendDataLW(); 
  frame.showFrame();
}
//====================================================================
// Send Data Frame lorawan
//====================================================================
void SendDataLW(void) {
  uint8_t tx_error, error;
  USB.println();
  USB.println(F("SENDING LORAWAN DATA PROCESS"));

  ///////////////////////////////////////////
  // 2.2 Send frame using LoRaWAN
  // 2.2.1. Switch on
  error = LoRaWAN.ON(SOCKET0);
  // Check status
  if (error == 0) {
    USB.println(F("...Switch ON OK"));
  } else {
    USB.print(F("... Switch ON error = "));
    USB.println(error, DEC);
  }
  // 2.2.2. Join network
  error = LoRaWAN.joinABP();
  if (error == 0) {
    USB.println(F("...Join network OK"));

    // 2.2.3. Send confirmed packet

    LoRaWAN.getDownCounter();
    LoRaWAN.getUpCounter();
    error = LoRaWAN.sendUnconfirmed(PORT, frame.buffer, frame.length);
    LoRaWAN.getDownCounter();
    LoRaWAN.getUpCounter();

    // Error messages:
    /*
       '6' : Module hasn't joined a network
       '5' : Sending error
       '4' : Error with data length
       '2' : Module didn't response
       '1' : Module communication error
    */
    // Check status
    if (error == 0) {
      USB.println(F("... Send Unconfirmed packet OK"));
    } else {
      USB.print(F("...Send Unconfirmed packet error = "));
      USB.println(error, DEC);
      error_flag = 1;
      cont++;
    }
  } else {
    USB.print(F("...Join network error = "));
    USB.println(error, DEC);
  }

  // 2.2.4. Switch off
  error = LoRaWAN.OFF(lora_socket);
  if (error == 0) {
    USB.println(F("... Switch OFF OK"));
  } else {
    USB.print(F("...Switch OFF error = "));
    USB.println(error, DEC);
  }
}
//====================================================================
// Configure LoRaWAN module
//====================================================================
void set868(void) {

  // 1. switch on
  USB.println(F("CONFIGURE LORAWAN MODULE"));
  USB.println(F("... 0.1 Configure module"));

  error = LoRaWAN.ON(lora_socket);

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.1  Switch ON OK"));
  } else {
    USB.print(F("....... 0.1.1  Switch ON error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  // 2. Reset to factory default values

  error = LoRaWAN.factoryReset();

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.2  Reset to factory default values OK"));
  } else {
    USB.print(F("....... 0.1.2  Reset to factory error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  // 3. Set Device EUI
  error = LoRaWAN.setDeviceEUI(DEVICE_EUI);

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.3  Set Device EUI OK"));
  } else {
    USB.print(F("....... 0.1.3  Set Device EUI error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  //////////////////////////////////////////////
  // 4. Set Application EUI
  //////////////////////////////////////////////

  error = LoRaWAN.setAppEUI(APP_EUI);

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.4  Application EUI set OK"));
  } else {
    USB.print(F("....... 0.1.4  Application EUI set error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  //////////////////////////////////////////////
  // 5. Set Application Session Key
  //////////////////////////////////////////////

  error = LoRaWAN.setAppKey(APP_KEY);

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.5 Application Key set OK"));
  } else {
    USB.print(F("....... 0.1.5 Application Key set error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  // 7. Set retransmissions for uplink confirmed packet
  // set retries
  error = LoRaWAN.setRetries(7);

  // Check status
  if (error == 0) {
    USB.println(
        F("....... 0.1.7  Set Retransmissions for uplink confirmed packet OK"));
  } else {
    USB.print(F("....... 0.1.7  Set Retransmissions for uplink confirmed "
                "packet error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  // 13. Set Adaptive Data Rate (recommended)
  // set ADR
  error = LoRaWAN.setADR("on");

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.9  Set Adaptive data rate status to on OK"));
  } else {
    USB.print(F("....... 0.1.9  Set Adaptive data rate status to on error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  error = LoRaWAN.setDataRate(4);

  // Check status
  if (error == 0) {
    USB.println(F("..............Data rate set OK"));
  } else {
    USB.print(F("2. Data rate set error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  // 14. Set Automatic Reply
  // set AR
  error = LoRaWAN.setAR("on");

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.10 Set automatic reply status to on OK"));
  } else {
    USB.print(F("....... 0.1.10 Set automatic reply status to on error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  /////////////////////////////////////////////////
  // 6. Join OTAA to negotiate keys with the server
  /////////////////////////////////////////////////

  error = LoRaWAN.joinOTAA();

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.11 Join network OK"));
  } else {
    USB.print(F("....... 0.1.11 Join network error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  // 15. Save configuration

  error = LoRaWAN.saveConfig();

  // Check status
  if (error == 0) {
    USB.println(F("....... 0.1.12 Save configuration OK\n"));
  } else {
    USB.print(F("....... 0.1.12 Save configuration error = "));
    USB.println(error, DEC);
    goto set868_end;
  }

  // Set the flag which says the LW was set
  LW_IS_SET = true;

  set868_end:
  USB.println("Switching LoRa Module OFF");
  LoRaWAN.OFF(lora_socket);
}

////====================================================================
//// Configure 4G module
////====================================================================
//void set4G() {
//  //////////////////////////////////////////////////
//  // 1. sets operator parameters
//  //////////////////////////////////////////////////
//  USB.println("SETTING 4G PARAMETERS...");
//  _4G.set_APN(apn, login, password);
//
//  //////////////////////////////////////////////////
//  // 2. Show APN settings via USB port
//  //////////////////////////////////////////////////
//  _4G.show_APN();
//
//  //////////////////////////////////////////////////
//  // 4. set PIN
//  //////////////////////////////////////////////////
//  if(!strcmp(PIN, "")) 
//    return;
//
//  USB.println(F("Setting PIN code..."));
//  if (_4G.enterPIN(PIN) == 0) {
//    USB.println(F("PIN code accepted"));
//  } else {
//    USB.println(F("PIN code incorrect"));
//  }
//}

//====================================================================
// Set time from 4G
//====================================================================
//void setTime4G() {
//  USB.println(F("Setting time from 4G...."));
//  error = _4G.ON();
//
//  if (error == 0) {
//    USB.println(F("4G module ready..."));
//
//    ////////////////////////////////////////////////
//    // Check connection to network and continue
//    ////////////////////////////////////////////////
//    connection_status = _4G.checkDataConnection(30);
//    delay(5000);
//    //////////////////////////////////////////////////
//    // 3. set time
//    //////////////////////////////////////////////////
//    if (connection_status == 0) {
//      if (_4G.setTimeFrom4G() == 0) {
//        USB.println(F("Succesufully set time from 4G"));
//        NTP_IS_SYNC = true;
//      } else {
//        USB.println(F("Failed to get time from 4G"));
//      }
//    }
//  } else {
//    // Problem with the communication with the 4G module
//    USB.println(F("4G module not started"));
//    USB.print(F("Error code: "));
//    USB.println(error, DEC);
//  }
//  ////////////////////////////////////////////////
//  // 4. Powers off the 4G module
//  ////////////////////////////////////////////////
//  USB.println(F("Switch OFF 4G module\n"));
//  _4G.OFF();
//}

////====================================================================
//// Send Data Frame 4G
////====================================================================
//void send4G() {
//
//  error = _4G.ON();
//
//  if (error == 0) {
//    USB.println(F("4G module ready..."));
//
//    ////////////////////////////////////////////////
//    // Check connection to network and continue
//    ////////////////////////////////////////////////
//    connection_status = _4G.checkDataConnection(30);
//    delay(5000);
//
//    ////////////////////////////////////////////////
//    // 3. Send to Meshlium
//    ////////////////////////////////////////////////
//    USB.print(F("Sending the frame..."));
//    error = _4G.sendFrameToMeshlium(host, port, frame.buffer, frame.length);
//
//    // check the answer
//    if (error == 0) {
//      USB.print(F("Done. HTTP code: "));
//      USB.println(_4G._httpCode);
//      USB.print("Server response: ");
//      USB.println(_4G._buffer, _4G._length);
//    } else {
//      USB.print(F("Failed. Error code: "));
//      USB.println(error, DEC);
//    }
//  } else {
//    // Problem with the communication with the 4G module
//    USB.println(F("4G module not started"));
//    USB.print(F("Error code: "));
//    USB.println(error, DEC);
//  }
//
//  ////////////////////////////////////////////////
//  // 4. Powers off the 4G module
//  ////////////////////////////////////////////////
//  USB.println(F("Switch OFF 4G module\n"));
//  _4G.OFF();
//}

void setup() {
  uint8_t error;

  // Turn ON the USB and print a start message
  USB.ON();
  delay(100);
  USB.println(F("\n*****************************************************"));
  USB.print(F("BEIA "));
  USB.println(MOTE_ID);
  USB.println(F("*****************************************************"));

  // Init RTC
  RTC.ON();

  // Function to configurate LoraWan module
  set868();

  // Set 4G
  //set4G();

  // Set time from 4G
  //setTime4G();
  
  // Getting time
  USB.print(F("Time [Day of week, YY/MM/DD, hh:mm:ss]: "));
  USB.println(RTC.getTime());
}

void loop() {
  // New iteration
  USB.ON();
  USB.println(F("\n*****************************************************"));
  USB.print(F("New iteration for BEIA "));
  USB.println(MOTE_ID);
  USB.println(F("*****************************************************"));

  // Turn on RTC and get starting time
  RTC.ON();

  // Check battery level
  battery = PWR.getBatteryLevel();
  USB.print(F("Battery level: "));
  USB.println(battery, DEC);

  // Step 1: Read suitable sensors
  readSensors();

  // Step 2: Create data Frame
  USB.println("Create ASCII Frame");
  CreateDataFrame(ASCII);

  // Step 3: Save on SD card
  writeSD();

  // Step 4: If enough battery, send data
  if (battery >= 30) {
/*
    if (!NTP_IS_SYNC) {
      USB.println("Time not set... retry...");
      setTime4G();
    }
*/
    if (!LW_IS_SET) {
      USB.println(F("LW is not set.... retry....."));
      set868();
    }

    // Step 4.1: Send using LoRa WAN
    SendDataLW();

    // Step 4.2: Send using 4G
    //send4G();
  } else {
    USB.println(F("Skip seding data... battery under 30%"));
  }

  USB.println(F("---------------------------------"));
  USB.println(F("...Enter deep sleep mode 15 min"));
  PWR.deepSleep("00:00:18:00", RTC_OFFSET, RTC_ALM1_MODE1, ALL_OFF);
  USB.ON();
  USB.print(F("...wake up!! Date: "));
  USB.println(RTC.getTime());

  RTC.setWatchdog(720); // 12h in minutes
  USB.print(F("...Watchdog :"));
  USB.println(RTC.getWatchdog());
  USB.println(F("****************************************"));
}

//***********************************************************************************************
// END OF THE SKETCH
//***********************************************************************************************

