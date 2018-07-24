#!/bin/bash

SECRET_NAME=$1
SECRET_VALUE=${2:-$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)}
DOCKER=${DOCKER:-docker}

if [ ! "${SECRET_NAME}" ]; then
  exit 0
fi

if [ "$(${DOCKER} secret ls --filter name=${SECRET_NAME} -q)" ];then
  echo "Secret is already present"
  exit 0
fi

if [ "$2" ]; then
  echo "Creating secret ${SECRET_NAME}"
else
  echo "Creating secret ${SECRET_NAME}=${SECRET_VALUE}"
fi
echo ${SECRET_VALUE} | ${DOCKER} secret create ${SECRET_NAME} -
