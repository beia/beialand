from network import WLAN
from mqtt import MQTTClient
import machine
#from machine import Pin, Timer
import ujson
import time
import gc
import ubinascii
#from deepsleep import DeepSleep
import deepsleep

ds = DeepSleep()

# Wireless network
WIFI_SSID = "LANCOMBEIA"
WIFI_PASS = "beialancom"

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
#wake_s = ds.get_wake_status()
#print(wake_s)

#if wake_s['wake'] == deepsleep.PIN_WAKE:
#    print("Pin wake up")
#elif wake_s['wake'] == deepsleep.TIMER_WAKE:
#   print("Timer wake up")
#else:  # deepsleep.POWER_ON_WAKE:
#    print("Power ON reset")


while True:
     datas = {'Total memory': gc.mem_free()+gc.mem_alloc(),'Free memory': gc.mem_free(),'Allocated memory': gc.mem_alloc()}
     datas_json = ujson.dumps(datas)
     print("Sending datas : Total Memory , Free Memory, Allocated Memory")
     client.publish("citisim/pycom/Loic", datas_json)
     ds.go_to_sleep(60)
