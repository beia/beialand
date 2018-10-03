import json
import logging
import sys
import time

import paho.mqtt.publish as publish
from pi_sht1x import sht1x

# import smbus


DATA_PIN = 13
CLOCK_PIN = 15
address = 0x48
A0 = 0x40
A1 = 0x41
A2 = 0x42
A3 = 0x43

MQTT_server = "mqtt.beia-telemetrie.ro"
MQTT_port = 1883
logger = logging.getLogger(__name__)


def main():
    # bus = smbus.SMBus(1)
    # GPIO.setwarnings(False)
    sensor = sht1x.SHT1x(DATA_PIN, CLOCK_PIN, logger=logger)
    while True:
        temperature = None
        # value = None
        # bus.write_byte(address,A0)
        try:
            temperature = sensor.read_temperature()
            # value = bus.read_byte(address)
            # print("AOUT: ",(value*3.3/255))

        except IOError as e:
            print(e)
            sys.exit(-1)

        print('Temperatura este {}'.format(temperature))

        topic = 'seaforest/raspberrypi/ST1'

        data = {'temp': temperature,
                # 'gas': value*3.3/255
                }

        payload = json.dumps(data)
        print("Temperatura este {0}".format(data))
        print("Topic: {0}".format(topic))
        print("Payload: {0}".format(payload))
        publish.single(topic, payload=payload,
                       hostname=MQTT_server, port=MQTT_port)
        time.sleep(300)


if __name__ == '__main__':
    main()

