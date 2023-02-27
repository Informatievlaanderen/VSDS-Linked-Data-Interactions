# syntax=docker/dockerfile:1

FROM maven:3.8.5-openjdk-18 AS builder

# MAVEN: application
FROM builder as app-stage
COPY . .
RUN mvn clean install -DskipTests

#
# RUN THE APPLICATION
#
FROM openjdk:18-ea-bullseye
RUN apt-get update & apt-get upgrade

COPY --from=app-stage ldi-orchestrator/ldio-application/target/ldio-application.jar ./

COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-http-in/target/ldio-http-in-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-ldes-client/target/ldio-ldes-client-jar-with-dependencies.jar ./lib/

COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-sparql-construct/target/ldio-sparql-construct-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-version-materialiser/target/ldio-version-materialiser-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-version-object-creator/target/ldio-version-object-creator-jar-with-dependencies.jar ./lib/

COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-console-out/target/ldio-console-out-jar-with-dependencies.jar ./lib/
COPY --from=app-stage ldi-orchestrator/ldio-connectors/ldio-http-out/target/ldio-http-out-jar-with-dependencies.jar ./lib/

ARG NIFI_DOCKER_IMAGE_VERSION

FROM ldes/nifi:1.20.0-jdk18
ADD --chown=nifi:nifi *.nar /opt/nifi/nifi-current/lib/
RUN rm -rf *.db *.db-* ldes-client-processor/*.db ldes-client-processor/*.db-*
RUN chmod -R 664 /opt/nifi/nifi-current/lib/*.nar
USER nifi
