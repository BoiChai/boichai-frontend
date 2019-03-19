FROM openjdk:8-jre-alpine

RUN apk update && apk add ca-certificates && rm -rf /var/cache/apk/*
RUN apk add --update curl && rm -rf /var/cache/apk/*

RUN mkdir -p /app

WORKDIR /app

COPY ./config.json /app/config.json
COPY ./config10k.json /app/config10k.json
COPY ./config25k.json /app/config25k.json
COPY ./config50k.json /app/config50k.json
COPY ./build/libs /app/libs
