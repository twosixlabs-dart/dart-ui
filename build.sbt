import sbt._

import scala.sys.process._
import Dependencies._
import sbtassembly.AssemblyPlugin.autoImport.assemblyMergeStrategy
import scalajsbundler.util.JSON

import scala.language.postfixOps

import org.scalajs.jsenv.nodejs.NodeJSEnv

/*
   ##############################################################################################
   ##                                                                                          ##
   ##                                  SETTINGS DEFINITIONS                                    ##
   ##                                                                                          ##
   ##############################################################################################
 */

// integrationConfig and wipConfig are used to define separate test configurations for
// integration testing
// and work-in-progress testing
lazy val IntegrationConfig = config( "integration" ) extend ( Test )
lazy val WipConfig = config( "wip" ) extend ( Test )

// JS configurations
lazy val DevConfig = config( "dev" )
lazy val ProdConfig = config( "prod" )

lazy val commonSettings = {
	inConfig( IntegrationConfig )( Defaults.testTasks ) ++
	  inConfig( WipConfig )( Defaults.testTasks ) ++
	  Seq(
		  organization := "com.twosixlabs.dart.ui",
		  scalaVersion := "2.12.7",
		  resolvers ++= Seq(
			  "Maven Central" at "https://repo1.maven.org/maven2/",
			  "Sonatype snapshots" at "https://s01.oss.sonatype" +
                ".org/content/repositories/snapshots/",
			  "JCenter" at "https://jcenter.bintray.com",
			  "Local Ivy Repository" at s"file://${
				  System
					.getProperty( "user.home" )
			  }/.ivy2/local/default"
		  ),
		  addCompilerPlugin(
			  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
		  ),
		  javacOptions ++= Seq( "-source", "1.8", "-target", "1.8" ),
		  scalacOptions ++= Seq( "-target:jvm-1.8" ),
		  useCoursier := false,
		  //        libraryDependencies,
		  // `sbt test` should skip tests tagged IntegrationTest
		  Test / testOptions := Seq( Tests
			.Argument( "-l", "com.twosixlabs.dart.test.tags.annotations.IntegrationTest" )
		  ),
		  Test / parallelExecution := false,
		  //         `sbt integration:test` should run only tests tagged IntegrationTest
		  IntegrationConfig / parallelExecution := false,
		  IntegrationConfig / testOptions := Seq( Tests
			.Argument( "-n", "com.twosixlabs.dart.test.tags.annotations.IntegrationTest" )
		  ),
		  //         `sbt wip:test` should run only tests tagged WipTest
		  WipConfig / testOptions := Seq( Tests
			.Argument( "-n", "com.twosixlabs.dart.test.tags.annotations.WipTest" )
		  ),
		  WipConfig / parallelExecution := false,
	  )
}

lazy val disablePublish = Seq(
	skip.in( publish ) := true,
)

lazy val assemblySettings = Seq(
	assemblyMergeStrategy in assembly := {
		case PathList( "META-INF", xs@_* ) => MergeStrategy.discard // may have to adjust this
		case PathList( "META-INF", "MANIFEST.MF" ) => MergeStrategy.discard
		case PathList( "reference.conf" ) => MergeStrategy.concat
		case x => MergeStrategy.last
	},
	test in assembly := {},
	mainClass in(Compile, packageBin) := Some( "Main" ),
)

sonatypeProfileName := "com.twosixlabs"
inThisBuild(List(
	organization := "com.twosixlabs.dart.ui",
	homepage := Some(url("https://github.com/twosixlabs-dart/dart-ui")),
	licenses := List("GNU-Affero-3.0" -> url("https://www.gnu.org/licenses/agpl-3.0.en.html")),
	developers := List(
		Developer(
			"twosixlabs-dart",
			"Two Six Technologies",
			"",
			url("https://github.com/twosixlabs-dart")
		)
	)
))

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"


/*
   ##############################################################################################
   ##                                                                                          ##
   ##                                  PROJECT DEFINITIONS                                     ##
   ##                                                                                          ##
   ##############################################################################################
 */

lazy val root = ( project in file( "." ) )
  .disablePlugins( sbtassembly.AssemblyPlugin )
  .aggregate( frontend, backend, common )
  .settings(
	  name := "dart-ui",
	  disablePublish,
	  cleanFiles ++= ( baseDirectory.value / "public" ).allPaths.get(),
  )

lazy val common = ( project in file( "common" ) )
  .configs( IntegrationConfig, WipConfig )
  .enablePlugins( ScalaJSPlugin )
  .disablePlugins( sbtassembly.AssemblyPlugin )
  .settings(
	  commonSettings,
	  testFrameworks += new TestFramework( "utest.runner.Framework" ),
	  libraryDependencies ++= uPickle.value ++ uTest.value ++ scalaTest.value,
  )

lazy val backend = ( project in file( "backend" ) )
  .disablePlugins( sbtassembly.AssemblyPlugin )
  .aggregate( api, services, server, utilities )
  .settings(
	  disablePublish,
  )

lazy val api = ( project in file( "backend/api" ) )
  .dependsOn( common )
  .configs( IntegrationConfig, WipConfig )
  .disablePlugins( sbtassembly.AssemblyPlugin )
  .settings(
	  commonSettings,
	  libraryDependencies ++= logging ++ uPickle.value ++ scalaTest.value,
  )

lazy val services = ( project in file( "backend/services" ) )
  .dependsOn( api, common ) // % "compile->compile;test->test;compile->test" )
  .configs( IntegrationConfig, WipConfig )
  .disablePlugins( sbtassembly.AssemblyPlugin )
  .settings(
	  commonSettings,
	  //      Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat,
	  testFrameworks += new TestFramework( "utest.runner.Framework" ),
	  libraryDependencies ++= akka
		++ ontologyRegistry
		++ conceptsApi
		++ arrangoTenants
		++ logging
		++ circeYaml
		++ betterFiles
		++ uTest.value
		++ scalaTest.value,
	  disablePublish,
  )

lazy val server = ( project in file( "backend/server" ) )
  .dependsOn( api, services % "compile->compile;test->test", common % "compile->compile;test->test" )
  .configs( IntegrationConfig, WipConfig )
  .enablePlugins( JavaAppPackaging )
  .settings(
	  commonSettings,
	  Compile / unmanagedResourceDirectories += baseDirectory.value / "webapp",
	  libraryDependencies ++= akka
		++ logging
		++ scalaMock
		++ scalaTags.value
		++ dartAuthCore.value
		++ arrangoTenants
		++ ontologyRegistry
		++ dartAuthControllers
		++ scalaTest.value,
	  dependencyOverrides ++= Seq(
		  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.5",
		  "com.arangodb" %% "velocypack-module-scala" % "1.2.0",
		  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.5",
		  "com.twosixlabs.dart" %% "dart-arangodb-datastore" % "3.0.16",
		  "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.5",
	  ),
	  assemblySettings,
  )

lazy val utilities = ( project in file( "backend/utilities" ) )
  .dependsOn( api, services % "compile->compile;test->test", common % "compile->compile;test->test" )
  .configs( IntegrationConfig, WipConfig )
  .enablePlugins( JavaAppPackaging )
  .settings(
	  commonSettings,
	  Compile / unmanagedResourceDirectories += baseDirectory.value / "webapp",
	  libraryDependencies ++= akka
		++ logging
		++ scalaMock
		++ scalaTags.value
		++ arrangoTenants
		++ ontologyRegistry
		++ scalaTest.value,
	  dependencyOverrides ++= Seq(
		  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.5",
		  "com.arangodb" %% "velocypack-module-scala" % "1.2.0",
		  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.5",
		  "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.5",
	  ),
	  assemblySettings,
  )

lazy val frontend = ( project in file( "frontend" ) )
  .enablePlugins( ScalaJSPlugin )
  .disablePlugins( sbtassembly.AssemblyPlugin )
  .aggregate( components, app )
  .settings(
	  disablePublish,
  )

lazy val components = ( project in file( "frontend/components" ) )
  .dependsOn( common % "compile->compile;test->test" )
  .enablePlugins( ScalaJSPlugin, ScalaJSBundlerPlugin, JSDependenciesPlugin )
  .configs( IntegrationConfig, WipConfig )
  .disablePlugins( sbtassembly.AssemblyPlugin )
  .settings(
	  commonSettings,
	  testFrameworks += new TestFramework( "utest.runner.Framework" ),
	  requireJsDomEnv in Test := true,
	  libraryDependencies ++= scalaJsDom.value
		++ scalaJsReact.value
		++ scalaCss.value
		++ diode.value
		++ scalaTime.value
		++ scalaTags.value
		++ uTest.value
		++ testState.value,
	  disablePublish,
	  npmDependencies in Compile ++= Seq(
		  "react" -> "^17.0.2",
		  "react-dom" -> "^17.0.2",
		  "prop-types" -> "^15.7.2",
		  "@material-ui/core" -> "^4.11.4",
		  "@material-ui/icons" -> "^4.11.2",
		  "lodash" -> "^4.17.15",
		  "keycloak-js" -> "^12.0.2",
	  ),
	  npmDevDependencies in Compile ++= Seq(
		  "html-webpack-plugin" -> "4.5.2",
		  "dynamic-cdn-webpack-plugin" -> "5.0.0",
		  "module-to-cdn" -> "3.1.5",
		  "@babel/core" -> "^7.12.0",
		  "@babel/plugin-proposal-class-properties" -> "^7.10.4",
		  "@babel/preset-env" -> "^7.12.0",
		  "@babel/preset-react" -> "^7.10.4",
		  "@babel/register" -> "^7.12.0",
		  "babel-eslint" -> "^10.1.0",
		  "babel-loader" -> "^8.1.0",
		  "babel-plugin-syntax-dynamic-import" -> "^6.18.0",
		  "eslint" -> "^7.27.0",
		  "eslint-config-airbnb" -> "^18.2.1",
		  "eslint-config-react" -> "^1.1.7",
		  "eslint-loader" -> "^4.0.2",
		  "eslint-plugin-import" -> "^2.22.1",
		  "eslint-plugin-jsx-a11y" -> "^6.3.1",
		  "eslint-plugin-react" -> "^7.21.4",
	  ),
	  scalaJSLinkerConfig ~= { _.withBatchMode( true ) },
	  webpackConfigFile in Test := Some( baseDirectory
		.value / ".." / "app" / "app.config.test.js"
	  ),
  )

lazy val scala13Components = ( project in file( "frontend/scala13-components" ) )
  .enablePlugins( ScalaJSPlugin )
  .configs( IntegrationConfig, WipConfig )
  .disablePlugins( sbtassembly.AssemblyPlugin )
  .settings(
	  {
		  inConfig( IntegrationConfig )( Defaults.testTasks ) ++
			inConfig( WipConfig )( Defaults.testTasks ) ++
			Seq(
				organization := "com.twosixlabs.dart.taxonomy.explorer",
				scalaVersion := "2.13.4",
				resolvers ++= Seq(
					"Maven Central" at "https://repo1.maven.org/maven2/",
					"Sonatype snapshots" at "https://s01.oss.sonatype" +
                      ".org/content/repositories/snapshots/",
					"JCenter" at "https://jcenter.bintray.com",
					"Local Ivy Repository" at s"file://${
						System
						  .getProperty( "user.home" )
					}/.ivy2/local/default"
				),
				javacOptions ++= Seq( "-source", "1.8", "-target", "1.8" ),
				scalacOptions ++= Seq( "-target:jvm-1.8" ),
				useCoursier := false,
				Test / testOptions := Seq( Tests
				  .Argument( "-l", "com.twosixlabs.dart.test.tags.annotations.IntegrationTest" )
				),
				Test / parallelExecution := false,
				//         `sbt integration:test` should run only tests tagged IntegrationTest
				IntegrationConfig / parallelExecution := false,
				IntegrationConfig / testOptions := Seq( Tests
				  .Argument( "-n", "com.twosixlabs.dart.test.tags.annotations.IntegrationTest" )
				),
				//         `sbt wip:test` should run only tests tagged WipTest
				WipConfig / testOptions := Seq( Tests
				  .Argument( "-n", "com.twosixlabs.dart.test.tags.annotations.WipTest" )
				),
				WipConfig / parallelExecution := false,
			)
	  },
	  libraryDependencies ++= scalaJsDom.value
		++ scalaJsReact.value
		++ scalaJsReactBeautifulDnd.value,
	  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
	  disablePublish,
  )

lazy val app = ( project in file( "frontend/app" ) )
  .dependsOn( common % "compile->compile;test->test", components % "compile->compile;test->test" )
  .enablePlugins( ScalaJSPlugin, ScalaJSBundlerPlugin, JSDependenciesPlugin )
  .configs( IntegrationConfig, WipConfig )
  .settings(
	  commonSettings,
	  testFrameworks := Seq( new TestFramework( "utest.runner.Framework" ) ),
	  libraryDependencies ++= scalaJsDom.value
		++ scalaJsReact.value
		++ scalaCss.value
		++ scalaTags.value
		++ diode.value
		++ uPickle.value
		++ testState.value
		++ scalaTime.value
		++ uTest.value
		++ dartAuthCore.value
		:+ "com.github.pathikrit" %% "better-files" % betterFilesVersion % Test,
	  npmDependencies in Compile ++= Seq(
		  "react" -> "^17.0.2",
		  "react-dom" -> "^17.0.2",
		  "prop-types" -> "^15.7.2",
		  "@material-ui/core" -> "^4.11.4",
		  "@material-ui/icons" -> "^4.11.2",
		  "@material-ui/lab" -> "^4.0.0-alpha.56",
		  "lodash" -> "^4.17.15",
		  "keycloak-js" -> "^12.0.2",
		  "react-pdf" -> "^4.2.0",
		  "react-redux" -> "^7.2.1",
		  "react-router-dom" -> "^6.2.1",
		  "react-virtualized" -> "^9.22.3",
		  "redux" -> "^4.1.2",
		  "redux-thunk" -> "^2.4.1",
		  "typeface-roboto" -> "0.0.75",
		  "react-outside-click-handler" -> "^1.3.0",
		  "react-beautiful-dnd" -> "13.0.0",
		  "dart-ui-components" -> "file:../../../../../components/dart-ui-components-1.0.0.tgz",
		  "dart-ui-scala13-components" -> "file:../../../../../scala13-components/dart-ui-scala13-components-1.0.0.tgz",
	  ),
	  npmDevDependencies in Compile ++= Seq(
		  "html-webpack-plugin" -> "4.5.2",
		  "dynamic-cdn-webpack-plugin" -> "5.0.0",
		  "module-to-cdn" -> "3.1.5",
		  "@babel/core" -> "^7.12.0",
		  "@babel/plugin-proposal-class-properties" -> "^7.10.4",
		  "@babel/preset-env" -> "^7.12.0",
		  "@babel/preset-react" -> "^7.10.4",
		  "@babel/register" -> "^7.12.0",
		  "@open-wc/webpack-import-meta-loader" -> "^0.4.7",
		  "babel-eslint" -> "^10.1.0",
		  "babel-loader" -> "^8.1.0",
		  "babel-plugin-syntax-dynamic-import" -> "^6.18.0",
		  "eslint" -> "^7.27.0",
		  "eslint-config-airbnb" -> "^18.2.1",
		  "eslint-config-react" -> "^1.1.7",
		  "eslint-loader" -> "^4.0.2",
		  "eslint-plugin-import" -> "^2.22.1",
		  "eslint-plugin-jsx-a11y" -> "^6.3.1",
		  "eslint-plugin-react" -> "^7.21.4",
		  "worker-loader" -> "^3.0.5",
		  "scalajs-friendly-source-map-loader" -> "^0.1.5",
		  "webpack" -> "^4.28.0",
	  ),
	  scalaJSUseMainModuleInitializer := true,
	  requireJsDomEnv in Test := true,
	  scalaJSLinkerConfig ~= ( _.withModuleKind( ModuleKind.CommonJSModule ) ),
	  scalaJSLinkerConfig ~= { _.withBatchMode( true ) },
	  additionalNpmConfig := Map( "legacy-peer-deps" -> JSON.bool( true ) ),
	  npmExtraArgs := Seq( "--legacy-peer-deps" ),
	  webpackConfigFile in Test := Some( baseDirectory.value / "app.config.test.js" ),
	  webpack / version := "4.28.4",
  )

/*
   ##############################################################################################
   ##                                                                                          ##
   ##                                    TASK DEFINITIONS                                      ##
   ##                                                                                          ##
   ##############################################################################################
 */


val buildFrontendDeps = taskKey[ Unit ]( "Build the scala2.13 and raw js dependencies of frontend scalajs code necessary for testing" )

ProdConfig / buildFrontendDeps := {
	( scala13Components / Compile / fullLinkJS ).value
	()
}
ProdConfig / buildFrontendDeps := {
	( ProdConfig / buildFrontendDeps ).value
	Seq( "/bin/sh", "-c", "./scripts/build-frontend-deps.sh", "prod" ) !
}

DevConfig / buildFrontendDeps := {
	( scala13Components / Compile / fastLinkJS ).value
	()
}
DevConfig / buildFrontendDeps := {
	( DevConfig / buildFrontendDeps ).value
	Seq( "/bin/sh", "-c", "./scripts/build-frontend-deps.sh", "dev" ) !
}

buildFrontendDeps := ( DevConfig / buildFrontendDeps ).value

// Run this if you want to build the frontend from scratch, including all js dependencies
// that aren't updated frequently
val assembleJsFull = taskKey[ Unit ]( "Build entire frontend and bundle with raw-js" )

ProdConfig / assembleJsFull := {
	( scala13Components / Compile / fullLinkJS ).value
	( app / Compile / fullLinkJS ).value
}
ProdConfig / assembleJsFull := {
	( ProdConfig / assembleJsFull ).value
	Seq( "/bin/sh", "-c", "./scripts/build-frontend.sh prod" ) !
}

DevConfig / assembleJsFull := {
	( scala13Components / Compile / fastLinkJS ).value
	( app / Compile / fastLinkJS ).value
}
DevConfig / assembleJsFull := {
	( DevConfig / assembleJsFull ).value
	Seq( "/bin/sh", "-c", "./scripts/build-frontend.sh dev" ) !
}

assembleJsFull := ( DevConfig / assembleJsFull ).value

// Run this if you want to rebundle the frontend after changing either the scalajs part or the
// raw-js dart-ui part. (Doesn't rebuild scala2.13 part or raw components js part)
val assembleJsMain = taskKey[ Unit ]( "Build main parts of frontend and bundle with raw-js" )

ProdConfig / assembleJsMain := {
	( app / Compile / fullLinkJS ).value
}
ProdConfig / assembleJsMain := {
	( ProdConfig / assembleJsMain ).value
	Seq( "/bin/sh", "-c", "./scripts/bundle-frontend.sh", "prod" ) !
}

DevConfig / assembleJsMain := {
	( app / Compile / fastLinkJS ).value
}
DevConfig / assembleJsMain := {
	( DevConfig / assembleJsMain ).value
	Seq( "/bin/sh", "-c", "./scripts/bundle-frontend.sh", "dev" ) !
}

assembleJsMain := ( DevConfig / assembleJsMain ).value

// Compile Application
val compileJs = taskKey[ Unit ]( "Compile frontend components" )

ProdConfig / compileJs := {
	( scala13Components / Compile / compile ).value
	( app / Compile / compile ).value
}

DevConfig / compileJs := {
	( scala13Components / Compile / compile ).value
	( app / Compile / compile ).value
}

compileJs := ( DevConfig / compileJs ).value

val injectConf = taskKey[ Unit ]( "Compile js config file" )

ProdConfig / injectConf := {
	Seq( s"scripts/inject-config.sh", "prod" ) !
}

DevConfig / injectConf := {
	Seq( s"scripts/inject-config.sh", "dev" ) !
}

injectConf := ( DevConfig / injectConf ).value

// Build Application and package
val assembleApp = taskKey[ Unit ]( "Build fatjar of application" )

ProdConfig / assembleApp := {
	( ProdConfig / assembleJsFull ).value
	( server / Compile / assembly ).value
}

DevConfig / assembleApp :=  {
	( DevConfig / assembleJsFull ).value
	( server / Compile / assembly ).value
}

assembleApp := ( DevConfig / assembleApp ).value

// Compile and run application, building from scratch
val runAppFull = taskKey[ Unit ]( "Assemble js and run server to run the complete application" )

ProdConfig / runAppFull := {
	( ProdConfig / assembleJsFull ).value
	( ProdConfig / injectConf ).value
}
ProdConfig / runAppFull := Def.sequential(
	ProdConfig / runAppFull,
	( server / Compile / run ).toTask( " -i" ),
).value

DevConfig / runAppFull := {
	( DevConfig / assembleJsFull ).value
	( DevConfig / injectConf ).value
}
DevConfig / runAppFull := Def.sequential(
	DevConfig / runAppFull,
	( server / Compile / run ).toTask( " -i" ),
).value

runAppFull := ( DevConfig / runAppFull ).value

// Compile and run application, only rebuilding main scala-js and raw-js dart-ui components
val runAppMain = taskKey[ Unit ]( "Assemble js and run server to run the complete application" )

ProdConfig / runAppMain := {
	( ProdConfig / assembleJsMain ).value
	( ProdConfig / injectConf ).value
}
ProdConfig / runAppMain := Def.sequential(
	ProdConfig / runAppMain,
	( server / Compile / run ).toTask( " -i" ),
).value

DevConfig / runAppMain := {
	( DevConfig / assembleJsMain ).value
	( DevConfig / injectConf ).value
}
DevConfig / runAppMain := Def.sequential(
	DevConfig / runAppMain,
	( server / Compile / run ).toTask( " -i" ),
).value

runAppMain := ( DevConfig / runAppMain ).value
