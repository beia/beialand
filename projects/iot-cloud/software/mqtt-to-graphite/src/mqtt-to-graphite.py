#!/usr/bin/env python

__author__ = "Jan-Piet Mens"
__copyright__ = "Copyright (C) 2013 by Jan-Piet Mens"

import datetime
import json
import logging
import os
import signal
import socket
import sys
import time

import dateutil.parser as date_parser
import paho.mqtt.client as paho

MQTT_HOST = os.environ.get('MQTT_HOST', '127.0.0.1')
MQTT_PORT = int(os.environ.get('MQTT_PORT', 1883))
CARBON_SERVER = os.environ.get('CARBON_SERVER', '127.0.0.1')
CARBON_PORT = int(os.environ.get('CARBON_PORT', 2003))
CARBON_PREFIX = os.environ.get('CARBON_PREFIX', "mqtt")

LOGFORMAT = '%(asctime)-15s %(message)s'

DEBUG = os.environ.get('DEBUG', True)
if DEBUG:
    logging.basicConfig(level=logging.DEBUG, format=LOGFORMAT)
else:
    logging.basicConfig(level=logging.INFO, format=LOGFORMAT)

client_id = "MQTT2Graphite_%d-%s" % (os.getpid(), socket.getfqdn())


def cleanup(signum, frame):
    '''Disconnect cleanly on SIGTERM or SIGINT'''

    mqttc.publish("/clients/" + client_id, "Offline")
    mqttc.disconnect()
    logging.info("Disconnected from broker; exiting on signal %d", signum)
    sys.exit(signum)


def is_number(s):
    '''Test whether string contains a number (leading/traling white-space is ok)'''

    try:
        float(s)
        return True
    except ValueError:
        return False


def on_connect(mosq, userdata, flags, rc):
    logging.info("Connected to broker at %s as %s" % (MQTT_HOST, client_id))

    mqttc.publish("/clients/" + client_id, "Online")

    map = userdata['map']
    for topic in map:
        logging.debug("Subscribing to topic %s" % topic)
        mqttc.subscribe(topic, 0)


def on_message(mosq, userdata, msg):
    sock = userdata['sock']
    lines = []
    now = datetime.datetime.now()

    map = userdata['map']
    # Find out how to handle the topic in this message: slurp through
    # our map
    for t in map:
        if paho.topic_matches_sub(t, msg.topic):
            # print "%s matches MAP(%s) => %s" % (msg.topic, t, map[t])

            # Must we rename the received msg topic into a different
            # name for Carbon? In any case, replace MQTT slashes (/)
            # by Carbon periods (.)
            (type, remap) = map[t]
            if remap is None:
                carbonkey = msg.topic.replace('/', '.')
            else:
                if '#' in remap:
                    remap = remap.replace('#', msg.topic.replace('/', '.'))
                carbonkey = remap.replace('/', '.')

            if len(CARBON_PREFIX) > 0:
                carbonkey = "{prefix}.{old_key}".format(prefix=CARBON_PREFIX,
                                                        old_key=carbonkey)

            logging.debug("CARBONKEY is [%s]" % carbonkey)

            if type == 'n':
                '''Number: obtain a float from the payload'''
                try:
                    number = float(msg.payload)
                    lines.append("%s %f %d" % (carbonkey, number, int(now)))
                except ValueError:
                    logging.info("Topic %s contains non-numeric payload [%s]" %
                                 (msg.topic, msg.payload))
                    return

            elif type == 'j':
                '''JSON: try and load the JSON string from payload and use
                   subkeys to pass to Carbon'''
                try:
                    data_time = now
                    st = json.loads(msg.payload)
                    if 'timestamp' in st:
                        try:
                            timestamp = st['timestamp']
                            data_time = date_parser.parse(timestamp)
                            logging.info('Extracted date {data_time} from timestamp "{timestamp}"'.format(
                                data_time=str(data_time), timestamp=timestamp))

                        except ValueError:
                            # Invalid content of timestamp
                            logging.info('Failed to extract date-time from "{}"'.format(timestamp))
                            pass
                        except OverflowError:
                            logging.info('Failed to extract date-time from "{}"'.format(timestamp))
                            pass

                    for k in st:
                        if is_number(st[k]) and k != 'id':
                            lines.append("%s.%s %f %d" % (carbonkey, k, float(st[k]), data_time.timestamp()))
                except Exception as e:
                    logging.info("Topic %s -  there was an error parsing this payload as compliant JSON [%s]" %
                                 (msg.topic, msg.payload))
                    logging.debug(e)
                    return

            else:
                logging.info("Unknown mapping key [%s]", type)
                return

            message = '\n'.join(lines) + '\n'
            logging.debug("%s", message)

            sock.send(message.encode())


def on_subscribe(mosq, userdata, mid, granted_qos):
    pass


def on_disconnect(mosq, userdata, rc):
    if rc == 0:
        logging.info("Clean disconnection")
    else:
        logging.info("Unexpected disconnect (rc %s); reconnecting in 5 seconds" % rc)
        time.sleep(5)


def main():
    logging.info("Starting %s" % client_id)
    logging.info("INFO MODE")
    logging.debug("DEBUG MODE")

    map = {}
    if len(sys.argv) > 1:
        map_file = sys.argv[1]
    else:
        map_file = 'map'

    f = open(map_file)
    for line in f.readlines():
        line = line.rstrip()
        if len(line) == 0 or line[0] == '#':
            continue
        remap = None
        try:
            type, topic, remap = line.split()
        except ValueError:
            type, topic = line.split()

        map[topic] = (type, remap)

    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    except:
        sys.stderr.write("Can't create TCP socket\n")
        sys.exit(1)

    try:
        sock.connect((CARBON_SERVER, CARBON_PORT))
    except:
        sys.stderr.write("Failed to connect to Graphite")
        sys.exit(-1)

    userdata = {
        'sock': sock,
        'map': map,
    }
    global mqttc
    mqttc = paho.Client(client_id, clean_session=True, userdata=userdata)
    mqttc.on_message = on_message
    mqttc.on_connect = on_connect
    mqttc.on_disconnect = on_disconnect
    mqttc.on_subscribe = on_subscribe

    mqttc.will_set("clients/" + client_id, payload="Adios!", qos=0, retain=False)

    mqttc.connect(MQTT_HOST, MQTT_PORT, 60)

    signal.signal(signal.SIGTERM, cleanup)
    signal.signal(signal.SIGINT, cleanup)

    mqttc.loop_forever()


if __name__ == '__main__':
    main()

