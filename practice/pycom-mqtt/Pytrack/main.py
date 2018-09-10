from network import WLAN
from mqtt import MQTTClient
import machine
#from machine import Pin, Timer
import ujson
import time
#import gc
import ubinascii
#from deepsleep import DeepSleep
import deepsleep
from LIS2HH12 import LIS2HH12
from pytrack import Pytrack

ds = DeepSleep()

# Wireless network
WIFI_SSID = "LANCOMBEIA"
WIFI_PASS = "beialancom"

#  Use on-board accelerometer
py = Pytrack()
acc = LIS2HH12()

# MQTT configuration
AIO_SERVER = "mqtt.beia-telemetrie.ro"
AIO_PORT = 1883
AIO_CLIENT_ID = ubinascii.hexlify(machine.unique_id()) # Can be anything

wlan = WLAN(mode=WLAN.STA)
wlan.connect(WIFI_SSID, auth=(WLAN.WPA2, WIFI_PASS), timeout=5000)

while not wlan.isconnected():
     machine.idle()

print("Connected to Wifi\n")
client = MQTTClient(AIO_CLIENT_ID, AIO_SERVER, AIO_PORT)
client.connect()

# get the wake reason and the value of the pins during wake up
#print(wake_s)

#if wake_s['wake'] == deepsleep.PIN_WAKE:
#    print("Pin wake up")
#elif wake_s['wake'] == deepsleep.TIMER_WAKE:
#    print("Timer wake up")
#else:  # deepsleep.POWER_ON_WAKE:
#    print("Power ON reset")


while True:
     acceleration = acc.acceleration()
     pitch = acc.pitch()
     roll = acc.roll()
     datas = {'accelerometer_x': acceleration[0],'accelerometer_y': acceleration[1],'accelerometer_z': acceleration[2], 'pitch': pitch,'roll': roll}
     datas_json = ujson.dumps(datas)
     client.publish("citisim/pycom/ST1", datas_json)
     print("Sending datas : acceleration[x,y,z], pitch and roll :")
     print('{},{},{}'.format(acceleration,pitch, roll))
     ds.go_to_sleep(300)
