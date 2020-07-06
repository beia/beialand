import paho.mqtt.client as mqtt
import re
import numpy as np

#topic = 'meshliumfa30/WINSHI/#'
topic = 'meshliumfa30/SCP3/PM10/#'
# topic = 'meshliumfa30/Metrorex/PM10'

hours = [0., 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12., 13., 14., 15., 16., 17., 18., 19., 20., 21., 22., 23.]
lista_ore = []
import torch



clf = torch.load("model_large_dataset3")

def on_connect(client, userdata, flags, rc):
    # print("Connect with Code: ", str(rc))
    #Subscribe Topic:
    client.subscribe(topic)

def on_message(client, userdata, msg):
    m = str(msg.payload)
    print(m)
    text = re.findall('"([^"]*)"', m) #selectarea string-urilor dintre ghilimele
    value = float(text[9])
    timestamp = text[11]
    hour = float(timestamp[11:13])
    print("value: ", value)
    print("Timestamp: ", timestamp)
    print("Hour: ", hour)

    if len(lista_ore) > 0:
        if lista_ore[-1][0] == hour:
            lista_ore.append([hour, np.float64(round(value))])
            print(lista_ore)
            print(hour, np.float64(round(value)), (hour + 1)%24)
        else:
            print("Aici!")
            l = []
            medie = np.mean([x[1] for x in lista_ore])
            print([lista_ore[-1][0], medie, hour])
            print([lista_ore[-1][0], float(round(medie)), hour])
            l.append(lista_ore[-1][0])
            l.append(float(round(medie)))
            l.append(hour)
            predict = clf.predict([l])
            print("Predicted mean value for ", hour, " hour: ", predict[0])
            lista_ore.clear()
            lista_ore.append([hour, round(value)])
            print(lista_ore)
    else:
        lista_ore.append([hour, round(value)])
        l = []
        l.append(hour)
        l.append(round(value))
        l.append((hour + 1)%24)
        l[1] = np.float64(l[1])
        print(l)
        pred = clf.predict([l])
        print("Predicted: ", pred[0])


client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("mqtt.beia-telemetrie.ro", 1883, 60)
client.loop_forever()