mvn clean install && docker stop discovery-service & docker rm discovery-service & docker rmi beierle/analoc:discovery_service & docker build --tag=beierle/analoc:discovery_service .
