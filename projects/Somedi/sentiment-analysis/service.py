from functools import wraps
import logging
import os
from time import time
from threading import Lock

from sanic import Sanic, response

from google.cloud import language
from google.cloud import translate
from google.cloud.language import enums
from google.cloud.language import types

DEFAULT_SA_LANG = "en"

class TokenBucket:
    """
    An implementation of the token bucket algorithm.
    """
    def __init__(self, rate, max_tokens):
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

            if self.tokens >= 0:
                self.tokens -= tokens
                return True
            else:
                return False


class TokenManager:
    def __init__(self):
        self.tokens = {}

    def add_token(self, token, rate, max_tokens):
        self.tokens[token] = TokenBucket(rate, max_tokens)

class Application:
    def __init__(self):
        self.logger = logging.getLogger('sentiment-analysis')
        self.logger.setLevel(logging.DEBUG)
        self.init_sanic()
        self.init_tokens()

    def init_sanic(self):
        self.app = Sanic(__file__)
        self.app.add_route(lambda request: self.healthcheck(request), '/healthcheck')
        self.app.add_route(lambda request: self.sentimentanalysis(request), '/sentiment-analysis', methods=["POST", "PUT"])
        self.app.add_route(lambda request: self.jobstatus(request), '/job-status')

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
        self.token_manager.add_token(token, 1, 3)

    def healthcheck(request):
        # TODO: implement
        return response.raw(b'', status=200)

    def sentimentanalysis(self, request):
        result = self.verify_request(request)
        if result:
            return result
        content = request.json["content"]
        language = request.json["language"]
        if language != DEFAULT_SA_LANG:
            translation = self.translate.translate(content, target_language="en")
            document = types.Document(content=translation, type=enums.Document.Type.PLAIN_TEXT)
        else:
            document = types.Document(content=content, type=enums.Document.Type.PLAIN_TEXT)
        annotations = self.language.analyze_sentiment(document=document)
        return response.json({
                "status": status,
                "job-id": None,
                "sentiment-score": annotations.document_sentiment.score
            })

    def jobstatus(request):
        result = self.verify_request(request)
        if result:
            return result
        # TODO: actually implement async API
        return response.json({
            "status": "not-found"
        }, status=404)

    def verify_request(self, request):
        if not request.json:
            return response.json({
                "status": "invalid-arguments"
                }, status=400)
        token = request.json.get("token")
        if token not in self.tokens:
            return response.json({
                "status": "access-denied",
            }, status=401)
        if not self.tokens[token].consume():
            return response.json({
                "status": "rate-limit",
            }, status=403)
        return None

    def run(self):
        self.translate = translate.Client()
        self.language = language.LanguageServiceClient()
        self.app.run(host="0.0.0.0", port=8080)

if __name__ == "__main__":
    app = Application()
    app.run()
