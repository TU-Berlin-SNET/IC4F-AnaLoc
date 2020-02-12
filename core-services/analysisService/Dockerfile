FROM flink:1.6.0-alpine

MAINTAINER Yong Wu <y.wu.1@campus.tu-berlin.de>


VOLUME /tmp
# Flink environment variables
ENV FLINK_INSTALL_PATH=/opt
ENV FLINK_LIB_DIR $FLINK_HOME/lib

ARG JAR_FILE=analysisService-0.0.2.jar

# copy ft jar to container
COPY target/$JAR_FILE $FLINK_LIB_DIR/job.jar

COPY docker-entrypoint.sh /
COPY wait-for-it.sh /

USER flink
EXPOSE 8081 6123 8080 8111
ENTRYPOINT ["bash","/wait-for-it.sh"]
#ENTRYPOINT ["bash","/docker-entrypoint.sh"]
CMD ["--help"]



