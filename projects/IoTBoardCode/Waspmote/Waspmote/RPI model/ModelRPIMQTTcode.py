from sense_hat import SenseHat
import time
import json
import paho.mqtt.publish as publish
import math

MQTT_HOST = 'vitalahtbroker.cloud.reply.eu'
MQTT_TOPIC = 'topic/temperature'

sense = SenseHat()

while True:   
    payload_dict = {"Site_ID": "Beia_01",
                    "Sensor_ID": "Temp_sensehat_01",
                    "Protoc_Type" : "MQTT",
                    "Sensor_Name": "Temperature_Sensor",
                    "Meas_Unit": "C",
                    "Latitude" : 44.395643,    
                    "Longitude": 26.102910,
                    "Meas_Timestamp": int(round(time.time())),
                    "Meas_Value": sense.get_temperature()}
    
    try:
        publish.single(MQTT_TOPIC, qos = 1, hostname = MQTT_HOST, payload = json.dumps(payload_dict), port=80)
    except:
        time.sleep(0.01) 
       
    time.sleep(600)