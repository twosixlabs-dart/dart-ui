#!/bin/bash

## Starts webpack dev server for a js-only version of DART-UI. Use this script when working
## on the JS side of DART-UI for faster build times and hot updates

. scripts/set-dev-env.sh

scripts/inject-config.sh

cd ../..

docker-compose up -d || exit 1

sbt scala13Components/fullLinkJS

cd frontend/scala13-components || exit 1
npm install --legacy-peer-deps || exit 1
npm run package || exit 1

cd ../components || exit 1
npm install --legacy-peer-deps || exit 1
npm run package || exit 1

cd ../app-js || exit 1

npx webpack-dev-server || exit 1

cd .. || exit 1

docker-compose down
