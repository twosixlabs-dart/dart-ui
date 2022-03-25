#!/bin/bash

## Generates the configuration script required by the application to run. This can be
## executed while the application is live: after refreshing the page in the web browser,
## the application will have the new configuration.

. scripts/set-dev-env.sh

cd frontend/config-js

npm install

npx webpack
