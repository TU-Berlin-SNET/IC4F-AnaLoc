mvn clean install && docker stop monitor-service & docker rm monitor-service & docker rmi beierle/analoc:monitor_service & docker build --tag=beierle/analoc:monitor_service .
