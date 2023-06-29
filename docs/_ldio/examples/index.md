---
layout: default
title: Examples
has_children: true
has_toc: true
nav_order: 10
---
# Linked Data Interactions Orchestrator Examples

The easiest way to start working with the LDIO is by using Docker.
We'll go through the setup of the service here.

***docker-compose.yml***:
````yaml
version: '3.3'
services:
  ldio-workbench:
    container_name: ldio-workbench
    image: ldes/ldi-orchestrator:1.1.0
    volumes:
      - ./ldio.config.yml:/ldio/application.yml:ro
    ports:
      - "<port>:8080"
````

Through the table of contents, you will find examples of the LDIO config that can be placed inside the ***ldio.config.yml***
to get the desired results.

{: .note }
> If any extra files are required for a processor (mapping/queries/...), you can add them in your volume binding  pointing to the ``ldio`` folder as follows:
>
> Note that the name given for the file can be whatever, as long as it is unique.
>
> ``- ./file.extension:/ldio/file.extension:ro``

{: .note }
> If any custom processors have been created, you can add the jars in your volume binding pointing to the ``ldio/lib`` folder as follows:
>
> Note that the name given for the jar file can be whatever, as long as it is unique.
>
> ``- <path to custom processor>:/ldio/lib/custom-processor.jar:ro``

## Execution

Once configured with the LDIO config, execute the command
````shell
docker compose up
````