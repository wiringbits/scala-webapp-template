// while there are some eviction errors, plugins seem to be compatible so far
evictionErrorLevel := sbt.util.Level.Warn

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.1")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.19")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.13.1")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")

addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta39")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.1.3")

addSbtPlugin("com.github.dwickern" % "sbt-swagger-play" % "0.5.0")
