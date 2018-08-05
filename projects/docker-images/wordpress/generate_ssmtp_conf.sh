smtp_envs=(
	WORDPRESS_SMTP_ADDRESS
	WORDPRESS_SMTP_RELAY_HOST
	WORDPRESS_SMTP_USER
	WORDPRESS_SMTP_PASSWORD
)
for e in "${smtp_envs[@]}"; do
	file_env "$e"
done

cat > /etc/ssmtp/ssmtp.conf << SMTPEOF
root=${WORDPRESS_SMTP_ADDRESS:=noreply@wordpress.beia.ro}
mailhub=${WORDPRESS_SMTP_RELAY_HOST:=localhost}
AuthUser=${WORDPRESS_SMTP_USER:=mailman}
AuthPass=${WORDPRESS_SMTP_PASSWORD:=newman}
UseSTARTTLS=YES
SMTPEOF

for e in "${smtp_envs[@]}"; do
	unset "$e"
done
