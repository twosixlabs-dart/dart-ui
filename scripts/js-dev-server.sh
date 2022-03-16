#!/bin/bash

## Starts webpack dev server for a js-only version of DART-UI. Use this script when working
## on the JS side of DART-UI for faster build times and hot updates

. scripts/set-dev-env.sh

scripts/inject-config.sh

cd ../..

docker-compose up -d

cd frontend/app-js

npx webpack-dev-server

cd ..

docker-compose down
