#!/bin/bash

## Build all of the raw js packages (not including config, which is injected
## at runtime)

cd frontend/scala13-components || exit 1
npm install --legacy-peer-deps || exit 1
npm run package || exit 1

cd ../components || exit 1
npm install || exit 1
npm run package || exit 1

cd ../app-js || exit 1
npm install || exit 1
npm run package || exit 1
