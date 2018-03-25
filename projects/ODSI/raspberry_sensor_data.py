#!/usr/bin/env python
from sense_hat import SenseHat
import time
from statsd import StatsClient
sense = SenseHat()

client = StatsClient(host="docker-master.beia-consult.ro", port=8125)
while True:
    t = sense.get_temperature()
    p = sense.get_pressure()
    h = sense.get_humidity()

    t = round(t, 1)
    p = round(p, 1)
    h = round(h, 1)

    msg = "Temperature = {0}, Pressure = {1}, Humidity = {2}".format(t,p,h)
    
    client.gauge("beia.raspberry.temperature", t)
    client.gauge("beia.raspberry.pressure", p)
    client.gauge("beia.raspberry.humidity", h)
    print (msg)
    time.sleep(15)
