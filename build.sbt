import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

ThisBuild / scalaVersion := "2.13.10"
ThisBuild / organization := "net.wiringbits"

val playJson = "2.9.4"
val sttp = "3.8.13"
val webappUtils = "0.5.16"
val swagger = "1.6.9"
val anorm = "2.7.0"

val consoleDisabledOptions = Seq("-Werror", "-Ywarn-unused", "-Ywarn-unused-import")

/** Say just `build` or `sbt build` to make a production bundle in `build`
  */
lazy val build = TaskKey[File]("build")

lazy val commonSettings: Project => Project = {
  _.settings(
    // Enable fatal warnings only when running in the CI
    scalacOptions ++= {
      sys.env
        .get("CI")
        .filter(_.nonEmpty)
        .map(_ => Seq("-Werror"))
        .getOrElse(Seq.empty[String])
    },
    Compile / compile / wartremoverErrors ++= List(
      Wart.ArrayEquals,
//      Wart.Any,
//      Wart.AsInstanceOf,
//      Wart.ExplicitImplicitTypes,
      Wart.IsInstanceOf,
      Wart.JavaConversions,
//      Wart.JavaSerializable,
      Wart.MutableDataStructures,
//      Wart.NonUnitStatements,
//      Wart.Nothing,
      Wart.Null,
      Wart.OptionPartial,
//      Wart.Overloading,
//      Wart.Product,
//      Wart.PublicInference,
      Wart.Return,
//      Wart.Serializable,
//      Wart.StringPlusAny,
//      Wart.ToString,
      Wart.TryPartial
    )
  )
}

// Used only by the server
// TODO: Reuse it in all projects
lazy val baseServerSettings: Project => Project = {
  _.settings(
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-encoding",
      "UTF-8",
      "-Wconf:src=src_managed/.*:silent",
      "-Xlint:missing-interpolator",
      "-Xlint:adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-unused"
    ),
    Compile / doc / scalacOptions ++= Seq("-no-link-warnings"),
    // Some options are very noisy when using the console and prevent us using it smoothly, let's disable them
    Compile / console / scalacOptions ~= (_.filterNot(consoleDisabledOptions.contains))
  )
}

// Used only by web projects
lazy val baseWebSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      scalacOptions ++= Seq(
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-encoding",
        "utf-8", // Specify character encoding used by source files.
        "-explaintypes", // Explain type errors in more detail.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-unchecked" // Enable additional warnings where generated code depends on assumptions.
      ),
      scalaJSUseMainModuleInitializer := true,
      /* disabled because it somehow triggers many warnings */
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
      /* for slinky */
      libraryDependencies ++= Seq("me.shadaj" %%% "slinky-hot" % "0.7.3"),
      libraryDependencies ++= Seq(
        "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
        "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.3.0"
      ),
      scalacOptions += "-Ymacro-annotations",
      Test / fork := false, // sjs needs this to run tests
      Test / requireJsDomEnv := true
    )

// Used only by the lib projects
// TODO: This should go to commonSettings instead
lazy val baseLibSettings: Project => Project = _.settings(
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8", // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked" // Enable additional warnings where generated code depends on assumptions.
  )
)

/** Implement the `build` task define above. Most of this is really just to copy the index.html file around.
  */
lazy val browserProject: Project => Project =
  _.settings(
    build := {
      val artifacts = (Compile / fullOptJS / webpack).value
      val artifactFolder = (Compile / fullOptJS / crossTarget).value
      val jsFolder = baseDirectory.value / "src" / "main" / "js"
      val distFolder = baseDirectory.value / "build"

      distFolder.mkdirs()
      artifacts.foreach { artifact =>
        val target = artifact.data.relativeTo(artifactFolder) match {
          case None => distFolder / artifact.data.name
          case Some(relFile) => distFolder / relFile.toString
        }

        Files.copy(artifact.data.toPath, target.toPath, REPLACE_EXISTING)
      }

      // copy public resources
      Files
        .walk(jsFolder.toPath)
        .filter(x => !Files.isDirectory(x))
        .forEach(source => {
          source.toFile.relativeTo(jsFolder).foreach { relativeSource =>
            val dest = distFolder / relativeSource.toString
            dest.getParentFile.mkdirs()
            Files.copy(source, dest.toPath, REPLACE_EXISTING)
          }
        })

      // link the proper js bundle
      val indexFrom = baseDirectory.value / "src/main/js/index.html"
      val indexTo = distFolder / "index.html"

      val indexPatchedContent = {
        import collection.JavaConverters._
        Files
          .readAllLines(indexFrom.toPath, IO.utf8)
          .asScala
          .map(_.replaceAllLiterally("-fastopt", "-opt"))
          .mkString("\n")
      }

      Files.write(indexTo.toPath, indexPatchedContent.getBytes(IO.utf8))
      distFolder
    }
  )

// specify versions for all of reacts dependencies
lazy val reactNpmDeps: Project => Project =
  _.settings(
    stTypescriptVersion := "3.9.3",
    stIgnore += "react-proxy",
    Compile / npmDependencies ++= Seq(
      "react" -> "16.13.1",
      "react-dom" -> "16.13.1",
      "@types/react" -> "16.9.42",
      "@types/react-dom" -> "16.9.8",
      "csstype" -> "2.6.11",
      "@types/prop-types" -> "15.7.3",
      "react-proxy" -> "1.1.8"
    )
  )

lazy val withCssLoading: Project => Project =
  _.settings(
    /* custom webpack file to include css */
    Compile / webpackConfigFile := Some((ThisBuild / baseDirectory).value / "custom.webpack.config.js"),
    Test / webpackConfigFile := None, // it is important to avoid the custom webpack config in tests to get them passing
    Compile / npmDevDependencies ++= Seq(
      "webpack-merge" -> "4.2.2",
      "css-loader" -> "3.4.2",
      "style-loader" -> "1.1.3",
      "file-loader" -> "5.1.0",
      "url-loader" -> "3.0.0"
    )
  )

lazy val bundlerSettings: Project => Project =
  _.settings(
    Compile / fastOptJS / webpackExtraArgs += "--mode=development",
    Compile / fullOptJS / webpackExtraArgs += "--mode=production",
    Compile / fastOptJS / webpackDevServerExtraArgs += "--mode=development",
    Compile / fullOptJS / webpackDevServerExtraArgs += "--mode=production"
  )

// Used only by play-based projects
lazy val playSettings: Project => Project = {
  _.enablePlugins(PlayScala)
    .disablePlugins(PlayLayoutPlugin)
    .settings(
      // docs are huge and unnecessary
      Compile / doc / sources := Nil,
      Compile / doc / scalacOptions ++= Seq(
        "-no-link-warnings"
      ),
      // remove play noisy warnings
      play.sbt.routes.RoutesKeys.routesImport := Seq.empty,
      libraryDependencies ++= Seq(
        guice,
        evolutions,
        jdbc,
        ws,
        "com.google.inject" % "guice" % "5.1.0"
      ),
      // test
      libraryDependencies ++= Seq(
        "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
        "org.mockito" %% "mockito-scala" % "1.17.12" % Test,
        "org.mockito" %% "mockito-scala-scalatest" % "1.17.12" % Test
      )
    )
}

lazy val common = (crossProject(JSPlatform, JVMPlatform) in file("lib/common"))
  .configure(baseLibSettings, commonSettings)
  .jsConfigure(_.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin))
  .settings(
    libraryDependencies ++= Seq()
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % playJson,
      "net.wiringbits" %% "webapp-common" % webappUtils,
      "org.scalatest" %% "scalatest" % "3.2.15" % Test
    )
  )
  .jsSettings(
    useYarn := true,
    Test / fork := false, // sjs needs this to run tests
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
      "com.typesafe.play" %%% "play-json" % playJson,
      "net.wiringbits" %%% "webapp-common" % webappUtils,
      "org.scalatest" %%% "scalatest" % "3.2.15" % Test,
      "com.beachape" %%% "enumeratum" % "1.7.0"
    )
  )

// shared apis
lazy val api = (crossProject(JSPlatform, JVMPlatform) in file("lib/api"))
  .dependsOn(common)
  .configure(baseLibSettings, commonSettings)
  .jsConfigure(_.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin))
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % playJson,
      "com.softwaremill.sttp.client3" %% "core" % sttp,
      "io.swagger" % "swagger-annotations" % swagger
    )
  )
  .jsSettings(
    useYarn := true,
    Test / fork := false, // sjs needs this to run tests
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % playJson,
      "com.softwaremill.sttp.client3" %%% "core" % sttp,
      "org.scalatest" %%% "scalatest" % "3.2.15" % Test,
      "com.beachape" %%% "enumeratum" % "1.7.0"
    )
  )

// shared on the ui only
lazy val ui = (project in file("lib/ui"))
  .configure(baseLibSettings, commonSettings)
  .configure(_.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, ScalablyTypedConverterPlugin))
  .dependsOn(api.js, common.js)
  .settings(
    name := "wiringbits-lib-ui",
    useYarn := true,
    scalacOptions += "-Ymacro-annotations",
    Test / requireJsDomEnv := true,
    Test / fork := false, // sjs needs this to run tests
    stTypescriptVersion := "3.9.3",
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List("@material-ui/core", "@material-ui/styles", "@material-ui/icons"),
    Compile / npmDependencies ++= Seq(
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
      "@types/classnames" -> "2.2.10",
      "react-router" -> "5.1.2",
      "@types/react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2",
      "@types/react-router-dom" -> "5.1.2"
    ),
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "scala-java-time" % "2.0.0",
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.1.1",
      "com.alexitc" %%% "sjs-material-ui-facade" % "0.2.0",
      "net.wiringbits" %%% "slinky-utils" % webappUtils,
      "org.scalatest" %%% "scalatest" % "3.2.15" % Test,
      "com.beachape" %%% "enumeratum" % "1.7.0"
    )
  )

lazy val server = (project in file("server"))
  .dependsOn(common.jvm, api.jvm)
  .configure(baseServerSettings, commonSettings, playSettings)
  .settings(
    name := "wiringbits-server",
    fork := true,
    Test / fork := true, // allows for graceful shutdown of containers once the tests have finished running
    libraryDependencies ++= Seq(
      "org.playframework.anorm" %% "anorm" % anorm,
      "org.playframework.anorm" %% "anorm-akka" % anorm,
      "org.playframework.anorm" %% "anorm-postgres" % anorm,
      "com.typesafe.play" %% "play-json" % "2.9.4",
      "org.postgresql" % "postgresql" % "42.6.0",
      "de.svenkubiak" % "jBCrypt" % "0.4.3",
      "commons-validator" % "commons-validator" % "1.7",
      "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.40.12" % "test",
      "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.40.12" % "test",
      "com.softwaremill.sttp.client3" %% "core" % sttp % "test",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % sttp % "test",
      "net.wiringbits" %% "admin-data-explorer-play-server" % webappUtils,
      "software.amazon.awssdk" % "ses" % "2.17.141",
      "jakarta.xml.bind" % "jakarta.xml.bind-api" % "4.0.0",
      "org.apache.commons" % "commons-text" % "1.10.0",
      "io.swagger" % "swagger-annotations" % swagger,
      // JAX-B dependencies for JDK 9+, required to use play sessions
      "javax.xml.bind" % "jaxb-api" % "2.3.1",
      "javax.annotation" % "javax.annotation-api" % "1.3.2",
      "javax.el" % "javax.el-api" % "3.0.0",
      "org.glassfish" % "javax.el" % "3.0.0",
      "com.beachape" %% "enumeratum" % "1.7.2"
    )
  )

lazy val webBuildInfoSettings: Project => Project = _.enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoKeys ++= {
      val apiUrl = sys.env.get("API_URL")
      val values = Seq(
        "apiUrl" -> apiUrl
      )
      // Logging these values is useful to make sure that the necessary settings
      // are being overriden when packaging the app.
      sLog.value.info(s"BuildInfo settings:\n${values.mkString("\n")}")
      values.map(t => BuildInfoKey(t._1, t._2))
    },
    buildInfoPackage := "net.wiringbits",
    buildInfoUsePackageAsPath := true
  )

lazy val web = (project in file("web"))
  .dependsOn(common.js, api.js, ui)
  .enablePlugins(ScalablyTypedConverterPlugin)
  .configure(
    baseWebSettings,
    browserProject,
    commonSettings,
    reactNpmDeps,
    withCssLoading,
    bundlerSettings,
    webBuildInfoSettings
  )
  .settings(
    name := "wiringbits-web",
    useYarn := true,
    webpackDevServerPort := 8080,
    stFlavour := Flavour.Slinky,
    stReactEnableTreeShaking := Selection.All,
    stUseScalaJsDom := true,
    Compile / stMinimize := Selection.All,
    // material-ui is provided by a pre-packaged library
    stIgnore ++= List("@material-ui/core", "@material-ui/styles", "@material-ui/icons"),
    Compile / npmDependencies ++= Seq(
      "@material-ui/core" -> "3.9.4", // note: version 4 is not supported yet
      "@material-ui/styles" -> "3.0.0-alpha.10", // note: version 4 is not supported yet
      "@material-ui/icons" -> "3.0.2",
      "@types/classnames" -> "2.2.10",
      "react-router" -> "5.1.2",
      "@types/react-router" -> "5.1.2",
      "react-router-dom" -> "5.1.2",
      "@types/react-router-dom" -> "5.1.2",
      "react-google-recaptcha" -> "2.1.0",
      "@types/react-google-recaptcha" -> "2.1.0"
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % playJson,
      "com.softwaremill.sttp.client3" %%% "core" % sttp,
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.1.1",
      "com.alexitc" %%% "sjs-material-ui-facade" % "0.2.0",
      "net.wiringbits" %%% "scalablytyped-facades" % webappUtils,
      "io.monix" %%% "monix-reactive" % "3.4.1"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %%% "scalatest" % "3.2.15" % Test
    )
  )

lazy val adminBuildInfoSettings: Project => Project = _.enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoKeys ++= {
      val apiUrl = sys.env.get("API_URL")
      val values = Seq(
        "apiUrl" -> apiUrl
      )
      // Logging these values is useful to make sure that the necessary settings
      // are being overriden when packaging the app.
      sLog.value.info(s"BuildInfo settings:\n${values.mkString("\n")}")
      values.map(t => BuildInfoKey(t._1, t._2))
    },
    buildInfoPackage := "net.wiringbits",
    buildInfoUsePackageAsPath := true
  )

lazy val admin = (project in file("admin"))
  .dependsOn(api.js)
  .enablePlugins(ScalaJSBundlerPlugin)
  .configure(browserProject, bundlerSettings, adminBuildInfoSettings)
  .settings(
    name := "wiringbits-admin",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
    webpackDevServerPort := 8081,
    webpackBundlingMode := BundlingMode.LibraryOnly(),
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.client3" %%% "core" % sttp,
      "net.wiringbits" %%% "admin-data-explorer-web" % webappUtils
    ),
    Compile / npmDependencies ++= Seq(
      "react" -> "17.0.0",
      "react-dom" -> "17.0.0",
      "react-scripts" -> "5.0.0",
      "react-admin" -> "4.1.0",
      "ra-ui-materialui" -> "4.1.0",
      "ra-data-simple-rest" -> "4.1.0",
      "ra-i18n-polyglot" -> "4.1.0",
      "ra-language-english" -> "4.1.0",
      "ra-core" -> "4.1.0",
      "@mui/material" -> "5.8.1",
      "@emotion/styled" -> "11.8.1"
    )
  )

lazy val root = (project in file("."))
  .aggregate(
    common.jvm,
    common.js,
    api.jvm,
    api.js,
    ui,
    server,
    web,
    admin
  )
  .settings(
    publish := {},
    publishLocal := {}
  )

addCommandAlias("dev-web", ";web/fastOptJS::startWebpackDevServer;~web/fastOptJS")
addCommandAlias("dev-admin", ";admin/fastOptJS::startWebpackDevServer;~admin/fastOptJS")
