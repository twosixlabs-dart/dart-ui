import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

    // Common
    val uPickleVersion = "1.3.8"
    val scalaTestVersion = "3.2.7"
    val uTestVersion = "0.7.10"
    val dartAuthVersion = "3.1.11"

    val uPickle = Def.setting( Seq( "com.lihaoyi" %%% "upickle" % uPickleVersion ) )
    val scalaTest = Def.setting( Seq( "org.scalatest" %%% "scalatest" % scalaTestVersion % "test" ) )
    val uTest = Def.setting( Seq( "com.lihaoyi" %%% "utest" % uTestVersion % "test" ) )
    val dartAuthCore = Def.setting( Seq( "com.twosixlabs.dart.auth" %%% "core" % dartAuthVersion ) )

    // Backend
    val scalaMockVersion = "4.4.0"
    val slf4jVersion = "1.7.20"
    val logbackVersion = "1.2.3"
    val postgresVersion = "42.2.10"
    val h2Version = "1.4.200"
    val c3p0Version = "0.9.5.1"
    val slickVersion = "3.3.3"
    val slf4jNopVersion = "1.6.4"
    val slickPgVersion = "0.19.4"
    val akkaVersion = "2.6.14"
    val akkaHttpVersion = "10.2.4"
    val scalaTagsVersion = "0.9.4"
    val circeYamlVersion = "0.12.0"
    val betterFilesVersion = "3.8.0"
    val ontologyRegistryVersion = "3.0.19"
    val conceptsApiVersion = "3.0.5"

    val scalaMock = Seq( "org.scalamock" %% "scalamock" % scalaMockVersion % "test" )

    val logging = Seq( "org.slf4j" % "slf4j-api" % slf4jVersion,
        "ch.qos.logback" % "logback-classic" % logbackVersion )
    val akka = Seq(
        "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
        "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
    )
    val database = Seq( "org.postgresql" % "postgresql" % postgresVersion,
        "com.mchange" % "c3p0" % c3p0Version,
        "com.typesafe.slick" %% "slick" % slickVersion,
        //                        "org.slf4j" % "slf4j-nop" % slf4jNopVersion,
        "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
        "com.github.tminglei" %% "slick-pg" % slickPgVersion )
    val circeYaml = Seq(
        "io.circe" %% "circe-yaml" % circeYamlVersion,
        "io.circe" %% "circe-generic" % circeYamlVersion,
        "io.circe" %% "circe-parser" % circeYamlVersion,
        "io.circe" %% "circe-generic-extras" % circeYamlVersion,
    )
    val betterFiles = Seq( "com.github.pathikrit" %% "better-files" % betterFilesVersion )
    val dartAuthControllers = Seq( "com.twosixlabs.dart.auth" %% "controllers" % dartAuthVersion )
    val arrangoTenants = Seq( "com.twosixlabs.dart.auth" %% "arrango-tenants" % dartAuthVersion )
    val ontologyRegistry = Seq( "com.twosixlabs.dart.ontologies" %% "ontology-registry-services" % ontologyRegistryVersion )
    val conceptsApi = Seq( "com.twosixlabs.dart.concepts" %% "concepts-client" % conceptsApiVersion )

    // Frontend
    val scalaJsDomVersion = "1.1.0"
    val scalaJsReactVersion = "1.7.7"
    val scalaJsReactBeautifulDndVersion = "0.4.0"
    val scalaCssVersion = "0.7.0"
    val diodeVersion = "1.1.8"
    val testStateVersion = "2.4.1"
    val scalaTimeVersion = "2.2.2"

    val scalaTags = Def.setting( Seq( "com.lihaoyi" %%% "scalatags" % scalaTagsVersion ) )
    val scalaJsDom = Def.setting( Seq( "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion ) )
    val testState = Def.setting( Seq(
        "com.github.japgolly.test-state" %%% "core" % testStateVersion % "test",
        "com.github.japgolly.test-state" %%% "dom-zipper" % testStateVersion % "test",
//        "com.github.japgolly.test-state" %%% "dom-zipper-selenium" % testStateVersion % "test",
        "com.github.japgolly.test-state" %%% "ext-scalajs-react" % testStateVersion % "test",
    ) )
    val scalaJsReact = Def.setting( Seq(
        "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "extra" % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "test" % scalaJsReactVersion % Test,
    ) )
    val scalaJsReactBeautifulDnd = Def.setting( Seq(
        "com.rpiaggio" %%% "scalajs-react-beautiful-dnd" % scalaJsReactBeautifulDndVersion,
    ) )
    val scalaTime = Def.setting( Seq(
        "io.github.cquiroz" %%% "scala-java-time" % scalaTimeVersion,
    ) )
    val scalaCss = Def.setting( Seq(
        "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
        "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion,
    ) )
    val diode = Def.setting( Seq(
        "io.suzaku" %%% "diode" % diodeVersion,
        "io.suzaku" %%% "diode-react" % "1.1.14",
    ) )

}

