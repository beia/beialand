const mqtt = require('mqtt');
const logger = require('./logger')
const blockchainCommunicator = require('./blockchain-communicator')
const isNumber = require('is-number');
const Influx = require('influx')

const influx= new Influx.InfluxDB({
    host: 'influxdb',
    database: 'statistics',
    port:8086
    });
    module.exports=influx;

var client  = mqtt.connect('mqtt://mqtt.beia-telemetrie.ro')

client.on('connect', async () => {
    client.subscribe('training/#', function (err) {
        if (err) {
            logger.error(err)
        }
    })
}) 

client.on('message', function (topic, message) {
    try {
        const dict = JSON.parse(message);
        
        const [_, device, candidate] = topic.split("/")
        const field_dict = {}
        const all_fields = []

        logger.log("Received a message from device " + device + " from candidate " + candidate)
        
        if ("timestamp" in dict) {
            field_dict["timestamp"] = new Date(dict["timestamp"]).getTime() * 1000000;
            logger.log("Data timestamp is " + dict["timestamp"])
        }
        else {
            field_dict["timestamp"] = new Date().getTime() * 1000000;
            logger.log("Data timestamp is NOW")  
        }

        for (const [key, value] of Object.entries(dict)) {
            if (isNumber(value)) {
                field_dict["measurement"] = device + "." + key
                field_dict["fields"] = {values : value}
                all_fields.push(field_dict)

                logger.log(device + "." + key + " " + value)
            }
        }

        const fieldsJsonString = JSON.stringify(all_fields)
        const base64JSON = Buffer.from(fieldsJsonString).toString('base64')
        blockchainCommunicator.storeData(base64JSON)
        
        influx.writePoints(all_fields)
    }
    catch (err) {
        logger.error(err)
    }
})
