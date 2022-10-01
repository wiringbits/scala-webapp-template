# Learning material

These are the tools used by the template, you don't need to master them all but being familiar with them definitely helps:
- [Scala](https://scala-lang.org/), we use Scala 2.13 because it has great tooling support, we'll eventually upgrade to Scala 3.
- [Scala.js](https://www.scala-js.org/) powers the frontend side.
- [Scalablytyped](https://scalablytyped.org/) generates the Scala facades to interact with JavaScript libraries by converting TypeScript definitions to Scala.js facades.
- [yarn](https://yarnpkg.com) (v1) as the JavaScript package manager.
- [React](https://reactjs.org/) as the view library.
- [Slinky](https://slinky.dev/) being the Scala wrapper for React.
- [Webpack](https://webpack.js.org) to bundle the web apps.
- [Scalajs bundler](https://scalacenter.github.io/scalajs-bundler/) being the Scala wrapper for Webpack.
- [Material UI v3](https://v3.material-ui.com/) as the Material UI framework on top of React (hoping to upgrade to v5 when Scalablytyped supports it).
- [Play Framework](https://playframework.com/) as the backend framework, used for the REST API.
- [sttp](https://github.com/softwaremill/sttp/) as the REST API client.
- [react-router](https://www.npmjs.com/package/react-router) is the frontend routing library.
- [play-json](https://github.com/playframework/play-json/) is the JSON library.
- [ansible](https://ansible.com/) as the tool for deploying everything to a VM.
- [nginx](https://nginx.org/en/) as the reverse proxy for handling the internet traffic, as well as the authentication mechanism for the admin portal.
- [GitHub](https://github.com/features/actions) actions integration so that you have a way to get every commit tested.
