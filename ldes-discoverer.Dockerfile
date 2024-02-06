# syntax=docker/dockerfile:1

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.8.5-amazoncorretto-17 AS builder

# MAVEN: application
FROM builder AS app-stage
COPY . .
RUN mvn clean install -DskipTests

#
# RUN THE APPLICATION
#
FROM amazoncorretto:17-alpine-jdk

RUN adduser -D -u 2000 ldes-discoverer
USER ldes-discoverer
WORKDIR /ldes-discoverer

COPY --from=app-stage ldi-ldes-discoverer/target/ldi-ldes-discoverer.jar ./lib/

ENTRYPOINT ["java", "-jar", "./lib/ldi-ldes-discoverer.jar"]