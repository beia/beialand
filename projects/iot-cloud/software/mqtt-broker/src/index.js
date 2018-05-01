'use strict';

let mosca = require('mosca');

const REDIS_PORT = process.env.REDIS_PORT || 6379;
const REDIS_HOST = process.env.REDIS_HOST || 'localhost';

console.log(`Redis connection to ${REDIS_HOST}:${REDIS_PORT}`);

let ascoltatore = {
  type: 'redis',
  redis: require('redis'),
  db: 12,
  port: REDIS_PORT,
  return_buffers: true, // to handle binary payloads
  host: REDIS_HOST,
};

let moscaSettings = {
  port: 1883,
  backend: ascoltatore,
  persistence: {
    factory: mosca.persistence.Redis,
    host: REDIS_HOST,
    port: REDIS_PORT,
  }
};

// fired when the mqtt server is ready
function setup() {
  console.log('Mosca server is up and running')
}

let server = new mosca.Server(moscaSettings);
server.on('ready', setup);

server.on('clientConnected', function(client) {
    console.log('client connected', client.id);
});

// fired when a message is received
server.on('published', function(packet, client) {
  console.log('Published', packet.topic, packet.payload);
});
