# Description of LoRa2MQTT

The purpose of this component is to intercept a webhook integration from a TTN (The Things Network) application that uses Cayenne LPP format and turn it into a MQTT message which can be processed by our MQTT2InfluxDB adaptor.

# Authentication

In this architecture:
- each device get authenticated to the TTN application using LoRaWAN specific mechanisms
- the application integration authenticates to the LoRa2MQTT component through a token passed as the `authentication` HTTP header.

# Application configuration and deploy

- 2 deployment ENV vars (`MQTT_HOST` and `MQTT_PORT`) specify the MQTT broker to be used.

- The rest of the application configuration is specified as a JSON file which are downloaded from a HTTPS endpoint. That should be specified through the `CONFIG_URL` ENV variable.

The format for the config file is the following:
```
{
    "applications": {
        "appid1": {
            "devices": {
                "deviceid1": {
                    "mqtt_topic": "lora/deviceid1"
                },
                "deviceid2": {
                    "mqtt_topic": "lora/deviceid2"
                }
                ...
                "deviceidN": {
                    "mqtt_topic": "lora/deviceidN"
                }
            },
            "tokens": [
                "app1_token1",
                "app1_token2",
                ...
                "app1_tokenN"
            ]
        },
        ...
        "appidN": {
            "devices": {
                "deviceid1": {
                    "mqtt_topic": "lora/deviceid1"
                },
                "deviceid2": {
                    "mqtt_topic": "lora/deviceid2"
                }
                ...
                "deviceidN": {
                    "mqtt_topic": "lora/deviceidN"
                }
            },
            "tokens": [
                "app1_token1",
                "app1_token2",
                ...
                "app1_tokenN"
            ]
        }
    }
}
```

A valid example of configuration JSON might look as following:

```
{
    "applications": {
        "fakeapp1": {
            "devices": {
                "fakedevice2": {
                    "mqtt_topic": "lora/fakedevice2"
                },
                "fakedevice3": {
                    "mqtt_topic": "lora/fakedevice3"
                }
            },
            "tokens": [
                "replaceme",
                "otherpassowrd"
            ]
        },
        "pysensetest": {
            "devices": {
                "fakedevice1": {
                    "mqtt_topic": "lora/fakedevice1"
                },
                "lopy1": {
                    "mqtt_topic": "lora/lopy1"
                }
            },
            "tokens": [
                "changeme"
            ]
        }
    }
}
```

# Input payload format

The payload from the TTN webhook integration comes as a POST request for each data-set, in the following format:

```
{
  "app_id": "my-app-id",              // Same as in the topic
  "dev_id": "my-dev-id",              // Same as in the topic
  "hardware_serial": "0102030405060708", // In case of LoRaWAN: the DevEUI
  "port": 1,                          // LoRaWAN FPort
  "counter": 2,                       // LoRaWAN frame counter
  "is_retry": false,                  // Is set to true if this message is a retry (you could also detect this from the counter)
  "confirmed": false,                 // Is set to true if this message was a confirmed message
  "payload_raw": "AQIDBA==",          // Base64 encoded payload: [0x01, 0x02, 0x03, 0x04]
  "payload_fields": {},               // Object containing the results from the payload functions - left out when empty
  "metadata": {
    "time": "1970-01-01T00:00:00Z",   // Time when the server received the message
    "frequency": 868.1,               // Frequency at which the message was sent
    "modulation": "LORA",             // Modulation that was used - LORA or FSK
    "data_rate": "SF7BW125",          // Data rate that was used - if LORA modulation
    "bit_rate": 50000,                // Bit rate that was used - if FSK modulation
    "coding_rate": "4/5",             // Coding rate that was used
    "gateways": [
      {
        "gtw_id": "ttn-herengracht-ams", // EUI of the gateway
        "timestamp": 12345,              // Timestamp when the gateway received the message
        "time": "1970-01-01T00:00:00Z",  // Time when the gateway received the message - left out when gateway does not have synchronized time
        "channel": 0,                    // Channel where the gateway received the message
        "rssi": -25,                     // Signal strength of the received message
        "snr": 5,                        // Signal to noise ratio of the received message
        "rf_chain": 0,                   // RF chain where the gateway received the message
        "latitude": 52.1234,             // Latitude of the gateway reported in its status updates
        "longitude": 6.1234,             // Longitude of the gateway
        "altitude": 6                    // Altitude of the gateway
      },
      //...more if received by more gateways...
    ],
    "latitude": 52.2345,              // Latitude of the device
    "longitude": 6.2345,              // Longitude of the device
    "altitude": 2                     // Altitude of the device
  },
  "downlink_url": "https://integrations.thethingsnetwork.org/ttn-eu/api/v2/down/my-app-id/my-process-id?key=ttn-account-v2.secret"
}
```

The components only accesses the `payload_fields` subtree, relevant identifications (`app_id` and `dev_id`) and `time` metadata. Other members of the JSON can be omitted.

An example of JSON with content from `lopy1` device inside the `pysensetest` application is shown next:

```
{
  "app_id": "pysensetest",
  "dev_id": "lopy1",
  "hardware_serial": "70B3D54999D5EC92",
  "port": 2,
  "counter": 40,
  "payload_raw": "AXH/8gAMA8oBhgBP/7UAAAFlAA8CZQAKAWg3AWcBowFzJyUBiAAAAAAAAAAkVAJnAYw=",
  "payload_fields": {
    "accelerometer_1": {
      "x": -0.014,
      "y": 0.012,
      "z": 0.97
    },
    "barometric_pressure_1": 1002.1,
    "gps_1": {
      "altitude": 93,
      "latitude": 0,
      "longitude": 0
    },
    "gyrometer_1": {
      "x": 0.79,
      "y": -0.75,
      "z": 0
    },
    "luminosity_1": 15,
    "luminosity_2": 10,
    "relative_humidity_1": 27.5,
    "temperature_1": 41.9,
    "temperature_2": 39.6
  },
  "metadata": {
    "time": "2018-08-27T17:25:47.456674533Z",
    "frequency": 867.1,
    "modulation": "LORA",
    "data_rate": "SF7BW125",
    "coding_rate": "4/5",
    "gateways": [
      {
        "gtw_id": "eui-fcc23dfffe0e3050",
        "timestamp": 2445056915,
        "time": "2018-08-27T17:25:47.429954Z",
        "channel": 3,
        "rssi": -107,
        "snr": -0.2,
        "rf_chain": 0,
        "latitude": 44.394016,
        "longitude": 26.12401,
        "altitude": 100,
        "location_source": "registry"
      }
    ]
  },
  "downlink_url": "https://integrations.thethingsnetwork.org/ttn-eu/api/v2/down/pysensetest/1234?key=ttn-account-v2.2cxcxzczjdsa"
}
``` 

# MQTT output format

For the compatibility with the MQTT2InfluxDB component, the payload JSON is flatten and the timestamp gets specified as the timestamp key. Next we show the output for the given example input.

```
{
   "accelerometer_1_x":-0.014,
   "accelerometer_1_y":0.012,
   "accelerometer_1_z":0.97,
   "barometric_pressure_1":1002.1,
   "gps_1_altitude":93,
   "gps_1_latitude":0,
   "gps_1_longitude":0,
   "gyrometer_1_x":0.79,
   "gyrometer_1_y":-0.75,
   "gyrometer_1_z":0,
   "luminosity_1":15,
   "luminosity_2":10,
   "relative_humidity_1":27.5,
   "temperature_1":41.9,
   "temperature_2":39.6,
   "timestamp":"2018-08-27T17:25:47.456674533Z"
}
```
