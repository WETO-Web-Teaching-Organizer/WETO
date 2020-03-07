# Database migrations

This project uses the flyway database migration tool for database migrations.
The project has two separate databases, hence the different configurations and migration scripts for each.

The configuration files should be good to go to run within a local vagrant environment, however you probably need to change the settings for different environments separately. The config files should serve as a template for that however.

## Usage

To migrate both databases (within Vagrant), simply execute the following command:
> flyway -configFiles=/weto/database/flyway-weto5_master.conf,/weto/database/flyway-weto5_course.conf migrate

For other usage see https://flywaydb.org/documentation/