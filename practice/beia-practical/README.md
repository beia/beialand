# IoT Data aggregator 

## Description
The program is a simple backend attached to a MQTT server listening to messages from IoT devices, creating a payload with the received data and posting it on a time-series database.


## Blockchain
To assure the data's integrity and authenticity, we store it on a smart contract deployed on the Ethereum Virtual Machine that only accepts transactions from the original contract deployer. 


## Setup
### Development-environment
To create a local blockchain, you can use `ganache`.

Installing ganache requires npm

    npm install -g ganache-cli

Ganache can be launched with a specified mnemonic to generate wallets. For development purposes, you can use

    ganache-cli --mnemonic "amateur meat segment know stamp limb mammal enhance essay hollow same merrynote"

Once the local blockchain is up and runnning, we need to deploy the contract using truffle.

    ../beia-practical/blockchain-contract/$ truffle deploy

The contract is going to get deployed and display it's address. 

`Network: UNKNOWN (id: 5777)
  DataSet: 0x85B174e3F8E168E084035EA0dfaB4De9a32Eb314`

The DAI_ADDRESS should then be added in docker-compose.yml file as a value for the environment variable DAI_ADDRESS `./docker-compose.yml:14`

The data is currently hosted on a local TSDB for development purposes.

To start the project, simply run `docker-compose up -d` in the root directory.

## IoT Config
At the moment, the project was only tested on Raspberry PIs, but the data generator should also work on other devices with systemd Debian-based linux distributions.

Under `iot-device/raspberry-pi` there is a `config.sh` script that creates a daemon that generates data every 10 minutes. The daemon is configured to auto-start at boot. 