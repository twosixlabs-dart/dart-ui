#!/bin/bash

. scripts/set-dev-env.sh

cd frontend/config-js

npm install

npx webpack
