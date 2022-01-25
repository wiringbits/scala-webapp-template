# Wiringbits Web Application Template

![wiringbits](https://github.com/wiringbits/scala-webapp-template/workflows/Build%20the%20server%20app/badge.svg)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.6.0.svg)](https://www.scala-js.org)

This is the Wiringbits Web Application Template.

The template provides a reusable skeleton to build web applications in Scala/Scala.js, it was created by extracting common functionality from web applications built by the [Wiringbits](https://wiringbits.net) team.

While this is still a work in progress, it has enough details to save a couple of weeks of work when starting a new project.

Check the [infra](./infra) docs to deploy your application.

## Demo

To get an idea on what the template provides, play with the live demos, what you see there is what you will get by cloning the template:

- [Web App](https://template-demo.wiringbits.net)
- [Admin App](https://template-demo-admin.wiringbits.net) (username = `demo`, password = `wiringbits`)

## Goal

The goal for this project is to include more functionality that is common for web projects, like:

- Better authentication instead of just using a JSON Web Token.
- Allow updating user details as well as changing password.
- Enable accounts after verifying the email address (by sending an email).
- Password recovery after lossing it.
- A better admin dashboard.

A long-term goal is to allow admins to update most details in the application by auto-generating the views (like [Django](https://www.djangoproject.com/) does).

## Tech stack

The stack used is:

- Scala 2.13 used for the whole app (hoping to upgrade to Scala 3 when the dependencies are ready).
- Postgresql is the database library.
- [Scala.js](https://www.scala-js.org/) powers the frontend side.
- [React](https://reactjs.org/) as the view library.
- [Slinky](https://slinky.dev/) being the Scala wrapper for React.
- [Scalablytyped](https://scalablytyped.org/) generates the Scala facades to interact with JavaScript libraries by converting TypeScript definitions to Scala.js facades.
- [Webpack](https://webpack.js.org) to bundle the web apps.
- [Scalajs bundler](https://scalacenter.github.io/scalajs-bundler/) being the Scala wrapper for Webpack.
- [Material UI v3](https://v3.material-ui.com/) as the Material UI framework on top of React (hoping to upgrade to v4 when Scalablytyped supports it).
- [Play Framework](https://playframework.com/) as the backend framework, used for the REST API.
- [sttp](https://github.com/softwaremill/sttp/) as the REST API client.
- [react-router](https://www.npmjs.com/package/react-router) is the frontend routing library.
- [play-json](https://github.com/playframework/play-json/) is the JSON library.
- [yarn](https://yarnpkg.com) as the JavaScript package manager.
- [ansible](https://ansible.com/) as the tool for deploying everything to a VM.
- [nginx](https://nginx.org/en/) as the reverse proxy for handling the internet traffic, as well as the authentication mechanism for the admin portal.
- [GitHub](https://github.com/features/actions) actions integration so that you have a way every commit gets tested.

## Features

By cloning this template you will gain a skeleton with:

- A backend server exposing a REST API with a simple JWT based authentication mechanism.
- A web application intended to be used by multiple users, allowing them to create an account, and to log into it.
- An admin web application intended to be used by administrators only, using the http basic authentication capability from nginx.
- A shared library reused by all the modules, yay!
- The necessary scripts to deploy the applications by running a single command once you set up the necessary config.
- Mobile-friendly web apps.
- A simple landing page with a navigation bar, footer, etc.
- The necessary components to display progress indicators when executing remote actions, as well as a retry button in case of unexpected failures.
- Hot-reloading to see the code changes without recompiling the apps manually.
- Database integration tests powered by [testcontainers](https://www.testcontainers.org/), launching a docker container with a clean database to run the tests, so that you are confident that the database layer is in sync with the actual database schema.
- REST API integration tests, so that you are confident that your changes aren't breaking the API compatibility unintentionally.
- A user log displaying the interactions with the app.

## Code format

The code format is checked by [scalafmt](https://scalameta.org/scalafmt) when the CI complains about the format, running `sbt scalafmtAll` should fix it.

## Ubuntu Setup

1. [SDKMan java/sbt setup](./docs/sdkman-java-sbt.md)
2. [Postgres setup](./docs/postgres.md)

## server

This is the server side for the project.

Run it with `sbt server/run`, you are expected to have updated the [application.conf](server/src/main/resources/application.conf) to define the settings to reach your postgres instance.

Notes:
- The server doesn't really start until you send the first request, like `curl localhost:9000/health`
- Make sure to follow the [Postgres setup](./docs/postgres.md) because the project depends on Postgres Extensions that are created there.

## web

This is the main web app for the users.

Run `sbt dev-web` to launch the website on `localhost:8080` pointing to the server url at `localhost:9000`, which reloads on code changes.

### Deploy

Run this command to package the app for release, the output is stored at `./web/build` (replace the placeholders):

```bash
API_URL="https://REPLACE_ME" sbt web/build
```

## admin

This is the admin web app.

Run `sbt dev-admin` to launch the website on `localhost:8081` pointing to the server url at `localhost:9000`, which reloads on code changes.

## lib

This is the shared library powering all the Scala apps.

### common

The [common](lib/common) module includes all the logic that can be shared on all the apps.

### api

The [api](lib/api) module includes the shared APIs like the backend client.

### ui

The [ui](lib/ui) module includes the shared UI like some reusable react components.

## Scala.js hints

- Put public resources on the [js](src/main/js) folder.
- Use IntelliJ, and configure it to format the code on save with [scalafmt](https://scalameta.org/scalafmt/docs/installation.html#intellij) to keep the code consistent.
- Make sure to follow the [Slinky](https://slinky.dev/) tutorial, and enable the `Slinky` extension on IntelliJ, otherwise, you may find highlighting errors while sbt compiles fine.
- Follow the [Material UI V3](https://v3.material-ui.com/) docs to understand the components the app is using (V4 is not supported yet by Slinky).
- Follow the [Material UI Slinky Demo](https://github.com/ScalablyTyped/SlinkyDemos/tree/master/material-ui/) when necessary.
- Follow the [Scala.js for JavaScript developers](https://www.scala-js.org/doc/sjs-for-js/) tutorial to get an understanding on how the project works.

It's strongly recommended to use these imports while dealing with material-ui components instead of referencing the components directly, otherwise, IntelliJ gets quite slow, and tends to highlight errors while sbt compiles properly:

```scala
import com.alexitc.materialui.facade.materialUiCore.{components => mui}
import com.alexitc.materialui.facade.materialUiIcons.{components => muiIcons}
```
