#!/bin/bash

rm -rf frontend/app-js/node_modules
rm -rf frontend/app-js/dist
rm -rf frontend/app-js/*.tgz
rm -rf frontend/app-js/package-lock.json

rm -rf frontend/components/node_modules
rm -rf frontend/components/dist
rm -rf frontend/components/*.tgz
rm -rf frontend/components/package-lock.json

rm -rf frontend/scala13-components/node_modules
rm -rf frontend/scala13-components/dist
rm -rf frontend/scala13-components/*.tgz
rm -rf frontend/scala13-components/package-lock.json

rm -rf frontend/config-js/node_modules
rm -rf frontend/config-js/package-lock.json

rm -rf frontend/app/target/scala-2.12/scalajs-bundler

rm -rf public/*
