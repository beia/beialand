/*
 *  ------------  [GP_v30_01] - Electrochemical gas sensors  --------------
 *
 *  Explanation: This is the basic code to manage and read an electrochemical
 *  gas sensor. These sensors include: CO, O2, O3, NO, NO2, SO2, NH3, H2, H2S,
 *  HCl, HCN, PH3, ETO and Cl2. Cycle time: 2 minutes
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
 *  Version:           3.1
 *  Design:            David Gascón
 *  Implementation:    Alejandro Gállego
 */
#include < WaspSensorGas_Pro.h >
#include < WaspXBee802.h >
#include < WaspFrame.h >
  /*
   * Define object for sensor: gas_PRO_sensor
   * Input to choose board socket.
   * Waspmote OEM. Possibilities for this sensor:
   *  - SOCKET_1
   *  - SOCKET_2
   *  - SOCKET_3
   *  - SOCKET_4
   *  - SOCKET_5
   *  - SOCKET_6
   * P&S! Possibilities for this sensor:
   *  - SOCKET_A
   *  - SOCKET_B
   *  - SOCKET_C
   *  - SOCKET_F
   */
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
Gas CO_PRO_sensor(SOCKET_3);
Gas NO2_PRO_sensor(SOCKET_5);
Gas SO2_PRO_sensor(SOCKET_6);
Gas O3_PRO_sensor(SOCKET_4);
float COconcentration; // Stores the concentration level in ppm
float SO2concentration; // Stores the concentration level in ppm
float O3concentration; // Stores the concentration level in ppm
float O3NO2concentration; // Stores the concentration level in ppm
float NO2concentration; // Stores the concentration level in ppm

// Destination MAC address
//////////////////////////////////////////
char RX_ADDRESS[] = "0013A20041678B8C";
//////////////////////////////////////////
// Define the Waspmote ID
char WASPMOTE_ID[] = "node_01";
// define variable
uint8_t error;

void setup()
{
  // open USB port
  USB.ON();
  USB.println(F("Electrochemical gas sensor example"));
  frame.setID(WASPMOTE_ID);

  ///////////////////////////////////////////
  // 1. Turn on the sensors
  ///////////////////////////////////////////
  // Power on the electrochemical sensor.
  // If the gases PRO board is off, turn it on automatically.
  CO_PRO_sensor.ON();
  NO2_PRO_sensor.ON();
  SO2_PRO_sensor.ON();
  O3_PRO_sensor.ON();
  // First sleep time
  // After 2 minutes, Waspmote wakes up thanks to the RTC Alarm
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
  ////////////////////////////////////
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
  delay(20000);
}

void loop()
{
  ///////////////////////////////////////////
  // 2. Read sensors
  ///////////////////////////////////////////
  COconcentration = CO_PRO_sensor.getConc();
  NO2concentration = NO2_PRO_sensor.getConc();
  SO2concentration = SO2_PRO_sensor.getConc();
  O3concentration = O3_PRO_sensor.getConc();
  // And print the values via USB
  USB.println(F("***************************************"));
  USB.print(F("CO concentration: "));
  USB.print(COconcentration);
  USB.println(F(" ppm"));
  USB.print(F("NO2 concentration: "));
  USB.print(NO2concentration);
  USB.println(F(" ppm"));
  USB.print(F("SO2 concentration: "));
  USB.print(SO2concentration);
  USB.println(F(" ppm"));
  USB.print(F("O3 concentration: "));
  USB.print(O3concentration);
  USB.println(F(" ppm"));
  //delay(20000);
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
  /////////////////////////////////////
  // 3. get encryption mode (1:enable; 0:disable)
  /////////////////////////////////////
  xbee802.getEncryptionMode();
  USB.print(F("encryption mode: "));
  USB.printHex(xbee802.encryptMode);
  USB.println();
  USB.println(F("-------------------------------"));
  delay(20000);
  ///////////////////////////////////////////
  ///////////////////////////////////////////
  // 1. Create ASCII frame
  ///////////////////////////////////////////
  // create new frame
  frame.createFrame(ASCII);
  // add frame fields
  frame.addSensor(SENSOR_STR, "new_sensor_frame");
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());
  frame.addSensor(SENSOR_GASES_PRO_CO, CO_PRO_sensor.getConc());
  frame.addSensor(SENSOR_GASES_PRO_NO2, NO2_PRO_sensor.getConc());
  frame.addSensor(SENSOR_GASES_PRO_SO2, SO2_PRO_sensor.getConc());
  frame.addSensor(SENSOR_GASES_PRO_O3, O3_PRO_sensor.getConc());
  ///////////////////////////////////////////
  // 2. Send packet
  ///////////////////////////////////////////
  //send XBee packet
  error = xbee802.send(RX_ADDRESS, frame.buffer, frame.length);
  // check TX flag
  if (error == 0) {
    USB.println(F("send ok"));
    // blink green LED
    Utils.blinkGreenLED();
  } else {
    USB.println(F("send error"));
    // blink red LED
    Utils.blinkRedLED();
    // After 2 minutes, Waspmote wakes up thanks to the RTC Alarm
    delay(20000);
  }
}
