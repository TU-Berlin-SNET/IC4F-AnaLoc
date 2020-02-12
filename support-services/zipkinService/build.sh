#!/bin/bash
mvn clean install
docker stop zipkin-service
docker rm zipkin-service
docker rmi beierle/analoc:zipkin_service
docker build --tag=beierle/analoc:zipkin_service .
