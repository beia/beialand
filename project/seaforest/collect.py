import time
while 1:
    tempfile = open ("/sys/bus/w1/devices/28-0516a1cc0cff/w1_slave")
    thetext = tempfile.read()
    tempfile.close()
    tempdate = thetext.split("\n")[1].split(" ")[9]
    temperature = float(tempdate[2:])
    temperature = temperature /1000
    print (temperature)
    
    time.sleep(10)
