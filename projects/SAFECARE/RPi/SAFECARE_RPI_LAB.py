import spidev 
import os 
import math 
import time 
import pigpio 
import math 
import subprocess 
import Adafruit_DHT 
import json 
import RPi.GPIO as GPIO 
import paho.mqtt.publish as publicare 

MQTT_HOST=" "
MQTT_TOPIC=" "
DHT_SENSOR = Adafruit_DHT.DHT22 
DHT_PIN = 4 
Fan_pin = 20 
Sound_pin= 18 
maxTMP = 32 
spi=spidev.SpiDev() 
spi.open(0,0)  
spi.max_speed_hz=1000000 
logfile = "data.txt" 
def fanON(): 
    setPin(True) 
    return() 
def fanOFF(): 
    setPin(False) 
    return() 
def setPin(mode): 
    GPIO.output(Fan_pin, mode) 
    return() 
def ReadInput(channel): 
    adc = spi.xfer2([6|(channel&4)>>2, (channel&3)<<6,0]) 
    data = ((adc[1]&15)<<8)+adc[2] 
    return data 
def ConvertVolts (data, places):    
    volts = (data*3.3)/float(4095) 
    volts = round(volts,places) 
    return volts 
def local_save(data): 
    file = open(logfile, "a+") 
    file.write(data + "\r\n"); 
    file.close() 
def local_delete(): 
    file = open(logfile, "a+") 
    file.truncate(0); 
    file.close() 
    fire=0 
    fire1=0 
def setup(): 
    GPIO.setmode(GPIO.BCM) 
    GPIO.setwarnings(False) 
    GPIO.setup(Fan_pin, GPIO.OUT) 
    GPIO.setwarnings(False) 
    GPIO.setup(Sound_pin,GPIO.IN) 
    GPIO.setwarnings(False) 
    return() 
channel = 18 
GPIO.setmode(GPIO.BCM)  
GPIO.setup(channel, GPIO.IN) 
t=time.time()  
try: 
    setup() 
    while True: 
        cont=0 
        Flood=0 
        glass_break=0 
        channel = 0
        temp=0
        hum=0
        humidity, temperature = Adafruit_DHT.read_retry(DHT_SENSOR, DHT_PIN) 
        channeldata = ReadInput(channel) 
        light=ConvertVolts(ReadInput(3),2) 
        flood_level= ConvertVolts (ReadInput(0),2) 
        glass = ConvertVolts (ReadInput(1),2) 
        fire1 = ConvertVolts (ReadInput(4),2) 
        #print('Data        : {}'.format(channeldata)) 
        #print('flood_level(V): {}'.format(glass)) 
        #print("Light={0:0.1f} lux".format(light*1000)) 
        #print("Temp={0:0.1f}*C  Humidity={1:0.1f}%".format(temperature, humidity)) 
#        print (sound) 
        if (fire1<1): 
            fire=2 
        elif (fire1>=1): 
            fire=1 
        if (glass>=2.55): 
            glass_break=1 
            #print("Safe") 
        elif (glass<2.55): 
            glass_break=2 
            #print("Danger!") 
        if (flood_level>=0.5): 
            Flood=2 
        elif (flood_level<0.5): 
            Flood=1
        if temperature is not None:
            if temperature>maxTMP: 
                setup() 
                fanON() 
                z=2 
            elif temperature<maxTMP: 
                setup() 
                fanOFF() 
                z=1 
                GPIO.cleanup(20)
        if(GPIO.input(18)== True):
            #print ('Liniste')
            a = 2
        else:
            #print ('zgomot')
            a = 1 
        if temperature is not None:
            temp = temperature
            hum = humidity
        #temperature = round(temperature,2)
        payload_dict={"Temperature_in_degrees":round(temp,2),
                      "Humidity_level":round(hum,2), 
                      "Flood_detection":Flood, 
                      "Glass_glass_break" :glass_break, 
                      "Fire_detection" : fire, 
                      "Light_level" :(light*1000), 
                      "Sound_level" : a, 
                      "Fan_Power":z} 
        message = "Flood_detection" + str(Flood) + " Glass_glass_break  " + str(glass_break) + "Fire_detection" + str(fire) +"Light_level" +str(light*1000) +"Sound_level" +str(a) 
        print(payload_dict) 
        try: 
            publicare.single(MQTT_TOPIC,qos = 1,hostname = MQTT_HOST,payload = json.dumps(payload_dict)) 
        except: 
            GPIO.cleanup() 
            cont=cont+1 
        if (cont==1): 
            cont=0 
            local_save(message) 
        time.sleep(20) 
except KeyboardInterrupt:               
    GPIO.cleanup() 
