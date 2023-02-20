# syntax=docker/dockerfile:1

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.8.5-openjdk-18 AS builder

# MAVEN: application
FROM builder as app-stage
COPY . /
RUN mvn install -DskipTests

#
# RUN THE APPLICATION
#
FROM openjdk:18-ea-bullseye
RUN apt-get update & apt-get upgrade

COPY --from=app-stage ldto-application/target/ldto-application-1.0.0-SNAPSHOT.jar ./
#COPY --from=app-stage ldto-starter-kit/target/ldto-starter-kit-1.0.0-SNAPSHOT.jar ./lib/
#COPY --from=app-stage ldes-server-infra-mongo/target/ldes-server-infra-mongo-jar-with-dependencies.jar ./lib/

RUN useradd -u 2000 ldes
USER ldes

CMD ["java", "-cp", "ldto-application-1.0.0-SNAPSHOT.jar", "-Dloader.path=lib/", "org.springframework.boot.loader.PropertiesLauncher"]
