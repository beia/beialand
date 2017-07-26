#!/usr/bin/env python
#-------------------------------------------------------------------------------
# Name:        MCP4921_sawtooth.py
# Purpose:     Output a sawtooth waveform
#-------------------------------------------------------------------------------

import spidev
from time import sleep
import math

spi_max_speed = 4 * 1000000 # 4 MHz
V_ref = 5 # 5V 
max_value = (2**12)-1 # 12 bits for the MCP 4921
CE = 1 # CE0 or CE1, select SPI device on bus

# setup and open an SPI channel
spi = spidev.SpiDev()
spi.open(0,CE)
spi.max_speed_hz = spi_max_speed

freq = 90
period = float(1/freq)
time_sleep =float((period/2)/max_value)

amplitude = 2.39
digital_amplitude = int(round(amplitude/V_ref*max_value))
#print (digital_amplitude)

def setOutput(val):
    # lowbyte has 8 data bits
    lowByte = val & 0b11111111
    # highbyte has control and 6 data bits
    highByte = ((val >> 8) & 0xff) | 0b0 << 7 | 0b0 << 6 | 0b1 << 5 | 0b1 << 4
    # by using spi.xfer2(), the CS is released after each block, transferring the
    # value to the output pin.
    spi.xfer2([highByte, lowByte])


try:
    while(True):
        # create a sawtooth ramp starting from 0 to V-ref in digital_amplitude steps
        for step in range(0,digital_amplitude):
            setOutput(step)
            sleep(time_sleep)
        while (step >= 0):
            setOutput(step)
            step = step -1
            sleep(time_sleep)



except (KeyboardInterrupt, Exception) as e:
    print(e)
    print ("Closing SPI channel")
    spi.close()

def main():
    pass

if __name__ == '__main__':
    main()

