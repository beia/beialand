FROM python:3.7.2-alpine
WORKDIR /srv/sentiment-analysis
COPY requirements.txt ./
RUN apk add --update --no-cache \
    libuv \
    && apk add --update --no-cache --virtual .build-deps \
    python-dev \
    libuv-dev \
    build-base \
    && pip install --no-cache-dir -r requirements.txt \
    && apk del .build-deps \
    && rm -rf /var/cache/apk/*

COPY . .
CMD [ "python", "./service.py" ]
