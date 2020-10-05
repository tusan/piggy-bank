FROM adoptopenjdk/openjdk11:alpine-slim
VOLUME /data
ARG JAR_FILE=piggy-bank-backend.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
