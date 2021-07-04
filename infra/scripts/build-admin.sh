#!/bin/bash
set -e
API_URL=$1
echo "API_URL=$API_URL" \
  && cd ../ \
  && API_URL=$API_URL sbt admin/build \
  && cd -
cd ../admin/build && zip -r admin.zip * && cd -
mv ../admin/build/admin.zip apps/admin.zip
