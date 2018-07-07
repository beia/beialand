#!/bin/bash

NETWORK_NAME=${NETWORK_NAME:-traefik_net}
DOCKER=${DOCKER:-docker}

if [ ! "$(${DOCKER} network ls --filter name=${NETWORK_NAME} -q)" ];then
	echo "Creating network"
	${DOCKER} network create --driver overlay --opt encrypted --attachable --scope "swarm" ${NETWORK_NAME}
else
	echo "Network was already created"
fi
