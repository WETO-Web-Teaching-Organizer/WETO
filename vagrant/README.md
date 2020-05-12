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

# Setting up IDEA (or similar IDE)

This readme covers how to set up the program for IntelliJ IDEA, but might come in handy with other IDEs as well.

1. Open Idea and select Import project
    - Navigate to the weto-folder in the project root and open pom.xml
    - Wait for it to load up the project
2. Set up the deployment settings from Tools > Deployment > Configuration
    - Add new SFTP "Vagrant"
    - Host: localhost, Port: 2222
    - User: vagrant, use Keypair authentication. You need to navigate to the key file which should be under the directory vagrant/vagrant/machines/default/virtualbox/private_key
    - Hit OK
3. Download the tomcat binaries for debugging
    - SSH to your Vagrant
    - Do the following commands:
    ```shell
    cd /opt/tomcat/latest/
    tar -cf tomcat-bin.tar lib/ bin/ conf/
    exit
    ## Do these on your local machine, password is vagrant
    scp -P 2222 vagrant@localhost:/opt/tomcat/latest/tomcat-bin.tar .
    mkdir tomcat
    tar -xf tomcat-bin.tar -C tomcat/
    rm tomcat-bin.tar
    ```
    - You may now also remove the tar from withing vagrant
4. Set up the debug configuration from Run > Edit Configurations
    - From templates select Tomcat Server and Remote
    - Hit "Create configuration" in the top right corner
    - Rename the configuration (e.g "Vagrant")
    - Set the browser URL setting to http://localhost:8080/weto5
    - Configure the Application Server
      - Tomcat home is the directory where we extracted the tomcat binaries to (project_root/vagrant/tomcat)
      - Hit OK
    - Configure the before launch settings ("Before launch: Build, Activate tool window")
      - Build artifacts -> select "weto:war" and "weto:war exploded"
      - Run Remote External tool
        - Name: Deploy weto
        - Deployment server: Vagrant
        - Program:
          - Host: localhost
          - Port: 2222
          - Credentials are the same as earlier
        - -> then select the file /vagrant/deploy.sh from the dialog that opens
        - Hit OK
    - Go to the tab "Startup/Connection"
      - Select "debug"
      - Change the port to 8081 from below
5. Run the debugger to test it's working
    - If you're getting permission denied on the deploy.sh script, run the following commands within the vagrant folder:
    ```shell
    chmod +X deploy.sh
    chmod 777 deploy.sh
    ```
    - If you're still facing issues it might be because the file is encoded with Windows file endings (CRLF). Linux can only run scripts with Unix file endings (LF). You can convert the file using most modern text editors.
6. Set up SASS file watchers
    - Separately: Install Node from [here](https://nodejs.org/) if you haven't already (or alternatively choco/homebrew)
      - After Node installation install Sass globally with "npm install -g sass" in a command line console
    - In IDEA: Open File > Settings > Plugins
      - Go to marketplace and install the "File Watchers" plugin by JetBrains
      - Click Restart IDE
    - Go to File > Settings > Tools > File Watchers
      - Add a new Watcher and select SCSS from the dropdown
      - Click on the "..." button next to Scope and add a new scope "SCSS"
        - Set the file pattern as follows: file[weto]:src/main/webapp/scss//*
        - Hit OK
      - Set the arguments field to be as follows: $FileName$:$ProjectFileDir$\src\main\webapp\css\\$FileNameWithoutExtension$.css
        - If you're not on Windows replace the backslashes with regular ones
      - Set the "Output paths to refresh" as follows: $ProjectFileDir$\src\main\webapp\css\\$FileNameWithoutExtension$.css:$ProjectFileDir$\src\main\webapp\css\\$FileNameWithoutExtension$.css.map
      - Hit OK and then OK again on the Settings
7. Done!
