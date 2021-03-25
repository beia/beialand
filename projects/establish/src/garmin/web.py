import os
import requests
from requests_oauthlib import OAuth1Session, OAuth1
from sanic import Sanic
from sanic import response
from sanic.log import logger
from sanic.response import json
from garmin.config import (
	CLIENT_KEY,
	CLIENT_SECRET,
	STATIC_FOLDER,
	CALLBACK_URL,
	REQUEST_TOKEN_URL,
	BASE_AUTH_URL,
	ACCESS_TOKEN_URL
	)
from garmin.db import store
from garmin.tokens import tokens


app = Sanic()
store.load()

app.static('/favicon.ico', os.path.join(STATIC_FOLDER, 'img', 'favicon.ico'))
logger.info("CLIENT_KEY=%s", CLIENT_KEY)
logger.info("CLIENT_SECRET=%s", CLIENT_SECRET)
logger.info("STATIC_FOLDER=%s", STATIC_FOLDER)
logger.info("CALLBACK_URL=%s", CALLBACK_URL)
logger.info("REQUEST_TOKEN_URL=%s", REQUEST_TOKEN_URL)
logger.info("BASE_AUTH_UrL=%s", BASE_AUTH_URL)
logger.info("ACCESS_TOKEN_URL=%s", ACCESS_TOKEN_URL)

@app.exception(FileNotFoundError)
def ignore_file_not_found(request, exception: FileNotFoundError):
	return response.text("File not found: {}".format(exception.path), 404)


@app.route('/register')
async def register(request):
	oauth = OAuth1Session(
		CLIENT_KEY,
		client_secret=CLIENT_SECRET,
		callback_uri=CALLBACK_URL)
	req_token = oauth.fetch_request_token(REQUEST_TOKEN_URL)
	logger.info("request token", req_token)
	token = req_token["oauth_token"]
	secret = req_token["oauth_token_secret"]
	tokens[token] = secret
	url = oauth.authorization_url(BASE_AUTH_URL)
	return response.redirect(url)


@app.route('/callback')
@app.route('/oauth/callback')
async def oauth_callback(request):
	token = request.args.get('oauth_token')
	verifier = request.args.get('oauth_verifier')
	if token not in tokens:
		return response.text("haha, nice try!")
	oauth = OAuth1Session(
		CLIENT_KEY,
		client_secret=CLIENT_SECRET,
		resource_owner_key=token,
		resource_owner_secret=tokens[token],
		verifier=verifier)
	auth_token = oauth.fetch_access_token(ACCESS_TOKEN_URL)
	store.add_user(auth_token["oauth_token"], auth_token["oauth_token_secret"])
	return response.text("Cool, registered!")


@app.route('/ping/<request_type:string>', methods=["GET", "POST"])
async def ping(request, request_type):
	logger.info("Received a request %s %s", request_type, request.body)
	return response.text("")
