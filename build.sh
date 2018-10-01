#! /bin/sh

export GOARCH="amd64"
export GOOS="linux"
export CGO_ENABLED=0

glide install .
go build -o boichai-frontend -v .
docker build . -t boichai-frontend:latest

rm ./boichai-frontend
