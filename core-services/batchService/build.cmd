mvn clean install && docker stop batch-service & docker rm batch-service & docker rmi beierle/analoc:batch-service & docker build --tag=beierle/analoc:batch-service .