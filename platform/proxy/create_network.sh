#!/bin/bash

NETWORK_NAME=traefik_net

if [ ! "$(docker network ls --filter name=${NETWORK_NAME} -q)" ];then
	echo "Creating network"
	docker network create --driver overlay --opt encrypted --attachable --scope "swarm" ${NETWORK_NAME}
else
	echo "Network was already created"
fi
