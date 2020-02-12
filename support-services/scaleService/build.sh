#!/bin/bash
mvn clean install
docker stop scale-service
docker rm scale-service
docker rmi beierle/analoc:scale_service
docker build --tag=beierle/analoc:scale_service .
