FROM openjdk:8-jre-alpine
MAINTAINER Teng
ADD target/ServiceAnalyzer-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Djava.net.preferIPv4Stack=true", "-jar","app.jar"]