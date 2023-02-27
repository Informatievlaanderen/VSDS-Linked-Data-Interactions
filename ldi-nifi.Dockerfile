ARG NIFI_DOCKER_IMAGE_VERSION

#
# INSTALL MAVEN DEPENDENCIES
#
FROM maven:3.8.5-openjdk-18 AS builder

# MAVEN: application
FROM builder as app-stage
COPY . .
RUN mvn clean net.revelc.code.formatter:formatter-maven-plugin:format install -DskipTests

FROM ldes/nifi:${NIFI_DOCKER_IMAGE_VERSION} AS packaging-stage

COPY --from=app-stage ldi-nifi/ldi-nifi-processors/create-version-object-processor/target/*.nar /opt/nifi/nifi-current/lib/
COPY --from=app-stage ldi-nifi/ldi-nifi-processors/ldes-client-processor/target/*.nar /opt/nifi/nifi-current/lib/
COPY --from=app-stage ldi-nifi/ldi-nifi-processors/sparql-interactions-processor/target/*.nar /opt/nifi/nifi-current/lib/
COPY --from=app-stage ldi-nifi/ldi-nifi-processors/version-materialisation-processor/target/*.nar /opt/nifi/nifi-current/lib/


ADD *.nar /opt/nifi/nifi-current/lib/
RUN chown nifi:nifi /opt/nifi/nifi-current/lib/*
RUN rm -rf *.db *.db-* ldes-client-processor/*.db ldes-client-processor/*.db-*
RUN chmod -R 664 /opt/nifi/nifi-current/lib/*.nar
USER nifi
