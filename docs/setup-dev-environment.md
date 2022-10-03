# Setup development environment

Let's get started setting up your development environment.

**NOTE** The instructions will work better on Linux/Mac, there could be some details that do not work on Windows (help wanted).


**[Table of Contents](http://tableofcontent.eu)**

- [Compile-time dependencies](compile-time-dependencies)
- [Runtime dependencies](#runtime-dependencies)
   - [Postgres](#postgres)
   - [AWS Email Service](#aws-email-service)
   - [direnv](#direnv)
   - [Custom config](#custom-config)
   - [Run](#run)
- [Test dependencies](#test-dependencies)
- [Deployment setup](#deployment-setup)

## Compile-time dependencies

1. Clone the repository

    ```shell
    git clone git@github.com:wiringbits/scala-webapp-template.git
    ```

2. JDK setup, we highly recommend [SDKMAN](https://sdkman.io/) due to its simplicity to switch between different jdk versions, run `sdk env` to pick the project's suggested jdk or edit sdkman config (`~/.sdkman/etc/config`) to set `sdkman_auto_env=true` which picks the project's jdk automatically:

   ```shell
   # sdkman_auto_env=true would pick the right jdk when moving into the project's directory
   $ cd scala-webapp-template
   
   Using java version 11.0.16-tem in this shell.
   
   # otherwise, you can set the jdk manually with `sdk env`
   $ sdk env
   
   Using java version 11.0.16-tem in this shell.
   
   # verify your version
   $ java -version
   openjdk version "11.0.16" 2022-07-19
   OpenJDK Runtime Environment Temurin-11.0.16+8 (build 11.0.16+8)
   OpenJDK 64-Bit Server VM Temurin-11.0.16+8 (build 11.0.16+8, mixed mode)
   ```

   **Hint**: [.sdkmanrc](../.sdkmanrc) defines our suggested jdk.

3. Install sbt, run `sdk install sbt` or follow the official [instructions](https://www.scala-sbt.org/download.html).

4. Node setup, we highly recommend [nvm](https://github.com/nvm-sh/nvm) due to its simplicity to switch between different node versions, run `nvm use` to pick the project's suggested node version, or follow [nvm-instructions](https://github.com/nvm-sh/nvm#automatically-call-nvm-use) to pick the right version automatically:

   ```shell
   # nvm can pick the right node version when moving into the project's directory
   $ cd scala-webapp-template
   Found '~/scala-webapp-template/.nvmrc' with version <16.7.0>
   Now using node v16.7.0 (npm v7.20.3)
   
   # otherwise, you can set the node version manually with `nvm use`
   $ nvm use
   Found '~/scala-webapp-template/.nvmrc' with version <16.7.0>
   Now using node v16.7.0 (npm v7.20.3)
   
   # verify your version
   $ node --version
   v16.7.0
   ```

   **Hint**: [.nvmrc](../.nvmrc) defines our suggested node version.

5. Install [yarn](https://classic.yarnpkg.com/en/docs/install), most times, `npm install --global yarn` should be enough (we have tested this with yarn v1), be aware that this must installed at the node version you set in the previous step:

   ```shell
   # yarn -version
   1.22.11
   ```

That's it, now just run `sbt compile` to compile the project (the first time it could take several minutes).


## Runtime dependencies

### Postgres
PostgreSQL is the only required runtime dependency, it can be installed by following the official [docs](https://www.postgresql.org/download/), it can also be run with docker.

What matters is that you can connect to it with `psql -U postgres -h 127.0.0.1` (`postgres` is the default username, if you changed it, you must update the command too).

**Hint**: We use `127.0.0.1` to force `psql` to use a TCP connection instead of a unix socket which (`localhost`), this happens because the app connects to postgres through TCP.

Once you are connected into postgres, we'll create a database for our app, and, any necessary dependencies:

```postgres-sql
-- create a database for the app
CREATE DATABASE wiringbits_db;

-- connect to it
\c wiringbits_db;

-- create an extension used by the app
CREATE EXTENSION IF NOT EXISTS CITEXT;
```

### AWS Email Service
We are using [SES](https://aws.amazon.com/ses/) to send emails (like the account verification email, password recovery, etc), what matters is to get AWS keys with access to SES.

This is an optional requirement, if you decide to ignore it, everything should work fine.

### direnv
[direnv](https://direnv.net/) is super handy to define your custom app settings without modifying any of the application files tracked by git (sorry windows). It is optional but highly recommended.

In short, it will allow you to create a `.envrc` file with all your custom settings, which will be loaded when moving into the project's directory (don't forget the [hook](https://direnv.net/docs/hook.html))

### Custom config
It is very likely that the default settings won't work for you, at least, you will be expected to update the settings to match your postgres credentials (and SES if used).

There are two ways:

1. Update [application.conf](../server/src/main/resources/application.conf)
Update application.conf to set your environment specific values (just avoid committing these).
2. Use `direnv`, create `.envrc` to export environment variables for your custom settings (don't forget to run `direnv allow` after that), get inspired by this example, it is unlikely that you will need to change any other settings:

   ```shell
   # postgres settings
   export POSTGRES_HOST="127.0.0.1"
   export POSTGRES_DATABASE="wiringbits_db"
   export POSTGRES_USERNAME="postgres"
   export POSTGRES_PASSWORD="postgres"
   
   # emails
   export EMAIL_SENDER_ADDRESS="test@wiringbits.net"
   export EMAIL_PROVIDER=none

   # aws, required only if the email provider is AWS
   export AWS_REGION="us-west-2"
   export AWS_ACCESS_KEY_ID="REPLACE_ME"
   export AWS_SECRET_ACCESS_KEY="REPLACE_ME"
   ```

### Run

Time to run the app:

1. `sbt server/run` launches the backend, which is started once you launch a request (like `curl localhost:9000`), swagger-ui available at `http://localhost:9000/docs/index.html`.
2. `sbt dev-web` launches the main web app at `localhost:8080` 
3. `sbt dev-admin` launches the admin web app at `localhost:8081`


**Hints**:

- All these apps are automatically reloaded on code changes.
- The server app prints the settings after starting, double check that they match your custom settings.
- By default, outgoing emails are logged, be sure to check those to look for the email verification links.

## Test dependencies

Docker is the only required dependency to run the integration tests.

Each integration test mounts its own clean database through docker, which removes the need to worry about tests polluting data.

Check the official [docs](https://docs.docker.com/engine/install/) to get it installed, it is ideal that docker can be executed without `sudo`, when this command works, tests must run too: `docker run hello-world`

Commands:
1. `sbt test` runs all the tests.
2. `sbt server/test` runs the server tests only.

**Hint** IntelliJ allows running all tests in a file, or a single test through its UI, which is very handy.


## Deployment setup

Check the [infra](../infra/README.md) project.
