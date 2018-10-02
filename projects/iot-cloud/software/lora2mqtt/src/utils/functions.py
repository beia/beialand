from utils.exceptions import RestException


def resolve_mapping(mapping_config, app_id, dev_id, auth_token):
    """
    Search the given device in the mapping are returns the key that should be used for publishing data to MQTT.
    If any requirement is not fit, when it returns a RestException.
    """
    app_dict = mapping_config.get(app_id)
    if not app_dict:
        raise RestException(400, 'Bad request', 'The application with app_id = {} is not registered.'.format(app_id))

    device_dict = app_dict['devices'].get(dev_id)
    if not device_dict:
        raise (400, 'Bad request', 'The device with dev_id = {} is not registered.'.format(dev_id))

    if auth_token not in app_dict['tokens']:
        raise (401, 'Unauthorized', 'The given token has no rights for this application.')

    return device_dict['mqtt_topic']
