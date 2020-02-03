# Local development using Vagrant

## Installation
Vagrant requires a virtual machine provider to work. The easiest one to set up is VirtualBox.

1. Check the version of Virtualbox supported by Vagrant. As of 1/2020 that would be virtualbox 6.0.
2. Download and install the correct version of [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
3. Download and install [Vagrant](https://www.vagrantup.com/)
4. Open a shell and navigate to this folder (that the readme is in)
5. Execute 'vagrant up'
6. Wait for the virtual machine to install itself. It should run everything automatically (mainly the installation.sh script).
7. Test that everything was installed successfully by trying to open the [Tomcat manager](http://localhost/manager) using username and password 'admin'.

## Usage
Starting up the virtual machine:
> vagrant up
>
Stopping the virtual machine:
> vagrant halt
>
Restarting the virtual machine:
> vagrant reload
>
Opening up a shell to vagrant
> vagrant ssh
>

You can find the tomcat installation in the /opt/tomcat/latest directory on the VM.