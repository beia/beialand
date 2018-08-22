#!/bin/sh
set -xe
ROOT_FOLDER=/srv/beialand
GIT=${GIT:-git}
MAKE=${MAKE:-make}

cd ${ROOT_FOLDER}
${GIT} reset --hard
${GIT} clean -df
${GIT} checkout -B ${BRANCH:-master} origin/${BRANCH:-master}
${GIT} pull --rebase

ENV=${ENV:-production} ${MAKE} -f platform/makefiles/Makefile.server server_deploy
