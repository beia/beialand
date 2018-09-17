import time
import pycom
import ujson
import machine

from pytrack import Pytrack
from LIS2HH12 import LIS2HH12
from L76GNSS import L76GNSS
from network import WLAN
from mqtt import MQTTClient
from ubinascii import hexlify

SLEEP_INTERVAL = 60000

SSID = 'LANCOMBEIA'
WIFI_PASSWORD = 'changeme'
WIFI_SECURITY = WLAN.WPA2

MQTT_SERVER = 'mqtt.beia-telemetrie.ro'
MQTT_PORT = 1883
MQTT_TOPIC = 'citisim/pycom/ST1'

# Disable heartbeat LED
pycom.heartbeat(False)

wlan = WLAN(mode=WLAN.STA)

pycom.rgbled(0xFF8C00)

wlan.connect(ssid=SSID, auth=(WIFI_SECURITY, WIFI_PASSWORD))
print('Connecting to Wifi')

while not wlan.isconnected():
    machine.idle()

print('Wifi connection succeded!')

pycom.rgbled(0xFF00FF)
print('Connecting to MQTT...\n')
client_id = 'pytrack_{}'.format(hexlify(wlan.mac()).decode('utf-8'))
mqtt_client = MQTTClient(client_id, MQTT_SERVER, port=MQTT_PORT)
mqtt_client.connect()
print('Connected to MQTT as {}'.format(client_id))

pycom.rgbled(0x001400)


py = Pytrack()
li = LIS2HH12(py)

# after 240 seconds of waiting without a GPS fix it will
# return None, None
gnss = L76GNSS(py, timeout=240)

while True:
    pycom.rgbled(0x000014)

    print('\n\n** 3-Axis Accelerometer (LIS2HH12)')
    acceleration = li.acceleration()
    print('Acceleration', acceleration)
    roll = li.roll()
    print('Roll', roll)
    pitch = li.pitch()
    print('Pitch', pitch)

    payload = {
    'accelerometer_x': acceleration[0],
    'accelerometer_y': acceleration[1],
    'accelerometer_z': acceleration[2],
    'pitch': pitch,
    'roll': roll}

    print('\n\n** GPS (L76GNSS)')
    loc = gnss.coordinates()
    if loc[0] == None or loc[1] == None:
        print('No GPS fix within configured timeout :-(')
    else:
        print('Latitude', loc[0])
        payload['latitude'] = loc[0]
        print('Longitude', loc[1])
        payload['longitude'] = loc[1]

    print('Seding MQTT message...\n')
    mqtt_client.publish(MQTT_TOPIC, ujson.dumps(payload))

    pycom.rgbled(0x001400)

    print('Sleeping for {} seconds.\n'.format(SLEEP_INTERVAL))
    time.sleep(SLEEP_INTERVAL)
