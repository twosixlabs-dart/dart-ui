#!/bin/bash

./scripts/build-frontend-deps.sh && ./scripts/bundle-frontend.sh "$1"
