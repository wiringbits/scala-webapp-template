#!/bin/bash
set -e
APP_SOURCE_ZIP=$1
echo "APP_SOURCE_ZIP=$APP_SOURCE_ZIP"
cd ../ && sbt server/dist && cd -
mkdir -p apps && cp ../server/target/universal/$APP_SOURCE_ZIP apps/server.zip
