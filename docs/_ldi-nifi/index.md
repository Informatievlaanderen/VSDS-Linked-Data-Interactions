---
title: Linked Data Interactions For Apache NiFi
layout: home
nav_order: 0
---

# Linked Data Interactions For Apache NiFi

[Apache Nifi] is an easy to use, powerful, and reliable system to process and distribute data.

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
3. Download either the `...-nar-bundle.jar` and unpack this or download the individual required processors (.nar extension) from the [nexus repository].
   Next, place the required processors in the `nifi-ext` directory.
4. Finally, start your instance.
    ````shell
    docker compose up
    ````
5. Log in at `https://localhost:8443/nifi` with the credentials mentioned in step 1
6. All downloaded extensions are available under the ``be.vlaanderen.informatievlaanderen.ldes.ldi.nifi`` group.

{: .note }
All documentation and notes about configuration are available in the NiFi component itself.

[Apache NiFi]: https://nifi.apache.org/
[nexus repository]: https://s01.oss.sonatype.org/#nexus-search;quick~be.vlaanderen.informatievlaanderen.ldes.ldi.nifi