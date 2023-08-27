FROM martialblog/limesurvey:6.2.3-230821-apache

COPY --chown=www-data:www-data mpm_prefork.conf /etc/apache2/mods-available/mpm_prefork.conf
COPY entrypoint.sh /usr/local/bin/entrypoint.sh