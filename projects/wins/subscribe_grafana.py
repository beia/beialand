import paho.mqtt.client as mqtt
import re
import numpy as np

#topic = 'meshliumfa30/WINSHI/#'
#topic = 'odsi/raspberry/marina_barbu'
topic = 'meshliumfa30/SCP3/PM10/#'
# topic = 'meshliumfa30/Metrorex/PM10'

hours = [0., 1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12., 13., 14., 15., 16., 17., 18., 19., 20., 21., 22., 23.]
lista_ore = []
import torch



clf = torch.load("model_large_dataset2")

# l = []
# l.append(float(round(float(10))))
# l.append(float(round((59.8))))
#
# l.append(float(round(float(11))))
# l[1] = np.float64(l[1])
# print(l)
# pred = clf.predict(l)
# print(pred)

def on_connect(client, userdata, flags, rc):
    # print("Connect with Code: ", str(rc))
    #Subscribe Topic:
    client.subscribe(topic)

def on_message(client, userdata, msg):
    m = str(msg.payload)
    print(m)
    text = re.findall('"([^"]*)"', m) #selectarea string-urilor dintre ghilimele
    # print(text)
    value = float(text[9])
    timestamp = text[11]
    hour = float(timestamp[11:13])
    # print(hour)
    # print(type(value))
    print("value: ", value)
    print("Timestamp: ", timestamp)
    print("Hour: ", hour)

    if len(lista_ore) > 0:
        if lista_ore[-1][0] == hour:
            lista_ore.append([hour, np.float64(round(value))])
            print(lista_ore)
            print(hour, np.float64(round(value)), (hour + 1)%24)
            # pred = clf.predict([float(round(float(hour))), float(round(value)), float(round(float(hour))) + 1])
            # print("Predicted: ",pred)
        else:
            medie = np.mean([x[1] for x in lista_ore])
            print([lista_ore[-1][0], medie, hour])
            # print([float(round(float(lista_ore[-1][0]))), float(round(medie)), float(round(float(hour)))])
            # predict = clf.predict([float(lista_ore[-1][0]), float(round(mean)), float(hour)])
            predict = clf.predict([lista_ore[-1][0], float(round(medie)), hour])
            print("Predict: ", predict[0])
            lista_ore.clear()
            lista_ore.append([hour, round(value)])
            print(lista_ore)
    else:
        lista_ore.append([hour, round(value)])
        # print(lista_ore)
        l = []
        l.append(hour)
        l.append(round(value))
        l.append((hour + 1)%24)
        # l = [float(round(float(hour))), float(round(value)), float(round(float(hour))) + 1]
        # print(type(l[0]), type(l[1]), type(l[2]))
        l[1] = np.float64(l[1])
        # print(type(l[1]))
        print(l)
        #values = np.frombuffer(values, dtype=np.float64)
        # print(type(l[0]), type(l[1]), type(l[2]))
        pred = clf.predict([l])
        print("Predicted: ", pred[0])


client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("mqtt.beia-telemetrie.ro", 1883, 60)
client.loop_forever()