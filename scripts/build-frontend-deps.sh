#!/bin/bash

## Build raw javascript dependencies

cd frontend/scala13-components || exit 1
npm install --legacy-peer-deps || exit 1
npm run package || exit 1

cd ../components || exit 1
npm install --legacy-peer-deps || exit 1
npm run package || exit 1
