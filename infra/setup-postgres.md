# Setup postgres
This is the manual way to set up postgres for a test server, for production it is recommended to use a managed database instead.

Either follow these [instructions](https://postgreshelp.com/postgresql-13-install-in-ubuntu/) or just run these commands:

```bash
# Create the file repository configuration
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

# Import the repository signing key
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -

# Update the package lists
sudo apt-get update

# Install the latest version of PostgreSQL.
# If you want a specific version, use 'postgresql-12' or similar instead of 'postgresql':
sudo apt-get -y install postgresql-13
```

Then, connect to the database (`sudo -u postgres psql`) and create the necessary user/database:
- `CREATE DATABASE server_db;`
- `CREATE USER db_user WITH SUPERUSER PASSWORD 'useYourOwnPasswordInstead';`
- `GRANT ALL PRIVILEGES ON DATABASE "server_db" to db_user;`

Test the connection to the new database with the custom user: `psql -h 127.0.0.1 -U db_user server_db`

That's it!
