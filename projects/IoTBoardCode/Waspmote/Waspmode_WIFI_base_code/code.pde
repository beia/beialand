#include <WaspFrame.h>
#include <WaspPM.h>
#include <WaspSD.h>
#include <WaspWIFI_PRO.h>

// define variable SD
// define file name: MUST be 8.3 SHORT FILE NAME
char filename[] = "FILE1.TXT";

char *time_date; // stores curent date + time
int x, b;
uint8_t error;
uint8_t status = false;
char y[3];
uint8_t sd_answer, ssent, resend_f = 2; // frame resend atempts
bool sentence = false; // true for deletion on reboot  , false for data appended
                       // to end of file
bool IRL_time = false; //  true for no external data source
int cycle_time, cycle_time2 = 120; // in seconds
char rtc_str[] = "00:00:00:05";    // 11 char ps incepe de la 0
unsigned long prev, previous;

// choose NTP server settings
///////////////////////////////////////
char SERVER1[] = "time.nist.gov";
char SERVER2[] = "wwv.nist.gov";

//"pool.ntp.org";

///////////////////////////////////////

// Define Time Zone from -12 to 12 (i.e. GMT+2)
///////////////////////////////////////
uint8_t time_zone = 2;
///////////////////////////////////////

// choose socket (SELECT USER'S SOCKET)
///////////////////////////////////////
uint8_t socket = SOCKET1;
///////////////////////////////////////

// choose URL settings
///////////////////////////////////////
char type[] = "http";
char host[] = "82.78.81.178";
char port[] = "80";
///////////////////////////////////////

char node_ID[] = "cevax";
int count_trials = 0;
int N_trials = 10;
char ESSID[] = "LANCOMBEIA";
char PASSW[] = "beialancom";

// subprograme

void scriitor_SD(char filename_a[], uint8_t ssent_a = 0) {
  int coruption = 0;
  PWR.deepSleep("00:00:00:05", RTC_OFFSET, RTC_ALM1_MODE1, ALL_OFF);
  // now storeing it locally
  SD.ON();
  time_date = RTC.getTime();
  USB.print(F("time: "));
  USB.println(time_date);

  x = RTC.year;
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  sd_answer = SD.append(filename_a, y);
  coruption = coruption + sd_answer;
  sd_answer = SD.append(filename_a, ".");
  coruption = coruption + sd_answer;
  x = RTC.month;
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  sd_answer = SD.append(filename_a, y);
  coruption = coruption + sd_answer;
  sd_answer = SD.append(filename_a, ".");
  coruption = coruption + sd_answer;
  x = RTC.date;
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  sd_answer = SD.append(filename_a, y);
  coruption = coruption + sd_answer;
  sd_answer = SD.append(filename_a, ".");
  coruption = coruption + sd_answer;
  x = RTC.hour;
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  sd_answer = SD.append(filename_a, y);
  coruption = coruption + sd_answer;
  sd_answer = SD.append(filename_a, ".");
  coruption = coruption + sd_answer;
  x = RTC.minute;
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  sd_answer = SD.append(filename_a, y);
  coruption = coruption + sd_answer;
  sd_answer = SD.append(filename_a, ".");
  coruption = coruption + sd_answer;
  x = RTC.second;
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  sd_answer = SD.append(filename_a, y);
  coruption = coruption + sd_answer;
  sd_answer = SD.append(filename_a, "  ");
  coruption = coruption + sd_answer;
  sd_answer = SD.append(filename_a, frame.buffer, frame.length);
  coruption = coruption + sd_answer;
  sd_answer = SD.append(filename_a, "  ");
  coruption = coruption + sd_answer;
  itoa(ssent_a, y, 10);
  sd_answer = SD.appendln(filename_a, y);
  coruption = coruption + sd_answer;
  // frame is stored

  SD.OFF();

  if (coruption == 15) {
    USB.println("SD sorage done with no errors");
  } else {
    USB.print("SD sorage done with:");
    USB.print(15 - coruption);
    USB.println(" errors");
  }
}

void try_RTC_set() { //////////////////////////////////////////////////
  // 1. Switch ON
  //////////////////////////////////////////////////
  error = WIFI_PRO.ON(socket);

  if (error == 0) {
    USB.println(F("1. WiFi switched ON"));
  } else {
    USB.println(F("1. WiFi did not initialize correctly"));
  }

  //////////////////////////////////////////////////
  // 2. Check if connected
  //////////////////////////////////////////////////

  // get actual time
  previous = millis();

  // check connectivity
  status = WIFI_PRO.isConnected();

  // Check if module is connected
  if (status == true) {
    USB.print(F("2. WiFi is connected OK"));
    USB.print(F(" Time(ms):"));
    USB.println(millis() - previous);
  } else {
    USB.print(F("2. WiFi is connected ERROR"));
    USB.print(F(" Time(ms):"));
    USB.println(millis() - previous);
  }

  //////////////////////////////////////////////////
  // 3. Set RTC Time from WiFi module settings
  //////////////////////////////////////////////////

  // Check if module is connected
  if (status == true) {
    // 3.1. Open FTP session
    error = WIFI_PRO.setTimeFromWIFI();

    // check response
    if (error == 0) {
      USB.print(F("3. Set RTC time OK. Time:"));
      USB.println(RTC.getTime());
    } else {
      USB.println(F("3. Error calling 'setTimeFromWIFI' function"));
      WIFI_PRO.printErrorCode();
      status = false;
    }
  }

  //////////////////////////////////////////////////
  // 4. Switch OFF
  //////////////////////////////////////////////////
  WIFI_PRO.OFF(socket);
  USB.println(F("4. WiFi switched OFF\n\n"));
  USB.println(F("Wait 10 seconds...\n"));
  delay(10000);
}

void WiFi_init() { // 1. Switch ON the WiFi module
  //////////////////////////////////////////////////
  error = 1;
  while (error == 1) {
    error = WIFI_PRO.ON(socket);

    if (error == 0) {
      USB.println(F("1. WiFi switched ON"));
    } else {
      USB.println(F("1. WiFi did not initialize correctly"));
    }
  }

  // 2. Reset to default values
  //////////////////////////////////////////////////
  error = 1;
  while (error == 1) {
    error = WIFI_PRO.resetValues();

    if (error == 0) {
      USB.println(F("2. WiFi reset to default"));
    } else {
      USB.println(F("2. WiFi reset to default ERROR"));
    }
  }
  // 3. Set ESSID
  //////////////////////////////////////////////////
  error = 1;
  while (error == 1) {
    error = WIFI_PRO.setESSID(ESSID);

    if (error == 0) {
      USB.println(F("3. WiFi set ESSID OK"));
    } else {
      USB.println(F("3. WiFi set ESSID ERROR"));
    }
  }
  //////////////////////////////////////////////////
  // 4. Set password key (It takes a while to generate the key)
  // Authentication modes:
  //    OPEN: no security
  //    WEP64: WEP 64
  //    WEP128: WEP 128
  //    WPA: WPA-PSK with TKIP encryption
  //    WPA2: WPA2-PSK with TKIP or AES encryption
  //////////////////////////////////////////////////
  error = 1;
  while (error == 1) {
    error = WIFI_PRO.setPassword(WPA2, PASSW);

    if (error == 0) {
      USB.println(F("4. WiFi set AUTHKEY OK"));
    } else {
      USB.println(F("4. WiFi set AUTHKEY ERROR"));
    }
  }
  //////////////////////////////////////////////////
  // 5. Software Reset
  // Parameters take effect following either a
  // hardware or software reset
  //////////////////////////////////////////////////
  error = WIFI_PRO.softReset();

  if (error == 0) {
    USB.println(F("5. WiFi softReset OK"));
  } else {
    USB.println(F("5. WiFi softReset ERROR"));
  }

  USB.println(F("*******************************************"));
  USB.println(F("Once the module is configured with ESSID"));
  USB.println(F("and PASSWORD, the module will attempt to "));
  USB.println(F("join the specified Access Point on power up"));
  USB.println(F("*******************************************\n"));
}

// initializare

void setup() {

  //////////////////////////////////////////////////
  // 2. Check if connected
  //////////////////////////////////////////////////
  while (status == false) {
    WiFi_init(); // initialize Wi-Fi communication
    // get actual time
    previous = millis();

    // check connectivity
    status = WIFI_PRO.isConnected();

    // Check if module is connected
    if (status == true) {
      USB.print(F("2. WiFi is connected OK"));
      USB.print(F(" Time(ms):"));
      USB.println(millis() - previous);
    } else {
      USB.print(F("2. WiFi is connected ERROR"));
      USB.print(F(" Time(ms):"));
      USB.println(millis() - previous);
    }
  }

  //////////////////////////////////////////////////
  // 3. NTP server
  //////////////////////////////////////////////////

  // Check if module is connected
  if (status == true) {

    //    // 3.1. Set NTP Server (option1)
    error = WIFI_PRO.setTimeServer(1, SERVER1);

    // check response
    if (error == 0) {
      USB.println(F("3.1. Time Server1 set OK"));
    } else {
      USB.println(F("3.1. Error calling 'setTimeServer' function"));
      WIFI_PRO.printErrorCode();
      status = false;
    }

    // 3.2. Set NTP Server (option2)
    error = WIFI_PRO.setTimeServer(2, SERVER2);

    // check response
    if (error == 0) {
      USB.println(F("3.2. Time Server2 set OK"));
    } else {
      USB.println(F("3.2. Error calling 'setTimeServer' function"));
      WIFI_PRO.printErrorCode();
      status = false;
    }

    // 3.3. Enabled/Disable Time Sync
    if (status == true) {
      error = WIFI_PRO.timeActivationFlag(true);

      // check response
      if (error == 0) {
        USB.println(F("3.3. Network Time-of-Day Activation Flag set OK"));
      } else {
        USB.println(F("3.3. Error calling 'timeActivationFlag' function"));
        WIFI_PRO.printErrorCode();
        status = false;
      }
    }

    // 3.4. set GMT
    if (status == true) {
      error = WIFI_PRO.setGMT(time_zone);

      // check response
      if (error == 0) {
        USB.print(F("3.4. GMT set OK to "));
        USB.println(time_zone, DEC);
      } else {
        USB.println(F("3.4. Error calling 'setGMT' function"));
        WIFI_PRO.printErrorCode();
      }
    }
  }

  //
  //  //////////////////////////////////////////////////
  //  // 4. Switch OFF
  //  //////////////////////////////////////////////////
  //  USB.println(F("4. WiFi switched OFF\n"));
  //  WIFI_PRO.OFF(socket);

  USB.println(F("-----------------------------------------------------------"));
  USB.println(F("Once the module has the correct Time Server Settings"));
  USB.println(F("it is always possible to request for the Time and"));
  USB.println(F("synchronize it to the Waspmote's RTC"));
  USB.println(
      F("-----------------------------------------------------------\n"));
  delay(5000);

  // Init RTC
  //  RTC.ON();
  //  USB.print(F("Current RTC settings:"));
  //  USB.println(RTC.getTime());
  //

  // open USB port
  USB.ON();
  RTC.ON(); // Executes the init process
            //  USB.print(F("Current RTC settings:"));
            //  USB.println(RTC.getTime());
  IRL_time = false;

  if (IRL_time) {
    // Setting date and time [yy:mm:dd:dow:hh:mm:ss]
    RTC.setTime("19:01:01:03:00:00:00");
  } else {
    // Check if module is connected
    if (status == true) {
      // 3.1. Open FTP session
      error = WIFI_PRO.setTimeFromWIFI();

      // check response
      if (error == 0) {
        USB.print(F("3. Set RTC time OK. Time:"));
        USB.println(RTC.getTime());
      } else {
        USB.println(F("3. Error calling 'setTimeFromWIFI' function"));
        WIFI_PRO.printErrorCode();
        status = false;
      }
    }

    while ((count_trials < N_trials) && (status == false)) {
      try_RTC_set();
      USB.println(F("Trial"));
      count_trials = count_trials + 1;
      USB.print(count_trials);
      USB.println();
    }
  }

  USB.print(F("Current RTC settings:"));
  USB.println(RTC.getTime());
  USB.println(F("SD_CARD_ARHIVE_V4_RTC_ON_BAREBONES"));

  // Set SD ON
  SD.ON();

  if (sentence == 1) {
    // Delete file
    sd_answer = SD.del(filename);

    if (sd_answer == 1) {
      USB.println(F("file deleted"));
    } else {
      USB.println(F("file NOT deleted"));
    }
  }
  // Create file IF id doent exist
  sd_answer = SD.create(filename);

  if (sd_answer == 1) {
    USB.println(F("file created"));
  } else {
    USB.println(F("file NOT created"));
  }

  USB.print("loop cycle time[s]:= ");
  USB.println(cycle_time2);
  sd_answer = SD.appendln(filename, "------------------------------------------"
                                    "----------------------------------");
  if (sd_answer == 1) {
    USB.println(F("writeing is OK"));
  } else {
    USB.println(F("writeing is haveing errors"));
  }

  //!!!!!!!!!!!!!!!!!!!!!!!
  // Se va inlocui cu
  // check connectivity
  /*status =  WIFI_PRO.isConnected();
  if (status==false)
  {sd_answer = SD.appendln(filename,
  "----------------------------------------------------------------------------"
  )};*/

  // pm
  USB.ON();
}

// main program
void loop() {
  // get actual time before loop
  prev = millis();

  // get actual time before wifi
  previous = millis();
  //////////////////////////////////////////////////
  // 4. Switch ON
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  error = WIFI_PRO.ON(socket);
  b = 0;
qwerty:
  if (error == 0) {
    USB.println(F("WiFi switched ON"));
  } else {
    USB.println(F("WiFi did not initialize correctly"));
  }
  status = WIFI_PRO.isConnected();
  // check if module is connected
  if (status == true) {
    USB.print(F("WiFi is connected OK"));
    USB.print(F(" Time(ms):"));
    USB.println(millis() - previous);
    USB.print(F(" (time it took for the WIFI status check)"));

    // frame

    frame.createFrame(ASCII);
    frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel()); // Add BAT level
    frame.showFrame();                                  // frame is made

    // 3.2. Send Frame
    ///////////////////////////////
    // http frame
    previous = millis();
    error = WIFI_PRO.sendFrameToMeshlium(type, host, port, frame.buffer,
                                         frame.length); // frame

    // check response
    if (error == 0) {
      USB.println(F("HTTP OK"));
      ssent = 1;
      USB.print(F("HTTP Time from OFF state (ms):"));
      USB.println(millis() - previous);
      USB.println(F("ASCII FRAME SEND OK"));
    } else {
      USB.println(F("Error calling 'getURL' function"));
      ssent = 0;
      WIFI_PRO.printErrorCode();
    }
  } else {
    USB.print(F("WiFi is connected ERROR"));
    USB.print(F(" Time(ms):"));
    USB.println(millis() - previous);
  }
  if (ssent == 0 && b <= resend_f) {
    delay(5000);
    goto qwerty;
  }

  WIFI_PRO.OFF(socket);
  USB.println(F("WiFi switched OFF\n\n"));
  b = (millis() - prev) / 1000;
  USB.print("loop execution time[s]: ");
  USB.println(b);

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  frame.createFrame(ASCII, node_ID); // frame1 de  stocat
  frame.addSensor(SENSOR_BAT, PWR.getBatteryLevel());
  scriitor_SD(filename, ssent);

  cycle_time = cycle_time2 - b - 5;
  if (cycle_time < 10) {
    cycle_time = 15;
  }
  USB.println(cycle_time);

  x = cycle_time % 60; // sec
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  rtc_str[9] = y[0];
  rtc_str[10] = y[1];

  x = cycle_time / 60 % 60; // min
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  rtc_str[6] = y[0];
  rtc_str[7] = y[1];

  x = cycle_time / 3600 % 3600; // h
  itoa(x, y, 10);
  if (x < 10) {
    y[1] = y[0];
    y[0] = '0';
  }
  rtc_str[3] = y[0];
  rtc_str[4] = y[1];

  ///-------------

  // Go to deepsleep

  ////////////////////////////////////////////////
  // 5. Sleep
  ////////////////////////////////////////////////
  USB.println(F("5. Enter deep sleep..."));
  USB.print("X");
  USB.print(rtc_str);
  USB.println("X");

  USB.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
              "|||||||||||||||");
  USB.OFF();
  PWR.deepSleep(rtc_str, RTC_OFFSET, RTC_ALM1_MODE1, ALL_OFF);
  USB.ON();
  USB.println(
      F("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
        "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
  USB.println(F("6. Wake up!!\n\n"));
}
