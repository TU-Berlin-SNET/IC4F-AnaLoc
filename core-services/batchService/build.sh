#!/bin/bash
mvn clean install
docker stop batch-service
docker rm batch-service
docker rmi beierle/analoc:batch_service
docker build --tag=beierle/analoc:batch_service .
