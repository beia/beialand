// Library include
#include < WaspSensorGas_v30.h >
#include < WaspFrame.h >
#include < WaspXBee802.h >

// Destination MAC address
//////////////////////////////////////////
char RX_ADDRESS[] = "0013A20041678B8C";
// Define the Waspmote ID
char WASPMOTE_ID[] = "Gas_Board";
// define variable
uint8_t error;

// PAN (Personal Area Network) Identifier
uint8_t panID[2] = {
  0x12,
  0x34
};
// Define Freq Channel to be set:
// Center Frequency = 2.405 + (CH - 11d) * 5 MHz
//   Range: 0x0B - 0x1A (XBee)
//   Range: 0x0C - 0x17 (XBee-PRO)
uint8_t channel = 0x0F;
// Define the Encryption mode: 1 (enabled) or 0 (disabled)
uint8_t encryptionMode = 0;
// Define the AES 16-byte Encryption Key
char encryptionKey[] = "WaspmoteLinkKey!";
// NO2 Sensor must be connected physically in SOCKET_3
NO2SensorClass NO2Sensor;
// CO Sensor must be connected physically in SOCKET_4
COSensorClass COSensor;
// VOC Sensor must be connected in SOCKET_5
VOCSensorClass VOCSensor;
// Calibration voltages obtained during calibration process (in KOHMs)
#define POINT1_RES_NO2 45.25 // <-- Rs at normal concentration in air
#define POINT2_RES_NO2 25.50# define POINT3_RES_NO2 3.55
// Calibration resistances obtained during calibration process
# define POINT1_RES_CO 230.30 // <-- Ro Resistance at 100 ppm. Necessary value.
# define POINT2_RES_CO 40.665 //
# define POINT3_RES_CO 20.300 //
# define POINT1_RES_VOC 230.30 // <-- Ro Resistance at 100 ppm. Necessary value.
# define POINT2_RES_VOC 40.665 //
# define POINT3_RES_VOC 20.300 //
// Concentrations used in calibration process
# define POINT1_PPM_NO2 10.0 // <-- Normal concentration in air
# define POINT2_PPM_NO2 50.0# define POINT3_PPM_NO2 100.0
// Concentratios used in calibration process
# define POINT1_PPM_CO 100.0 // <--- Ro value at this concentration
# define POINT2_PPM_CO 300.0 //
# define POINT3_PPM_CO 1000.0 //
# define POINT1_PPM_VOC 100.0 //  <--- Ro value at this concentration
# define POINT2_PPM_VOC 300.0# define POINT3_PPM_VOC 1000.0
// Define the number of calibration points
# define numPoints 3
float temperature; // Stores the temperature in ºC
float humidity; // Stores the realitve humidity in %RH
float pressure; // Stores the pressure in Pa
float NO2resValues[] = {
  POINT1_RES_NO2,
  POINT2_RES_NO2,
  POINT3_RES_NO2
};

float resValues[] = {
  POINT1_RES_CO,
  POINT2_RES_CO,
  POINT3_RES_CO
};

float VOCresValues[] = {
  POINT1_RES_VOC,
  POINT2_RES_VOC,
  POINT3_RES_VOC
};

float NO2concentrations[] = {
  POINT1_PPM_NO2,
  POINT2_PPM_NO2,
  POINT3_PPM_NO2
};

float concentrations[] = {
  POINT1_PPM_CO,
  POINT2_PPM_CO,
  POINT3_PPM_CO
};

float VOCconcentrations[] = {
  POINT1_PPM_VOC,
  POINT2_PPM_VOC,
  POINT3_PPM_VOC
};

void setup()
{
  // Configure the USB port
  USB.ON();
  USB.println(F("-------------------------------"));
  USB.println(F("Configure XBee 802.15.4"));
  USB.println(F("-------------------------------"));

  // init XBee
  xbee802.ON();

  /////////////////////////////////////
  // 1. set channel
  /////////////////////////////////////
  xbee802.setChannel(channel);

  // check at commmand execution flag
  if (xbee802.error_AT == 0) {
    USB.print(F("1. Channel set OK to: 0x"));
    USB.printHex(xbee802.channel);
    USB.println();
  } else {
    USB.println(F("1. Error calling 'setChannel()'"));
  }

  /////////////////////////////////////
  // 2. set PANID
  /////////////////////////////////////
  xbee802.setPAN(panID);

  // check the AT commmand execution flag
  if (xbee802.error_AT == 0) {

    USB.print(F("2. PAN ID set OK to: 0x"));
    USB.printHex(xbee802.PAN_ID[0]);
    USB.printHex(xbee802.PAN_ID[1]);
    USB.println();

  } else {
    USB.println(F("2. Error calling 'setPAN()'"));
  }

  /////////////////////////////////////
  // 3. set encryption mode (1:enable; 0:disable)
  /////////////////////////////////////
  xbee802.setEncryptionMode(encryptionMode);

  // check the AT commmand execution flag
  if (xbee802.error_AT == 0) {
    USB.print(F("3. AES encryption configured (1:enabled; 0:disabled):"));
    USB.println(xbee802.encryptMode, DEC);
  } else {
    USB.println(F("3. Error calling 'setEncryptionMode()'"));
  }

  /////////////////////////////////////
  // 4. set encryption key
  /////////////////////////////////////
  xbee802.setLinkKey(encryptionKey);

  // check the AT commmand execution flag
  if (xbee802.error_AT == 0) {
    USB.println(F("4. AES encryption key set OK"));
  } else {
    USB.println(F("4. Error calling 'setLinkKey()'"));
  }
  /////////////////////////////////////
  // 5. write values to XBee module memory
  /////////////////////////////////////
  xbee802.writeValues();

  // check the AT commmand execution flag
  if (xbee802.error_AT == 0) {
    USB.println(F("5. Changes stored OK"));
  } else {
    USB.println(F("5. Error calling 'writeValues()'"));
  }

  USB.println(F("-------------------------------"));
  USB.println(F("Sending packets example"));

  // store Waspmote identifier in EEPROM memory
  frame.setID(WASPMOTE_ID);

  // Turn on the sensor board
  Gases.ON();
  delay(100);
  NO2Sensor.ON();
  COSensor.ON();
  VOCSensor.ON();

  // Calculate the slope and the intersection of the logarithmic function
  NO2Sensor.setCalibrationPoints(NO2resValues, NO2concentrations, numPoints);

  // Calculate the slope and the intersection of the logarithmic function
  COSensor.setCalibrationPoints(resValues, concentrations, numPoints);

  // Calculate the slope and the intersection of the logarithmic function
  VOCSensor.setCalibrationPoints(VOCresValues, VOCconcentrations, numPoints);

}

void loop()
{
  /////////////////////////////////////
  // 1. get channel
  /////////////////////////////////////
  xbee802.getChannel();
  USB.print(F("channel: "));
  USB.printHex(xbee802.channel);
  USB.println();

  /////////////////////////////////////
  // 2. get PANID
  /////////////////////////////////////
  xbee802.getPAN();
  USB.print(F("panid: "));
  USB.printHex(xbee802.PAN_ID[0]);
  USB.printHex(xbee802.PAN_ID[1]);
  USB.println();

  ////////////////////////////////////
  // 3. get encryption mode (1:enable; 0:disable)
  /////////////////////////////////////
  xbee802.getEncryptionMode();
  USB.print(F("encryption mode: "));
  USB.printHex(xbee802.encryptMode);
  USB.println();
  USB.println(F("-------------------------------"));
  delay(3000);

  //////////////////////////////////////////
  // 2. Read sensors
  //////////////////////////////////////////
  temperature = Gases.getTemperature();
  humidity = Gases.getHumidity();
  pressure = Gases.getPressure();

  float NO2Vol = NO2Sensor.readVoltage();
  float coVol = COSensor.readVoltage();
  float vocVol = VOCSensor.readVoltage();
  float NO2Res = NO2Sensor.readResistance(NO2Vol); // Resistance of the sensor
  float coRes = COSensor.readResistance(coVol);
  float vocRes = VOCSensor.readResistance(vocVol); // Resistance of the sensor
  float NO2PPM = NO2Sensor.readConcentration(NO2Res); // PPM value of NO2
  float coPPM = COSensor.readConcentration(coRes); // PPM value of CO
  float vocPPM = VOCSensor.readConcentration(vocRes); // PPM value of VOC

  char node_ID[] = "TRHP_CO_VOC_NO2_example";

  // Print of the results
  USB.print(F(" Temperature: "));
  USB.print(temperature);
  USB.println(F(" Celsius Degrees |"));

  USB.print(F(" Humidity : "));
  USB.print(humidity);
  USB.println(F(" %%RH"));

  USB.print(F(" Pressure : "));
  USB.print(pressure);
  USB.println(F(" Pa"));

  USB.print(F(" CO Sensor Voltage: "));
  USB.print(coVol);
  USB.println(F(" mV |"));

  USB.print(F(" VOC Sensor Voltage: "));
  USB.print(vocVol);
  USB.println(F(" V |"));

  USB.print(F(" NO2 Sensor Voltage: "));
  USB.print(NO2Vol);
  USB.println(F(" V |"));

  USB.print(F(" CO Sensor Resistance: "));
  USB.print(coRes);
  USB.println(F(" Ohms |"));

  USB.print(F(" VOC Sensor Resistance: "));
  USB.print(vocRes);
  USB.println(F(" Ohms |"));

  USB.print(F(" NO2 Sensor Resistance: "));
  USB.print(NO2Res);
  USB.println(F(" Ohms |"));

  USB.print(F(" CO concentration Estimated: "));
  USB.print(coPPM);
  USB.println(F(" ppm"));

  USB.print(F(" VOC concentration Estimated: "));
  USB.print(vocPPM);
  USB.println(F(" ppm"));

  USB.print(F(" NO2 concentration Estimated: "));
  USB.print(NO2PPM);
  USB.println(F(" ppm"));

  USB.println();

  ///////////////////////////////////////////
  // 3. Create BINARY frame
  ///////////////////////////////////////////
  frame.createFrame(BINARY);
  frame.addSensor(SENSOR_GASES_TC, Gases.getTemperature());
  frame.addSensor(SENSOR_GASES_HUM, Gases.getHumidity());
  frame.addSensor(SENSOR_GASES_PRES, Gases.getPressure());
  frame.addSensor(SENSOR_GASES_NO2, NO2Sensor.readConcentration(NO2Res));
  frame.addSensor(SENSOR_GASES_CO, COSensor.readConcentration(coRes));
  frame.addSensor(SENSOR_GASES_VOC, VOCSensor.readConcentration(vocRes));

  // send XBee packet
  error = xbee802.send(RX_ADDRESS, frame.buffer, frame.length);

  delay(5000);

  // check TX flag
  if (error == 0) {
    USB.println(F("send ok"));
    // blink green LED
    Utils.blinkGreenLED();
  } else {
    USB.println(F("send error"));
    // blink red LED
    Utils.blinkRedLED();
  }
  delay(2000);
}
