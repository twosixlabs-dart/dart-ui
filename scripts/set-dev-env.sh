#!/bin/bash

## This script simply sets the necessary environment parameters to run dart-ui in local
## development mode

export DART_AUTH_BYPASS=true

# Raw-JS DART-UI parameters
export CLUSTER_TEST_MODE=true
export PROCUREMENT_URL=http://localhost:8092
export SEARCH_URL=http://localhost:8088

# Scala.js DART-UI parameters
export CLUSTERING_TEST_MODE=false
export CLUSTERING_HOST=localhost
export CLUSTERING_PORT=8091
#export AUTH_SERVER_URL=http://localhost:8090/auth
export ARANGODB_HOST=localhost
export KAFKA_BOOTSTRAP_SERVERS=localhost:19092
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_USER=dart
export POSTGRES_PASSWORD=dart_pass
export POSTGRES_DATABASE=dart_db
export BACKEND_PUBLIC_DIR=$(pwd)/public

# Sbt parameters
export SBT_OPTS="-Xmx4G -Xss2M"
