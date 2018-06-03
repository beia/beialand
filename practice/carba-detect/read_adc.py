#!/usr/bin/env python
import spidev
import time
import math
import os
import statsd

spi = spidev.SpiDev()
spi.open(0,0)
client = statsd.StatsClient(host='docker-master.beia-consult.ro', port=8125)


def ReadInput(channel):
    adc = spi.xfer2([6|(channel&4)>>2,(channel&3)<<6,0])
    data = ((adc[1]&15)<<8)+adc[2]
    return data

def ConvertVolts(data,places):
    volts = (data*5)/float(4095)
    volts = round(volts,places)
    return volts

voltage_input = 0

delay = 2

while True:

    val = 0
    counter = 0
    previous = time.time()
    while (time.time()-previous) < 0.04:
        val = val + ReadInput(voltage_input)
        counter = counter + 1
        
    mean = val/counter
    voltage = ConvertVolts(mean,3) 
    print ("Voltage: {}V".format(voltage))
    # msg = "Voltage is {}".format(voltage)
    client.gauge("beia.raspberry.voltage",voltage)
    # print(msg)


    time.sleep(delay)
