#!/bin/bash

curl --upload-file /weto/target/weto.war "http://admin:admin@localhost:8080/manager/text/deploy?path=/weto&update=true"
sudo -u tomcat cp -v /vagrant/package.properties /opt/tomcat/latest/webapps/weto/WEB-INF/classes/package.properties
