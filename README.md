# DART UI

## Overview

This application is the visual user-interface for several DART functionalities:

1. Document Upload (Forklift): submit documents with or without custom metadata for ingestion by 
   the DART pipeline
2. Corpus Exploration (Corpex): search corpora by extracted text, extracted metadata, or analytic 
   annotations.
3. Concepts Explorer: build and publish ontologies used by connected reader technologies.

## Dependencies
DART-UI is a Scala application, using Akka-http for the backend and a combination Scala.js and ES6 
for the frontend. In addition to the publicly available libraries it uses, it has dependencies on a 
number of other Scala, Java, and JavaScript libraries. In order to build DART-UI these dependencies 
must be accessible via the local filesystem (in the SBT cache) or over the network via 
[Sonatype Nexus](https://www.sonatype.com/products/repository-oss-download) and npm where they are 
published.

## Building
This project is built using SBT, npm, and webpack. For more information on installation and 
configuration of SBT please [see the documentation](https://www.scala-sbt.org/1.x/docs/)

The build configuration is considerably more complicated than most standard sbt configurations 
due to the fact that build outputs of the frontend scala.js subproject are required as inputs for 
the backend scala-JVM subproject. Custom sbt tasks are defined in the `build.sbt` to ensure that 
the bundled JavaScript outputs are injected into a resources directory in the backend.

These tasks are also parameterized by two different configurations: `prod` and `dev`. Running 
build tasks in the `prod` configuration will optimize the raw JavaScript outputs both in 
compilation and in bundling. `dev` will compile and bundle more quickly and will generate mapped 
outputs allowing easier debugging in the browser; however it will also produce much larger 
bundles.

To build and test the code (>2G heap space recommended):
```bash
sbt clean test
````

To create a runnable fat-JAR (>2G heap space recommended):
```bash
sbt clean dev:assembleApp
```
or
```bash
sbt clean prod:assembleApp
```

To create a Docker image of the runnable application:
```bash
make docker-build
```

## Development Environment

To run locally with necessary services, it is recommended to use the following script:

```bash 
./scripts/dev-sbt.sh
```

This will launch all the necessary backend services via docker-compose, and will 
populate all the necessary environment parameters to build a frontend that will communicate 
with those services. The command will start an `sbt` session from which you can use `dev:runApp`
or `prod:runApp` to build and launch the web application. You can access the application by 
navigating to `http://localhost:8080`. To terminate the application, just hit `Enter` at the 
command line.

You can also run a pure-js-only version of the application, which lacks `Concepts Explorer` and 
the full nav menu. This is recommended if you are working on the javascript components (`Corpex` 
and `Forklift`) and want to see an updated build without having to bundle it with scala.js outputs, 
which can take a long time. To stand up a hot-reloading JS build, use the following script (uses 
webpack-dev-server):

```bash 
./scripts/js-dev-server.sh
```

## Runtime Configuration

Configuration for the application is defined in two places:
* backend/server/src/main/resources/application.conf
* frontend/config-js/src/config/env/default.js

Most configuration inputs either require or can be overridden at runtime via environment 
variables:

| Name                        | Description                                                    | Example Values            |
|-----------------------------|----------------------------------------------------------------|---------------------------|
| DART_ENABLE_LOGGING         | Boolean: enables high-level debugging component in the UI      | `"true"` or `"false"`     | 
| DART_LOG_MAX_LENGTH         | Maximum of logging messages to be cached in UI if enabled      | `10000`                   |
| DART_PUBLIC_REPORT_DURATION | Duration in ms for an error/alert to be displayed in the UI    | `3000`                    |  
| CLUSTERING_TEST_MODE        | Boolean: test mode mocks clustering service, using random data | `"true"` or `"false"`     |
| CLUSTERING_SCHEME           | Scheme for accessing clustering service (defaults to http)     | `https`                   |
| CLUSTERING_HOST             | Clustering hostname or ip                                      | `api.clustering.org`      |
| CLUSTERING_PORT             | Port of clustering service                                     | `8080`                    |
| ARANGODB_DATABASE           | namespace of the canonical document database                   | `dart`                    |
| ARANGODB_HOST               | host of the canonical database                                 | `dart.arangodb.com`       |
| ARANGODB_PORT               | port of the canonical database                                 | `8529`                    |
| ARANGODB_CONNECTION_POOL    | size of the connection pool for ArangoDB                       | `5`                       |
| POSTGRES_HOST               | host of the postgres database                                  | `dart.postgres.com`       |
| POSTGRES_PORT               | port for the postgres database                                 | `5432`                    |
| POSTGRES_USER               | user of the postgres database                                  | `dart`                    |
| POSTGRES_PASSWORD           | password of the postgres database                              | `my_pwd`                  |
| POSTGRES_DB                 | postgres database name                                         | `dart`                    |
| POSTGRES_TIMEOUT            | timeout for postgres requests in minutes                       | `1`                       |
| POSTGRES_MAX_CONNECTIONS    | maximum allowed number of concurrent postgress connections     | `6`                       |
| KAFKA_BOOTSTRAP_SERVERS     | base url for kafka provider                                    | `kafka-broker-1:19092`    |
| BACKEND_PUBLIC_DIR          | directory where static files are served                        | `/opt/app/public`         |
| DART_BASE_PATH              | base path for REST requests                                    | `/concepts/explorer`      |
| DART_AUTH_SECRET            | secret used for reading auth token                             | `xxxxxxxxxxxx`            |
| DART_AUTH_BYPASS            | disable authentication/authorization                           | `false`                   |

## Project structure

The build is divided into a tree of subprojects:

```
root
 |
 |-- backend
 |     |
 |     |-- services
 |     |
 |      -- server
 |
 |-- common
 |
  -- frontend
       |
       |-- app
       |
       |-- app-js
       |
       |-- components
       |
       |-- config-js
       |
        -- scala13-components
```

Within `backend`, `server` contains the backend application that serves the frontend UI and integrates 
with DART and external services via utilities defined in the `services` module. All of the code in these 
two modules within `backend` run only the JVM.

`common` contains data models and service definitions common to the backend and the frontend. All code 
in this module must be able to compile both to JVM bytecode and to Javascript.

`frontend` contains all code and assets that are run in the browser. `app` contains the Scala.js React 
application that constitutes the UI. `app-js` contains the pure JS components of DART-UI, including 
Corpex and Forkfift. `components` contains reusable Scala.js components used by `app`, which are 
published as Maven artifacts for external use, as well as any pure JS code required by these. 
`config-js` contains the configuration code which is built by webpack at runtime in order to inject 
configuration pulled from environment variables into the frontend application. Finally, 
`scala13-components` contains any scalajs components that can only be compiled using scala 2.13. 


## WARNINGS

### Material-UI

MUI css injection can create problems if you aren't careful about imports.
Make sure all imports of core components are in the form of:
```javascript
import { Component } from '@material-ui/core';
```
or
```scala
@JSImport( "@material-ui/core", "Component" )
object ComponentMui extends js.Object
```

Also note that the `app.config.js` webpack config used by `scala-js-bundler`
needs to include aliases for `@material-ui/core` and `@material-ui/style` so
that only one instance of each component and styles object is used in the
application.
