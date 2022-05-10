#!/bin/bash

## Build all of the raw js packages (not including config, which is injected
## at runtime)



cd frontend/scala13-components || exit 1
#npm install --legacy-peer-deps || exit 1
#npm run package || exit 1

cd ../components || exit 1
#npm install --legacy-peer-deps || exit 1
#npm run package || exit 1

cd ..
if [ $1="prod" ]; then
  # for prod, only move js over (no maps)
  cp app/target/scala-2.12/app-opt/*.js app-js/src/main/
else
  cp app/target/scala-2.12/app-fastopt/* app-js/src/main/
fi

cd app-js || exit 1
npm install --legacy-peer-deps || exit 1
if [ $1="prod" ]; then
   npx webpack --mode="production"
else
   npx webpack --mode="development"
fi
