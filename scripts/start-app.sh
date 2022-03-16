#!/bin/bash

## This script is only meant to be run inside the docker container. It injects
## the configuration js into the public directory and then runs the application.

cd $APP_DIR/config-js
npx webpack
$APP_DIR/run.sh
