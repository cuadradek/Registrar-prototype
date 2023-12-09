FROM openjdk:17
COPY .. /opt/registrar
WORKDIR /opt/registrar
ENTRYPOINT exec java $JAVA_OPTS -Dspring.config.location=/opt/config/application.properties -jar registrar-2.0-SNAPSHOT.jar
