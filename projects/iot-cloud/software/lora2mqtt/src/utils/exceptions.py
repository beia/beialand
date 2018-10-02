class RestException(Exception):
    def __init__(self, http_code, short_description, long_description=None):
        self.http_code = http_code
        self.short_description = short_description
        self.long_description = long_description

    def get_as_dict(self):
        result = {'error': {'http_code': self.http_code,
                            'short_description': self.short_description}}

        if self.long_description:
            result['error']['long_description'] = self.long_description

        return result
