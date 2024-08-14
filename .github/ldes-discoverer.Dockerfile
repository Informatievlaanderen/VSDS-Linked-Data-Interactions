# syntax=docker/dockerfile:1

FROM amazoncorretto:21-alpine-jdk

RUN adduser -D -u 2000 ldes-discoverer
USER ldes-discoverer
WORKDIR /ldes-discoverer

COPY ./ldi-extensions/ldes-discoverer/target/ldes-discoverer.jar ./lib/

ENTRYPOINT ["java", "-jar", "./lib/ldes-discoverer.jar"]