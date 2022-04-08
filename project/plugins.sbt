
logLevel := Level.Warn

val seleniumEnvVersion = "1.1.0"
val seleniumEnv = Seq( "org.scala-js" %% "scalajs-env-selenium" % seleniumEnvVersion )
libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"

libraryDependencies ++= seleniumEnv

addSbtPlugin("org.scala-js" % "sbt-scalajs-env-phantomjs" % "1.0.0")

addSbtPlugin( "com.eed3si9n" % "sbt-assembly" % "0.14.6" )
addSbtPlugin( "com.typesafe.sbt" % "sbt-native-packager" % "1.3.2" )
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.9.0")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2" )
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
