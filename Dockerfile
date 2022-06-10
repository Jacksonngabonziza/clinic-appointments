FROM openjdk:8-jdk-alpine

COPY target/appointment-0.0.1-SNAPSHOT.jar appointment-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/appointment-0.0.1-SNAPSHOT.jar"]

