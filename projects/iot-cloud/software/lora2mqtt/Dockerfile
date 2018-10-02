FROM tiangolo/uwsgi-nginx:python3.6-alpine3.7

ENV UWSGI_INI /app/uwsgi.ini

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY src/ /app
