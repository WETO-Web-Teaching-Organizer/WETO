#!/bin/bash

flyway -configFiles=/weto/database/flyway-weto5_master.conf migrate
flyway -configFiles=/weto/database/flyway-weto5_course.conf migrate

curl --upload-file /weto/weto/target/weto.war "http://admin:admin@localhost:8080/manager/text/deploy?path=/weto5&update=true"
sudo -u tomcat cp -v /vagrant/package.properties /opt/tomcat/latest/webapps/weto5/WEB-INF/classes/package.properties
curl "http://admin:admin@localhost:8080/manager/text/start?path=/weto5&update=true"
