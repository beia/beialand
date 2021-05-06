#!/bin/bash

sudo apt-get update
sudo apt-get install mosquitto-clients -y
sudo cp ./mock_data_generator_daemon /etc/init.d/
sudo cp ./mock_data_generator_daemon.service /etc/systemd/system/

sudo systemctl --now enable mock_data_generator_daemon.service