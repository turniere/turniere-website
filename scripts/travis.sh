#!/bin/bash
DOCKER_IMAGE="turniere/turniere-website"

if [ $TRAVIS_PULL_REQUEST != "false" ]
then
    echo "Pull Request => Skipping docker build and push"
    exit 0
fi

# build docker image
mvn docker:build

# build docker image tag
BRANCH_REV="${TRAVIS_BRANCH}-$(git rev-parse --short HEAD)"

# tag and push docker with branch + branch revision
IMAGE_BRANCH_REV="${DOCKER_IMAGE}:${BRANCH_REV}"
docker tag ${DOCKER_IMAGE} $IMAGE_BRANCH_REV
docker push $IMAGE_BRANCH_REV

# tag and push image with branch
IMAGE_BRANCH="${DOCKER_IMAGE}:${TRAVIS_BRANCH}"
docker tag ${DOCKER_IMAGE} $IMAGE_BRANCH
docker push $IMAGE_BRANCH

if [ $TRAVIS_BRANCH == "master" ]
then
    docker push ${DOCKER_IMAGE}:latest
fi
