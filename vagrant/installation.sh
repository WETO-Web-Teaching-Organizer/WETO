#!/bin/bash

## Set up locale languages
sudo locale-gen en_US en_US.UTF-8
sudo locale-gen fi_FI fi_FI.UTF-8
sudo dpkg-reconfigure locales
export LANGUAGE="en_US.UTF-8"
sudo sh -c 'echo "LANGUAGE=\"en_US.UTF-8\"" >> /etc/default/locale'
sudo sh -c 'echo "LC_ALL=\"en_US.UTF-8\"" >> /etc/default/locale'

sudo apt-get update

## Install psql and config it
sudo apt-get -y -q install postgresql
sudo -u postgres psql -f /vagrant/init.sql
sudo service postgresql restart

## Install java
sudo add-apt-repository -y ppa:openjdk-r/ppa
sudo apt-get -y -q update
sudo apt-get -y -q install openjdk-8-jdk

## Install tomcat
sudo useradd -r -m -U -d /opt/tomcat -s /bin/false tomcat
sudo usermod -a -G tomcat vagrant
wget -nv https://www.nic.funet.fi/pub/mirrors/apache.org/tomcat/tomcat-9/v9.0.31/bin/apache-tomcat-9.0.31.tar.gz -P /tmp
sudo tar -xf /tmp/apache-tomcat-9.*.tar.gz -C /opt/tomcat
sudo ln -s /opt/tomcat/apache-tomcat-9.0.31 /opt/tomcat/latest
sudo chown -RH tomcat: /opt/tomcat/latest
sudo chmod -R 775 /opt/tomcat/latest
sudo sh -c 'chmod +x /opt/tomcat/latest/bin/*.sh'

# Set tomcat up for systemctl
sudo apt-get -y -q install systemd
sudo cp /vagrant/tomcat-setup/tomcat-service-file /etc/systemd/system/tomcat.service
sudo systemctl daemon-reload
sudo systemctl start tomcat
sudo systemctl enable tomcat
sudo -u tomcat cp /vagrant/tomcat-setup/manager-context.xml /opt/tomcat/latest/webapps/manager/META-INF/context.xml
sudo -u tomcat cp /vagrant/tomcat-setup/manager-context.xml /opt/tomcat/latest/webapps/host-manager/META-INF/context.xml
sudo -u tomcat cp /vagrant/tomcat-setup/tomcat-users.xml /opt/tomcat/latest/conf/tomcat-users.xml
sudo systemctl restart tomcat

sudo mkdir -p /opt/flyway
sudo chown vagrant /opt/flyway
cd /opt/flyway
wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/6.3.0/flyway-commandline-6.3.0-linux-x64.tar.gz | tar xvz
cp -rf flyway-6.3.0/* .
rm -rf flyway-6.3.0
sudo chown -R vagrant *
sudo ln -s `pwd`/flyway /usr/local/bin

## Initialize the database
flyway -configFiles=/weto/database/flyway-weto5_master.conf baseline
flyway -configFiles=/weto/database/flyway-weto5_course.conf baseline