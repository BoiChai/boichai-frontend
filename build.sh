#! /bin/sh

export GOARCH="amd64"
export GOOS="linux"
export CGO_ENABLED=0

glide install .
go build -o boichai-frontend -v .
docker build . -t repo.treescale.com/boichaiapp/frontend:1.0.0

rm ./boichai-frontend
