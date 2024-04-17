---
title: Linked Data Interactions For Apache NiFi
layout: home
nav_order: 0
---

# Linked Data Interactions For Apache NiFi

[Apache Nifi] is an easy to use, powerful, and reliable system to process and distribute data.

## Deliverables

As a deliverable, we provide a list of processors that can be imported in NiFi:

- [Create Version Processor](../core/ldi-transformers/version-object-creator)
- [GeoJson to WKT Processor](../core/ldi-transformers/geojson-to-wkt)
- [Json to Json LD Processor](../core/ldi-adapters/json-to-json-ld)
- [Ldes Client Processor](../core/ldi-inputs/ldes-client)
- [Ngsi V2 to LD Processor](../core/ldi-adapters/ngsiv2-to-ld)
- [RDF4j Repository Materialization Processor](../core/ldi-outputs/repository-materialiser)
- [SPARQL Interactions Processor](./processors/sparql-interactions)
- [Version Materialization Processor](../core/ldi-transformers/version-materializer)

## Set up NiFi instance with LDI processors

The processors can be imported into a NiFi docker instance via volume binding:

1. Create a `docker-compose.yml` file with the following content in a new directory
    ````yaml
    services:
      nifi:
        image: apache/nifi:2.0.0-M2
        environment:
          SINGLE_USER_CREDENTIALS_USERNAME: admin
          SINGLE_USER_CREDENTIALS_PASSWORD: ctsBtRBKHRAx69EqUghvvgEvjnaLjFEB
        ports:
          - 8443:8443
        volumes:
          - ./nifi-ext:/opt/nifi/nifi-current/nar_extensions:rw
    ````
2. Create a directory `nifi-ext` in your current directory.
3. Download the required processors (.nar extension)
   from [the nexus repository](https://s01.oss.sonatype.org/#nexus-search;quick~be.vlaanderen.informatievlaanderen.ldes.ldi.nifi)
   and place these in the `nifi-ext` directory.
4. Finally, start your instance.
    ````shell
    docker compose up
    ````
5. Log in at `https://localhost:8443/nifi` with the credentials mentioned in step 1
6. All downloaded extensions are available under the ``be.vlaanderen.informatievlaanderen.ldes.ldi.nifi`` group.

{: .note }
All documentation and notes about configuration are available in the NiFi component itself.

[Apache NiFi]: https://nifi.apache.org/