FROM wordpress:${WORDPRESS_VERSION:-latest}
RUN apt-get update && apt-get install -y iputils-ping
#COPY fix-wp-config.sh /tmp
#RUN /tmp/fix-wp-config.sh
