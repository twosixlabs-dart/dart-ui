#!/bin/bash

## This script is used to build and bundle the application. Use this when you
## have made updates to any raw js component of the project and want to refresh
## the application *while it's already running*. You will only have to hit refresh
## in the browser to see changes.

scripts/build-js.sh
scripts/bundle-app.sh
