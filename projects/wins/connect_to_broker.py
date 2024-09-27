import paho.mqtt.client as mqtt
import time
import paho.mqtt.subscribe as subscribe
import re

MQTT_server = "mqtt.beia-telemetrie.ro"
MQTT_port = 1883



def on_message_print(client, userdata, message):
    print(str(message.payload))
    m = str(message.payload)
    text = re.findall('"([^"]*)"', m)
    value = text[9]
    timestamp = text[11]
    print("value: ", value)
    print("Timestamp: ", timestamp)
    print("Hour: ", timestamp[11:13])
    return value, timestamp

def main():
    topic = 'meshliumfa30/SCP3/PM10/#'
    while True:
        subscribe.callback(on_message_print,topic, hostname=MQTT_server, port=MQTT_port)
        value, timestamp = on_message_print()
        print(value, timestamp)
        # time.sleep(5)

if __name__ == '__main__':
    main()