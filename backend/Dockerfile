FROM openjdk:12-jdk-alpine
VOLUME /tmp
ARG JAR_FILE = backend\build\libs\backend-0.0.1-SNAPSHOT-plain.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ "java" , "-jar", "/app.jar" ]
