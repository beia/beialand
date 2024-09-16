import os

def env_load(name, default=None, strip=True):
    value = os.environ.get(name)
    if value is not None:
        return value.strip()
    filename = os.environ.get(name + "__FILE")
    if filename is not None:
        with open(filename) as f:
            return f.read().strip()
    return default
