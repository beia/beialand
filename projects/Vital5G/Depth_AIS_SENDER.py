import serial
import time
import paho.mqtt.client as mqtt
import json
from pyais.stream import FileReaderStream
import datetime

DEBUGG = True

mqttBroker = 'mqtt.beia-telemetrie.ro'
port = 1883
topicDEP  = "training/vital5g/DST-2/depth"
topicAIS = "training/vital5g/galati_AIS"

client_id = "NUC-navrom"
client = mqtt.Client(client_id)
client.connect(mqttBroker)

print("\nI'm alive, DEBUGG = " + str(DEBUGG))

if DEBUGG:
    print("connected to Beia mqtt broker ok") 

def GetAISData():
    
    current_time = datetime.datetime.now()
    
    if DEBUGG:
        print("\nGetting AIS data...")
    
    AISmessage = "no data"
    
    #connecting to serial port for AIS data
    try:
        serialPortAIS = serial.Serial(    port="COM7", baudrate=38400, bytesize=8, timeout=2, stopbits=serial.STOPBITS_ONE)
        
        if DEBUGG:
            print("   connected to serial port for AIS data")
            
        time.sleep(1)
        
        if serialPortAIS.in_waiting > 0:
            
            if DEBUGG:
                print("   found data in buffer")
                
            # Read data out of the buffer until a carraige return / new line is found
            serialStringAIS = serialPortAIS.readline()
            AISmessage = serialStringAIS.decode("Ascii")
            
            if DEBUGG:
                print("   message: " + AISmessage)
            
            # Print the contents of the serial data
            
            try:
                stor = str(current_time) + ": " + str(AISmessage)
                f = open("aisdata.txt", "a")
                f.write(stor)
                f.close()
                
                if DEBUGG:
                    print("   stored message locally"),
                    
            except:
                if DEBUGG:
                    print("   failed to store data locally") 
        else:
            if DEBUGG:
                print("   no data in buffer")
            
    except:
        if DEBUGG:
            print("   failed to connect to serial port for AIS data")
        
    finally:
            serialPortAIS.close()

    return AISmessage


def GetDEPData():
    
    current_time = datetime.datetime.now()
    
    if DEBUGG:
        print("\nGetting depth data...")
    
    DepthMessage = -1
    
    #connecting to serial port for AIS data
    try:
        serialPortDEP = serial.Serial(    port="COM8", baudrate=4800, bytesize=8, timeout=2, stopbits=serial.STOPBITS_ONE)
        
        if DEBUGG:
            print("   connected to serial port for depth data")
        
        time.sleep(1)
        
        if serialPortDEP.in_waiting > 0:
            
            if DEBUGG:
                print("   found data in buffer")
                
            # Read data out of the buffer until a carraige return / new line is found
            serialStringDEP = serialPortDEP.readline()
            DEPmessage = serialStringDEP.decode("Ascii")
            
            if DEBUGG:
             print("   message: " + DEPmessage)
            
            # Print the contents of the serial data
            
            try:
                stor = str(current_time) + ": " + str(DEPmessage)
                f = open("depthdata.txt", "a")
                f.write(stor)
                f.close()
                
                if DEBUGG:
                    print("   stored message locally"),
                
            except:
                if DEBUGG:
                    print("   failed to store data locally") 
                    
        else:
            if DEBUGG:
                print("   no data in buffer")
                 
    except:
        if DEBUGG:
            print("   failed to connect to serial port for depth data")
        
    finally:
        serialPortDEP.close()
        
    splitMessage = DEPmessage.split(',')
    
    try:
        if splitMessage[0] == "$SDDPT":
            DepthMessage = float(splitMessage[1])
    except:
        if DEBUGG:
            print("Unknown message format")
        else:
            pass

    return DepthMessage

while True:
    print("\n-----------New loop for getting data-----------\n")
    
    AISData = GetAISData()
    DepthData = GetDEPData()
    
    print("\nAIS data: " + AISData)
    print("Depth data: " + str(DepthData))
    
    payloadAIS = AISData
    client.publish(topicAIS, payloadAIS)
    
    DepthDict = {"id":"GalatiPonton1", "value":DepthData }
    payloadDEP = json.dumps(DepthDict)
    client.publish(topicDEP, payloadDEP)
            
    print("\n------------------End of loop------------------\n")
    print("Waiting 10 seconds before new loop...")
    time.sleep(10)
