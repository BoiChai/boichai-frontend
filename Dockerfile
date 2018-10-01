FROM golang:alpine

RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*

WORKDIR /

COPY public /etc/public
COPY templates /etc/templates
COPY boichai-frontend /usr/local/bin/

ENTRYPOINT ["boichai-frontend"]
