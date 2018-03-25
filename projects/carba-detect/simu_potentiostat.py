#!/usr/bin/env python
import spidev
import math
import os
from time import time
from time import sleep

spi = spidev.SpiDev()
spi.open(0,0)
simu_potentiostat = 0

def ReadInput(channel):
    adc = spi.xfer2([6|(channel&4)>>2,(channel&3)<<6,0])
    data = ((adc[1]&15)<<8)+adc[2]
    return data

def ConvertVolts(data,places):
    volts = (data*5)/float(4095)
    volts = round(volts,places)
    return volts

if __name__ == '__main__':
   
    t=3
    while t>0:
    
        previous=time()
        x=0
        first_counter=0
        while time()-previous<7:
            previous2=time()
            counter=0
            val=0
            while time()-previous2<0.02:
                val=val+ReadInput(simu_potentiostat)
                counter=counter+1
                mean=val/counter
            x=x+mean
            first_counter=first_counter+1
        first_mean=x/first_counter
       
        previous=time()
        y=0
        second_counter=0
        while time()-previous<3:
            previous2=time()
            val=0
            counter=0
            while time()-previous2<0.02:
                val=val+ReadInput(simu_potentiostat)
                counter=counter+1
                mean=val/counter
            y=y+mean
            second_counter=second_counter+1
            #carba_values_list.append(mean)
        second_mean=y/second_counter
        
        previous=time()
        z=0
        third_counter=0
        while time()-previous<3:
            previous2=time()
            val=0
            counter=0
            while time()-previous2<0.02:
                val=val+ReadInput(simu_potentiostat)
                counter=counter+1
                mean=val/counter
            z=z+mean
            third_counter=third_counter+1
        third_mean=z/third_counter
        
        if float(first_mean*2.2)<second_mean:
        	if float(second_mean*1.2)<third_mean:
        		print ("Carba detected")
        else:
            print ("No Carba detected")	
        t=t-1
        sleep(13)
