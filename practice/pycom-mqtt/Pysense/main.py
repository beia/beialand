from network import WLAN
from mqtt import MQTTClient
import machine
# from machine import Pin, Timer
import ujson
import time
# import gc
import ubinascii
# from deepsleep import DeepSleep
# import deepsleep
from pysense import Pysense
from LIS2HH12 import LIS2HH12
from SI7006A20 import SI7006A20
from LTR329ALS01 import LTR329ALS01
from MPL3115A2 import MPL3115A2,ALTITUDE,PRESSURE


# ds = DeepSleep()

# Wireless network
WIFI_SSID = "LANCOMBEIA"
WIFI_PASS = "beialancom"

#  Use on-board sensors
py = Pysense()
mp = MPL3115A2(py,mode=ALTITUDE) # Returns height in meters.
mpp = MPL3115A2(py,mode=PRESSURE) # Returns pressure in Pa.
si = SI7006A20(py) # Returns temperature in deg C and relative humidity RH
lt = LTR329ALS01(py) # Returns blue and red light intensity in lux
# li = LIS2HH12(py)

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


while True:
     temperature = mp.temperature()
     altitude = mp.altitude()
     pressure = mpp.pressure()
     humidity = si.humidity()
     light = lt.light()
     datas = {'temperature': temperature,'altitude': altitude,'pressure': pressure, 'humidity': humidity,'blue light': light[0],'red light': light[1] }
     datas_json = ujson.dumps(datas)
     client.publish("citisim/pycom/ST2", datas_json)
     print("Sending datas : temperature, altitude, pressure, humidity and light intensity")
     print('{},{},{},{},{},{}'.format(temperature,altitude,pressure,humidity,light[0],light[1]))

    #  Deepsleep during 300s
    #  ds.go_to_sleep(300)
     time.sleep(300)
