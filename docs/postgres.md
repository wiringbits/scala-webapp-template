# Postgres setup on ubuntu 21.04

## Installation

To install on ubuntu run the following commands

```sh
sudo sh -c 'echo "deb http://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -
sudo apt-get update
sudo apt-get -y install postgresql
```

> Postgres CLI command : `psql`

## Default values

| Property | Default Value |
| -------- | ------------- |
| Port     | `54320`       |
| Username | `postgress`   |
| Password | `null`        |

---

## Authentication

To set a password run

```sh
sudo -u postgres psql
```

then run

```sql
ALTER ROLE postgres WITH PASSWORD 'postgres';
```

then you can access `psql -U postgres -h 127.0.0.1 -W` using `postgres` as password

To enter the postgres CLI without password you should modify the file `/etc/postgresql/<postgres-version>/main/pg_hba.conf`

for example

```sh
sudo nano /etc/postgresql/14/main/pg_hba.conf
```

then set the `METHOD` column values of rows `ipv4` and `ipv6` to `trust`

```properties
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             all                                     peer
# IPv4 local connections:
host    all             all             127.0.0.1/32            scram-sha-256
# IPv6 local connections:
host    all             all             ::1/128                 scram-sha-256
```

like this

```properties
# TYPE  DATABASE        USER            ADDRESS                 METHOD

# "local" is for Unix domain socket connections only
local   all             all                                     peer
# IPv4 local connections:
host    all             all             127.0.0.1/32            trust
# IPv6 local connections:
host    all             all             ::1/128                 trust
```

Restart postgres

```sh
sudo service postgresql restart
```

then you can connect by

```sh
psql -U postgres -h 127.0.0.1
```

---

## Create de database

on the postgres CLI then create the `wiringbits_db` by

```sql
CREATE DATABASE wiringbits_db;

CREATE EXTENSION IF NOT EXISTS CITEXT;
```

---

[Potsgres linux instalation](https://www.postgresql.org/download/linux/ubuntu/)
