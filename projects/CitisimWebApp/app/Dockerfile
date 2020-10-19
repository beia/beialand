
FROM tiangolo/uwsgi-nginx:python2.7

ENV UWSGI_INI /app/uwsgi.ini
ENV CITISIM_MYSQL_HOST citisim_db
ENV CITISIM_MYSQL_USER root
ENV CITISIM_MYSQL_PASSWORD toor
ENV CITISIM_MYSQL_DB libcitisim

RUN apt-get update && apt-get -y install python2.7-dev default-libmysqlclient-dev

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY src/ /app
COPY uwsgi.ini /app
