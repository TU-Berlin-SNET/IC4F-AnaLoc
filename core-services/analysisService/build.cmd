mvn clean install && docker stop analysis-service & docker rm analysis-service & docker rmi beierle/analoc:analysis-service & docker build --tag=beierle/analoc:analysis-service .