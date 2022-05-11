#!/bin/bash

## Bundle scalajs app with the raw js dependencies (which are
## built using `build-raw-js.sh`

rm frontend/app-js/src/main/*
if [ $1="prod" ]; then
  # for prod, only move js over (no maps)
  cp frontend/app/target/scala-2.12/app-opt/*.js frontend/app-js/src/main/
else
  cp frontend/app/target/scala-2.12/app-fastopt/* frontend/app-js/src/main/
fi

cd frontend/app-js || exit 1
npm install --legacy-peer-deps || exit 1
if [ $1="prod" ]; then
   npx webpack --mode="production"
else
   npx webpack --mode="development"
fi

