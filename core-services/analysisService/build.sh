#!/bin/bash
mvn clean install
docker stop analysis-service
docker rm analysis-service
docker rmi beierle/analoc:analysis_service
docker build --tag=beierle/analoc:analysis_service .