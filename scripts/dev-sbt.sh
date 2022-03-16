#!/bin/bash

## Start sbt with env parameters necessary for running a local development environment.
## If you have the docker-compose system running, you can run the application by executing
## dev:runApp or prod:runApp in sbt.

. scripts/set-dev-env.sh

docker-compose up -d

sbt

docker-compose down
