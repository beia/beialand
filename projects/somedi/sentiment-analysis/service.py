#!/usr/bin/env python
from functools import wraps
import os
import uuid
import json
from time import time
from threading import Lock

from redis import Redis

from sanic import Sanic, response
from sanic.log import logger

from google.cloud import language
from google.cloud import translate
from google.cloud.language import enums
from google.cloud.language import types

DEFAULT_SA_LANG = "en"
ALL_METHODS = frozenset(["GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"])

class TokenBucket:
    """
    An implementation of the token bucket algorithm.
    """
    def __init__(self, rate, max_tokens):
        self.max_tokens = max_tokens
        self.tokens = max_tokens
        self.rate = rate
        self.last = time()
        self.lock = Lock()

    def consume(self, tokens=1):
        with self.lock:
            if not self.rate:
                return False
            now = time()
            lapse = now - self.last
            self.last = now
            self.tokens += lapse * self.rate
            self.tokens = min(self.max_tokens, self.tokens)

            if self.tokens >= tokens:
                self.tokens -= tokens
                return True
            else:
                return False


class TokenManager:
    def __init__(self):
        self.tokens = {}

    def add_token(self, token, rate, max_tokens):
        self.tokens[token] = TokenBucket(rate, max_tokens)

    def is_valid(self, token):
        return token in self.tokens

    def can_consume(self, token):
        return self.is_valid(token) and self.tokens[token].consume()

class KeyValueStore:
    def __init__(self):
        host = os.environ.get('REDIS_HOST', 'localhost')
        port = int(os.environ.get('REDIS_PORT', 6379))
        db = int(os.environ.get('REDIS_DB', 0))
        self.redis_expire = 300 # TODO: set from outside if needed
        self.redis = Redis(host=host, port=port, db=db)

    def get(self, job_id):
        content = self.redis.get(job_id)
        if not content:
            return None
        return json.loads(content)

    def set(self, job_id, content, expire=None):
        if not expire:
            expire=self.redis_expire
        self.redis.set(job_id, json.dumps(content), ex=expire)


class Application:
    def __init__(self):
        self.logger = logger
        self.init_sanic()
        self.init_tokens()
        self.kvstore = KeyValueStore()

    def init_sanic(self):
        self.app = Sanic(__file__)
        self.app.add_route(lambda request: self.healthcheck(request), '/healthcheck')
        self.app.add_route(lambda request: self.sentimentanalysis(request), '/sentiment-analysis', methods=ALL_METHODS)
        self.app.add_route(lambda request: self.jobstatus(request), '/job-status', methods=ALL_METHODS)
        self.app.add_route(lambda request: self.catch_all(request, path='/'), '/', methods=ALL_METHODS)
        self.app.add_route(lambda request, path: self.catch_all(request, path=path), '/<path:path>', methods=ALL_METHODS)

    def get_uuid(self):
        return str(uuid.uuid4())

    def init_tokens(self):
        self.token_manager = TokenManager()
        if "ACCESS_TOKEN" in os.environ:
            token = os.environ["ACCESS_TOKEN"]
        elif "ACCESS_TOKEN__FILE" in os.environ:
            with open(os.environ["ACCESS_TOKEN__FILE"], 'r') as f:
                token = f.read().strip()
        else:
            self.logger.error("Couldn't find a token. Set ACCESS_TOKEN or ACCESS_TOKEN__FILE")
            raise Exception("Couldn't find a token")
        self.logger.info(f"Loading token ACCESS_TOKEN: {token}")
        self.token_manager.add_token(token, 1, 3)

    def catch_all(self, request, path):
        return response.json({
            'status': 'invalid-arguments',
            'message': 'Invalid API request'
            }, status=404)

    def healthcheck(self, request):
        rnd = self.get_uuid()
        self.kvstore.set(rnd, {"content": rnd }, 5)
        job = self.kvstore.get(rnd)
        if not job or job["content"] != rnd:
            return response.text(None, status=500)
        # TODO: check Google API connectivity?
        return response.text(None, status=200)

    def sentimentanalysis(self, request):
        result = self.verify_request(request)
        if result:
            return result
        content = request.json.get("content")
        language = request.json.get("language", "en")
        if not content:
            return response.json({
                "status": "invalid-arguments",
                "message": "The request is missing the 'content' parameter"
            }, status=400)
        job_id = str(uuid.uuid4())
        self.app.add_task(self.long_running(language, content, job_id))
        return response.json({
                "status": "pending",
                "job-id": str(job_id)
            })

    async def long_running(self, language, content, job_id):
        self.kvstore.set(job_id, { "status": "pending" })
        try:
            self.logger.info("[Job %s]: Starting communication with Google", job_id)
            text = content
            if language != DEFAULT_SA_LANG:
                self.logger.info("[Job %s]: Content requires translation. Calling Translation API", job_id)
                translation = self.translate.translate(content, target_language="en")
                self.logger.debug("[Job %s]: Content(translated): %s", job_id, translation)
                text = translation["translatedText"]
            self.logger.info("[Job %s]: Calling Sentiment Analysis API", job_id)
            self.logger.debug("[Job %s]: Content: %s", job_id, text)
            document = types.Document(content=text, type=enums.Document.Type.PLAIN_TEXT)
            annotations = self.language.analyze_sentiment(document=document)
            self.logger.debug("[Job %s]: Annotations: %s", job_id, annotations)
            self.kvstore.set(job_id, {
                "status": "success",
                "sentiment-score": annotations.document_sentiment.score,
                })
            self.logger.info("[Job %s]: Finish processing", job_id)
        except Exception as ex:
            correlation_id = self.get_uuid()
            self.logger.exception("[Job %s]: We encountered an error. Correlation id '%s'", job_id, correlation_id)
            self.kvstore.set(job_id, {
                "status": "server-error",
                "message": "Encountered an error while processing request",
                "correlation-id": correlation_id
                })

    def jobstatus(self, request):
        result = self.verify_request(request)
        if result:
            return result
        job_id = request.json.get("job-id")
        if not job_id:
            return response.json({
                "status": "invalid-arguments",
                "message": "The request is missing the 'job-id' parameter"
            }, status=400)
        job = self.kvstore.get(job_id)
        if not job:
            return response.json({
                "status": "not-found",
                "message": "The specified job was not found"
            }, status=404)
        self.logger.info("Key-Value store returned %s", job)
        return response.json(job, status=200)

    def verify_request(self, request, methods=frozenset(["POST", "PUT"])):
        if request.method.upper() not in methods:
            return response.json({
                "status": "invalid-arguments",
                "message": "Unsupported method %s for this endpoint" % request.method.upper(),
                }, status=400)
        if request.json is None:
            return response.json({
                "status": "invalid-arguments",
                "message": "Request body needs to be JSON",
                }, status=400)
        token = request.json.get("token")
        if not token:
            self.logger.error(f"Token {token} is not valid")
            return response.json({
                "status": "access-denied",
                "message": "The request doesn't contain mandatory 'token' parameter"
            }, status=401)
        if not self.token_manager.is_valid(token):
            self.logger.error(f"Token {token} is not valid")
            return response.json({
                "status": "access-denied",
                "message": "The token was not recognized"
            }, status=403)
        if not self.token_manager.can_consume(token):
            self.logger.error(f"Token {token} doesn't have enough credits")
            return response.json({
                "status": "rate-limit",
                "message": "Your token used up its request quota. Wait a bit..."
            }, status=429)
        return None

    def run(self):
        self.translate = translate.Client()
        self.language = language.LanguageServiceClient()
        self.app.run(host="0.0.0.0", port=int(os.environ.get('PORT', "8080")))

if __name__ == "__main__":
    app = Application()
    app.run()
