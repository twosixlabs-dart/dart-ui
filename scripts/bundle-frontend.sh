#!/bin/bash

## Bundle scalajs app with the raw js dependencies (which are
## built using `build-raw-js.sh`

echo "Bundling Frontend"
echo "$0 $1"

rm frontend/app-js/src/main/*
echo "Copying scala.js artifacts to app-js"
mkdir -p frontend/app-js/src/main
if [[ $1 == "prod" ]]; then
  echo "Copying prod scalajs artifacts"
  ls frontend/app/target/scala-2.12/app-opt/
  cp frontend/app/target/scala-2.12/app-opt/*.js frontend/app-js/src/main/
else
  echo "Copying dev scalajs artifacts"
  ls frontend/app/target/scala-2.12/app-fastopt/
  cp frontend/app/target/scala-2.12/app-fastopt/* frontend/app-js/src/main/
fi
ls frontend/app-js/src/main/

cd frontend/app-js || exit 1
npm install --legacy-peer-deps || exit 1
if [[ $1 == "prod" ]]; then
   echo "Running webpack in production mode"
   npx webpack --mode="production"
else
   echo "Running webpack in development mode"
   npx webpack --mode="development"
fi

cd ../..

