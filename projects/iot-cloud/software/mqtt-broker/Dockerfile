FROM node:alpine
COPY src/ /srv/mqtt-broker
RUN cd /srv/mqtt-broker && yarn install
CMD node /srv/mqtt-broker
