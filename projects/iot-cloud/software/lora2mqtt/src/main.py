import json
import logging
import os
import sys
import atexit

import paho.mqtt.publish as publish
import requests
from apscheduler.schedulers.background import BackgroundScheduler
from flask import Flask
from flask import jsonify
from flask import request
from flatten_json import flatten

from utils.exceptions import RestException
from utils.functions import resolve_mapping

application = Flask(__name__)

CONFIG_URL = os.environ.get('CONFIG_URL',
                            'https://gist.githubusercontent.com/dorinelfilip/6743182de240959b2a723fcf232b8496/raw/'
                            'f37c2be3e3e5f6abc54b16f14b7643ce0ab39d42/lora2mqtt.json')

MQTT_HOST = os.environ.get('MQTT_HOST', 'mqtt.beia-telemetrie.ro')
MQTT_PORT = os.environ.get('MQTT_PORT', 1883)


@application.route('/')
def hello_world():
    return jsonify({'result': 'hello world'})


@application.route('/publish', methods=['POST'])
def publish_data():
    data = request.get_json(silent=True)
    if not data:
        raise RestException(400, "Bad request", "Payload is not a valid JSON.")

    try:
        metrics = data['payload_fields']
        metic_time = data['metadata']['time']
        app_id = data['app_id']
        dev_id = data['dev_id']
    except KeyError:
        raise RestException(400, 'Bad request', 'Mandatory key is missing from the JSON.')

    authorization = request.headers.get('authorization')
    if not authorization:
        raise RestException(401, 'Not authorized', 'token authorization HTTP header is missing.')

    mqtt_topic = resolve_mapping(application.config['mapping']['applications'], app_id, dev_id, authorization)

    result = flatten(metrics)
    result['timestamp'] = metic_time
    payload = json.dumps(result)

    application.logger.info('New topic: {}\nPayload: {}'.format(mqtt_topic, payload))

    publish.single(mqtt_topic, hostname=MQTT_HOST, port=MQTT_PORT, payload=payload, qos=2)

    return jsonify({'status': 'ok'})


@application.before_first_request
def init_app():
    application.logger.setLevel(logging.INFO)
    application.logger.info('Initializing application')
    __print_environment_config()
    application.logger.info('Downloading config')
    downloaded_config = __get_config(silent=True)
    if downloaded_config:
        application.config['mapping'] = downloaded_config
    else:
        application.logger.error('Failed to download config.')
        sys.exit(-1)

    application.sched = scheduler = BackgroundScheduler(timezone='UTC')

    @scheduler.scheduled_job('interval', seconds=300)
    def refresh_config():
        application.logger.info('Running periodic config refresh')
        mew_config = __get_config(silent=True)
        if not mew_config:
            application.logger.warning('Failed to pull new config file')
        else:
            application.logger.info("New config loaded. Content {}".format(application.config['mapping']))
            application.config['mapping'] = mew_config

    scheduler.start()
    atexit.register(lambda: clean_app(application))


def __get_config(silent=False, timeout=5):
    """
    Uses requests to get the new config. Based on the silent parameter, it returns None or raise an exception
    if an error occures.
    :param silent: if this parameter is left False, when requests specific exeptions are raised. If is is false,
    when the Exception becomes silent and None is returned in case of error
    :return: the decoded config. None if there is an error.
    """
    try:
        r = requests.get(CONFIG_URL, timeout=timeout)
        return r.json()
    except Exception as e:
        if not silent:
            raise e


@application.errorhandler(RestException)
def rest_exception_handler(e):
    return __create_json_response_from_dict(e.http_code, e.get_as_dict())


@application.errorhandler(500)
def internal_error_handler(e):
    # That comes to be strange, but raise RestException won't work.
    application.logger.exception('Internal Server Error')
    return __create_json_response_from_dict(500, RestException(500, 'Internal Server Error').get_as_dict())


def __create_json_response_from_dict(http_status_code, dictionary):
    return application.response_class(
        response=json.dumps(dictionary),
        status=http_status_code,
        mimetype='application/json',
    )


def __print_environment_config():
    message = ('{separator}\n'
               'Application environment\n'
               '{separator}\n'
               'MQTT_HOST: {mqtt_host}\n'
               'MQTT_PORT: {mqtt_port}\n'
               'CONFIG_URL: {config_url}\n'
               '{separator}'.format(separator=20 * '*', mqtt_host=MQTT_HOST, mqtt_port=MQTT_PORT,
                                    config_url=CONFIG_URL))

    application.logger.info(message)


def clean_app(app):
    app.sched.shutdown(wait=False)


if __name__ == '__main__':
    application.run(host='0.0.0.0', debug=False, port=80)
