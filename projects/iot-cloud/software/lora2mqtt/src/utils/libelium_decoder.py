def decode_libelium(ttn_json):
    authorization_header = ttn_json.headers['authorization']
    if authorization_header == "beiawasp29032021":
        APP_ID, params_GW = overtake_metadata(ttn_json)
        payload_B=turn_payload_to_bytes(ttn_json)
        start_payload_decoded = base64.b64decode(payload_B)[0:3].decode()
        print(start_payload_decoded)
        log.info('Payload decoded:' + str(start_payload_decoded))

        if start_payload_decoded == "<=>":
            print("Start corect")
            log.info('Correct payload')
            device_id, key_values = decode_payload(payload_B)
            new_key_values = dict(key_values, **params_GW)
            print(new_key_values)
            mqtt_payload = json.dumps(key_values)
            return mqtt_payload
        else:
            log.error('Incorrect payload')
            # return sanic.response.json({'status': 'invalid payload'})
            return None

def turn_payload_to_bytes(request):
    # payload-bytes
    payload_str = request.json['payload_raw']
    payload_bytes = bytes(payload_str, 'ascii')
    return payload_bytes

def decode_payload(payload_bytes):
    payload_decoded = base64.b64decode(payload_bytes)[5:].decode()
    # print(payload_decoded.split('#'))
    token_list = payload_decoded.split('#')
    device_id = token_list[2]
    print(device_id)
    key_values = {t.split(':')[0]: t.split(':')[1] for t in token_list if len(t.split(':')) == 2}
    print(key_values)
    return device_id, key_values

def overtake_metadata(request):
    # chestii relevante (RSSI, ID gateway...) -- nu prea multe
    METADATE = request.json['metadata']
    APP_ID = request.json['app_id']
    print(METADATE)
    print("////////////////")
    # print(APP_ID)
    GATEWAYS = METADATE['gateways']
    dictfilt = lambda x, y: dict([(i, x[i]) for i in x if i in set(y)])
    wanted_keys = ('gtw_id', 'snr', 'rssi')
    print(range(len(GATEWAYS)))
    for i in range(len(GATEWAYS)):
        gw_dict = GATEWAYS[i]
        params = dictfilt(gw_dict, wanted_keys)
        if params['gtw_id'] == GW_ID_BEAM:
            params_our_GW = params
            print('Parameters of our gateway:')
            log.info('Parameters of our gateway:'+params)
            print(params)
        else:
            print('Parameters of other gateway:')
            log.info('Parameters of other gateway:' + params)
            print(params)
    return APP_ID, params_our_GW





