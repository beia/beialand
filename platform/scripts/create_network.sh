#!/bin/bash

NETWORK_NAME=$1
DOCKER=${DOCKER:-docker}

if [ "$1" == "" ]; then
	exit 0
fi

if [ ! "$(${DOCKER} network ls --filter name=${NETWORK_NAME} -q)" ];then
	echo "Creating network"
	${DOCKER} network create --driver overlay --opt encrypted --attachable --scope "swarm" ${NETWORK_NAME}
else
	echo "Network was already created"
fi
