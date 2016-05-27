#!/bin/bash

set -x
set -e

#
# Build binaries using docker
#
docker build -t auth-${BUILD_NUMBER} -f Dockerfile.build .

#
# Prepare distribution folder
#
rm -rf dist
mkdir dist
# Note: so far, on can't `docker cp` from image. Would make this even simpler
# see https://github.com/docker/docker/issues/16079
container=$(docker create auth-${BUILD_NUMBER})
# docker cp lack support for wildcard, we can't use target/*.jar
# see https://github.com/docker/docker/issues/7710
docker cp $container:/work/target/app.jar dist/app.jar
docker rm $container
docker rmi auth-${BUILD_NUMBER}
cp Dockerfile.prod dist/Dockerfile

#
# Build production docker image
#
docker build -t cfpio/auth dist

#
# Push to Dockerhub
#
docker tag cfpio/auth cfpio/auth:1.0.${BUILD_NUMBER}
docker push cfpio/auth:1.0.${BUILD_NUMBER}

docker tag cfpio/auth cfpio/auth:latest
docker push cfpio/auth:latest