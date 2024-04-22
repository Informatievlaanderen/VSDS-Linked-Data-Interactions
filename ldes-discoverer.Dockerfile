# syntax=docker/dockerfile:1

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.9.6-amazoncorretto-21 AS builder

# MAVEN: application
FROM builder AS app-stage
COPY . .
RUN mvn clean install -DskipTests

#
# RUN THE APPLICATION
#
FROM amazoncorretto:21-alpine-jdk

RUN adduser -D -u 2000 ldes-discoverer
USER ldes-discoverer
WORKDIR /ldes-discoverer

COPY --from=app-stage ldi-extensions/ldes-discoverer/target/ldes-discoverer.jar ./lib/

ENTRYPOINT ["java", "-jar", "./lib/ldes-discoverer.jar"]