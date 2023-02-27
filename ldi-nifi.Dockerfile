
ARG NIFI_DOCKER_IMAGE_VERSION

FROM ldes/nifi:${NIFI_DOCKER_IMAGE_VERSION}
ADD --chown=nifi:nifi *.nar /opt/nifi/nifi-current/lib/
RUN rm -rf *.db *.db-* ldes-client-processor/*.db ldes-client-processor/*.db-*
RUN chmod -R 664 /opt/nifi/nifi-current/lib/*.nar
USER nifi
