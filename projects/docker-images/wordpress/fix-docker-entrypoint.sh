#!/bin/sh
PHP_SMTP_CONFIG=/usr/local/etc/php/conf.d/sendmail.ini
ENTRYPOINT_SCRIPT=/usr/local/bin/docker-entrypoint.sh

echo "sendmail_path = /usr/sbin/ssmtp -t" >> $PHP_SMTP_CONFIG
(
	head -n -1 ${ENTRYPOINT_SCRIPT}
	cat /tmp/generate_ssmtp_conf.sh
	tail -n -1 ${ENTRYPOINT_SCRIPT}
) > ${ENTRYPOINT_SCRIPT}.new
mv ${ENTRYPOINT_SCRIPT}.new ${ENTRYPOINT_SCRIPT}
chmod a+x ${ENTRYPOINT_SCRIPT}
