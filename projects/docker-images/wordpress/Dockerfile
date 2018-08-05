FROM wordpress
RUN apt-get update && apt-get install -y ssmtp
COPY . /tmp
RUN /tmp/fix-docker-entrypoint.sh
