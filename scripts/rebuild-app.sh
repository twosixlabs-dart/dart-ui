#!/bin/bash

## This script is used to build the pure JS components of the application and rebuild the app
## by bundling it with existing Scala.js outputs

## Use this script if the app is already running via `sbt runApp` an you have made some changes
## to the pure JS parts.

scripts/build-js.sh
scripts/bundle-app.sh
