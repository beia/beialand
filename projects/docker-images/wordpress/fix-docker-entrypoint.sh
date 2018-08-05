#!/bin/sh

echo "sendmail_path = /usr/sbin/ssmtp -t" >> /usr/local/etc/php/conf.d/sendmail.ini
head -n -1 /usr/local/bin/docker-entrypoint.sh > /usr/local/bin/docker-entrypoint.sh.new
cat /tmp/generate_ssmtp_conf.sh >> /usr/local/bin/docker-entrypoint.sh.new
tail -n -1 /usr/local/bin/docker-entrypoint.sh >> /usr/local/bin/docker-entrypoint.sh.new
mv /usr/local/bin/docker-entrypoint.sh.new /usr/local/bin/docker-entrypoint.sh
chmod a+x /usr/local/bin/docker-entrypoint.sh
