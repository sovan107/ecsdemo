ARG TARGETPLATFORM=linux/amd64
FROM --platform=$TARGETPLATFORM eclipse-temurin:17-jdk-alpine
RUN apk add curl
VOLUME /tmp
EXPOSE 8080
ADD target/demo-ecs.jar demo-ecs.jar
ENTRYPOINT ["java", "-jar", "/demo-ecs.jar"]