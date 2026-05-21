// while there are some eviction errors, plugins seem to be compatible so far
evictionErrorLevel := sbt.util.Level.Warn

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")

addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.10")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.21.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1")

addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta45")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.1")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.5.7")
