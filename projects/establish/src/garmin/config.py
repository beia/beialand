import os.path
from garmin.helpers import env_load

ACCESS_TOKEN_URL="https://connectapi.garmin.com/oauth-service/oauth/access_token"
BASE_AUTH_URL="https://connect.garmin.com/oauthConfirm"
CALLBACK_URL="https://establish.beia-consult.ro/oauth/callback"
REQUEST_TOKEN_URL="https://connectapi.garmin.com/oauth-service/oauth/request_token"
STATIC_FOLDER = os.path.join(os.path.dirname(__file__), "../static")
TEMPLATES_FOLDER = "../templates"
DB_LOCATION = env_load("DB_LOCATION")
CLIENT_KEY = env_load("CLIENT_KEY")
CLIENT_SECRET = env_load("CLIENT_SECRET")
PORT = env_load('VIRTUAL_PORT', default=8080)
