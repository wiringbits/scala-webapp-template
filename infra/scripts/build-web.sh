#!/bin/bash
set -e
API_URL=$1
echo "API_URL=$API_URL" \
  && cd ../ \
  && API_URL=$API_URL sbt web/build \
  && cd -
cd ../web/build && zip -r web.zip * && cd -
mkdir -p apps && mv ../web/build/web.zip apps/web.zip
