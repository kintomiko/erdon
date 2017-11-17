FROM mysql/mysql-server:5.6

# Mysql docker image automagically runs this script for us
# COPY db_setup.sh /docker-entrypoint-initdb.d/db_setup.sh

#
COPY my.cnf /etc/mysql/my.cnf
ENV MYSQL_ROOT_PASSWORD password
ENV MYSQL_RAKEN_PASSWORD erdon
ENV MYSQL_ROOT_HOST %
ENV MYSQL_USER erdon
ENV MYSQL_PASSWORD erdon

EXPOSE 3306

#VOLUME /tmp

#Note that the data will go to /var/lib/mysql


