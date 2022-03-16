#!/bin/bash

## Bundle scalajs app with the raw js dependencies (which are
## built using `build-js.sh`

cd frontend/app/target/scala-2.12/scalajs-bundler/main
rm -rf node_modules/dart-ui-*
rm -rf package-lock.json
npm cache clean --force
npm cache verify
npm install
npx webpack --config app.config.js
