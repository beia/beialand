/*
  Possible sockets for this sensor is:
  - XTR_SOCKET_E       _________
                      |---------|
                      | A  B  C |
                      |_D__E__F_|
*/
#include <Wasp4G.h>
#include <WaspFrame.h>
#include <WaspSensorXtr.h>

// choose socket (SELECT USER'S SOCKET)
///////////////////////////////////////
const uint8_t lora_socket = SOCKET0;
///////////////////////////////////////

//====================================================================
// INSTANCE DEFINITION
//====================================================================
//[Sensor Class] [Sensor Name] [Selected socket]
DatasolMET mySensor;

//====================================================================
// MOTE_ID
//===================================================================
// Default device name
// TODO: Change
char *MOTE_ID = "SAX2";

//====================================================================
// PARAMETERS FOR SD CARD
//===================================================================

char SD_FILENAME[] = "IOTDATA.TXT";

//====================================================================
// PARAMETERS TO CONFIGURE 4G RADIO
//===================================================================
// TODO: Check
char apn[] = "broadband";
char login[] = "";
char password[] = "";
char PIN[] = "";

// SERVER settings
///////////////////////////////////////
// TODO: Check/update
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
// TODO: Add variables

int status;

/////////////////////////////////////////////////
// Global flags (do not change until you know what are you doing
////////////////////////////////////////////////
bool NTP_IS_SYNC = false;;

/*
   Writes the frame to the SD card usind the following format:
   When RTC is okay (set)
   +\t<milis>\t<epoch_time>\t<frame>
   When RTC is not set
   -\t<milis>\t*\t<frame>
   Uses NTP_IS_SYNC to see if the RTC is set.
   Returns true if the write is succesful.
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
void readSensors() {
  delay(100);

  // TODO: Read sensors
  USB.println(F("****************************************"));

  ///////////////////////////////////////////
  //  1. Read the sensors
  ///////////////////////////////////////////
  // 1. Turn ON the sensor
  mySensor.ON();

  // 2. Read the sensor
  /*
    Note: read() function does not directly return sensor values.
    They are stored in the class vector variables defined for that purpose.
    Values are available as a float value
  */
  mySensor.read();

  // 3. Turn off the sensor
  mySensor.OFF();

  // 4. Print information
  USB.println(F("---------------------------"));
  USB.println(F("Datasol MET"));
  USB.print(F("Measured radiation: "));
  USB.print(mySensor.sensorDatasolMET.radiation);
  USB.println(F(" W/m2"));
  USB.print(F("Semi-cell 1 radiation: "));
  USB.print(mySensor.sensorDatasolMET.semicell1Radiation);
  USB.println(F(" W/m2"));
  USB.print(F("Semi-cell 2 radiation: "));
  USB.print(mySensor.sensorDatasolMET.semicell2Radiation);
  USB.println(F(" W/m2"));
  USB.print(F("Environment temperature: "));
  USB.printFloat(mySensor.sensorDatasolMET.environmentTemperature, 1);
  USB.println(F(" degrees Celsius"));
  USB.print(F("Panel temperature: "));
  USB.printFloat(mySensor.sensorDatasolMET.panelTemperature, 1);
  USB.println(F(" degrees Celsius"));
  USB.print(F("Peak sun hours: "));
  USB.printFloat(mySensor.sensorDatasolMET.peakSunHours, 2);
  USB.println(F(" hours"));
  USB.print(F("Necessary cleaning notice: "));
  USB.println(mySensor.sensorDatasolMET.necessaryCleaningNotice);
  USB.print(F("Wind speed: "));
  USB.printFloat(mySensor.sensorDatasolMET.windSpeed, 1);
  USB.println(F(" m/s"));
  USB.println(F("---------------------------\n"));

  delay(5000);

  USB.println(F("... *************************************"));
}

//====================================================================
// Create a Data Frame Lorawan
//====================================================================
void CreateDataFrame(uint8_t frame_type) {
  USB.println(F("..CREATING FRAME PROCESS "));

  frame.createFrame(frame_type, MOTE_ID);
  frame.addTimestamp();
  // set frame fields (Sensors Values)
  //  frame.createFrame(BINARY, node_ID); // frame2
  frame.setFrameType(INFORMATION_FRAME_AGR_XTR);

  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());
  //  frame.addSensor(SENSOR_TIME, RTC.getTimestamp());
  frame.addSensor(AGRX_DATASOL_RAD, mySensor.sensorDatasolMET.radiation);
  frame.addSensor(AGRX_DATASOL_SC1_RAD, mySensor.sensorDatasolMET.semicell1Radiation);
  frame.addSensor(AGRX_DATASOL_SC2_RAD, mySensor.sensorDatasolMET.semicell2Radiation);
  frame.addSensor(AGRX_DATASOL_ETC, mySensor.sensorDatasolMET.environmentTemperature, 1);
  frame.addSensor(AGRX_DATASOL_PTC, mySensor.sensorDatasolMET.panelTemperature, 1);
  frame.addSensor(AGRX_DATASOL_PSH, mySensor.sensorDatasolMET.peakSunHours, 2);
  frame.addSensor(AGRX_DATASOL_NCN, mySensor.sensorDatasolMET.necessaryCleaningNotice);

  //#define AGRX_DATASOL_RAD        95
  //#define AGRX_DATASOL_SC1_RAD      96
  //#define AGRX_DATASOL_SC2_RAD      97
  //#define AGRX_DATASOL_ETC        98
  //#define AGRX_DATASOL_PTC        99
  //#define AGRX_DATASOL_WSP        100
  //#define AGRX_DATASOL_PSH        101
  //#define AGRX_DATASOL_NCN
  frame.showFrame();
}

//====================================================================
// Configure 4G module
//====================================================================
void set4G() {
  //////////////////////////////////////////////////
  // 1. sets operator parameters
  //////////////////////////////////////////////////
  USB.println("SETTING 4G PARAMETERS...");
  _4G.set_APN(apn, login, password);

  //////////////////////////////////////////////////
  // 2. Show APN settings via USB port
  //////////////////////////////////////////////////
  _4G.show_APN();

  //////////////////////////////////////////////////
  // 4. set PIN
  //////////////////////////////////////////////////
  if (!strcmp(PIN, ""))
    return;

  USB.println(F("Setting PIN code..."));
  if (_4G.enterPIN(PIN) == 0) {
    USB.println(F("PIN code accepted"));
  } else {
    USB.println(F("PIN code incorrect"));
  }
}

//====================================================================
// Set time from 4G
//====================================================================
void setTime4G() {
  USB.println(F("Setting time from 4G...."));
  error = _4G.ON();

  if (error == 0) {
    USB.println(F("4G module ready..."));

    ////////////////////////////////////////////////
    // Check connection to network and continue
    ////////////////////////////////////////////////
    connection_status = _4G.checkDataConnection(30);
    delay(5000);
    //////////////////////////////////////////////////
    // 3. set time
    //////////////////////////////////////////////////
    if (connection_status == 0) {
      if (_4G.setTimeFrom4G() == 0) {
        USB.println(F("Succesufully set time from 4G"));
        NTP_IS_SYNC = true;
      } else {
        USB.println(F("Failed to get time from 4G"));
      }
    }
  } else {
    // Problem with the communication with the 4G module
    USB.println(F("4G module not started"));
    USB.print(F("Error code: "));
    USB.println(error, DEC);
  }
  ////////////////////////////////////////////////
  // 4. Powers off the 4G module
  ////////////////////////////////////////////////
  USB.println(F("Switch OFF 4G module\n"));
  _4G.OFF();
}

//====================================================================
// Send Data Frame 4G
//====================================================================
void send4G() {

  error = _4G.ON();

  if (error == 0) {
    USB.println(F("4G module ready..."));

    ////////////////////////////////////////////////
    // Check connection to network and continue
    ////////////////////////////////////////////////
    connection_status = _4G.checkDataConnection(30);
    delay(5000);

    ////////////////////////////////////////////////
    // 3. Send to Meshlium
    ////////////////////////////////////////////////
    USB.print(F("Sending the frame..."));
    error = _4G.sendFrameToMeshlium(host, port, frame.buffer, frame.length);

    // check the answer
    if (error == 0) {
      USB.print(F("Done. HTTP code: "));
      USB.println(_4G._httpCode);
      USB.print("Server response: ");
      USB.println(_4G._buffer, _4G._length);
    } else {
      USB.print(F("Failed. Error code: "));
      USB.println(error, DEC);
    }
  } else {
    // Problem with the communication with the 4G module
    USB.println(F("4G module not started"));
    USB.print(F("Error code: "));
    USB.println(error, DEC);
  }

  ////////////////////////////////////////////////
  // 4. Powers off the 4G module
  ////////////////////////////////////////////////
  USB.println(F("Switch OFF 4G module\n"));
  _4G.OFF();
}

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

  // Set 4G
  set4G();

  // Set time from 4G
  setTime4G();

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

    if (!NTP_IS_SYNC) {
      USB.println("Time not set... retry...");
      setTime4G();
    }

    // Step 4.1: Send using 4G
    CreateDataFrame(BINARY);
    send4G();
  } else {
    USB.println(F("Skip seding data... battery under 30%"));
  }

  USB.println(F("---------------------------------"));
  USB.println(F("...Enter deep sleep mode 30 min"));
  PWR.deepSleep("00:00:30:00", RTC_OFFSET, RTC_ALM1_MODE1, ALL_OFF);
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