#!/bin/bash

set -x
set -e

docker build -t cfpio/auth .

#
# Push to Dockerhub
#
docker tag cfpio/auth cfpio/auth:1.0.${BUILD_NUMBER}
docker push cfpio/auth:1.0.${BUILD_NUMBER}

docker tag cfpio/auth cfpio/auth:latest
docker push cfpio/auth:latest