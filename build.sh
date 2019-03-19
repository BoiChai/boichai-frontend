#!/bin/bash

version=$1
docker build -t datacultr/push-broker:flash-load-java-${version} .
