FROM python:3-alpine

WORKDIR /usr/src/app

COPY src/ .
COPY config/ .

RUN pip install --no-cache-dir -r requirements.txt

CMD [ "python", "./mqtt-to-graphite.py" ]
