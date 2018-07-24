#!/bin/bash

NETWORK_NAME=$1
DOCKER=${DOCKER:-docker}

if [ ! "${NETWORK_NAME}" ]; then
  exit 0
fi

if [ "$(${DOCKER} network ls --filter name=${NETWORK_NAME} -q)" ];then
  echo "Network was already created"
  exit 0
fi

echo "Creating network ${NETWORK_NAME}"
${DOCKER} network create --driver overlay --opt encrypted --attachable --scope "swarm" ${NETWORK_NAME}
