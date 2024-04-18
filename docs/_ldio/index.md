---
title: The Linked Data Interactions Orchestrator
layout: home
nav_order: 0
---

# The Linked Data Interactions Orchestrator

A lightweight application maintained by the LDI team. Its creation came when a more lightweight alternative for [Apache NiFi] was needed.

## Docker Compose

The easiest way to start working with the LDIO is by using Docker. The image is located on the [Docker Hub](https://hub.docker.com/r/ldes/ldi-orchestrator/tags).

To set up your environment, start by creating a new folder dedicated to your LDIO project. Within this folder, create two files: a `docker-compose.yml` and a YAML configuration file.
The YAML file can be named according to your preference and can be added to the volume bindings pointing to the `ldio/application.yml` file.

To enable Swagger UI, debug logging, or monitoring, please follow the instructions provided below on how to incorporate them into the LDIO YAML configuration file.

***docker-compose.yml***:
````yaml
version: '3.3'
services:
  ldio-workbench:
    container_name: ldio-workbench
    image: ldes/ldi-orchestrator:2.4.0-SNAPSHOT
    volumes:
      - ./ldio.config.yml:/ldio/application.yml:ro
    ports:
      - "<port>:8080"
````

Once configured with the LDIO config, execute the command
````shell
docker compose up
````

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

## Enable swagger UI

To use the swagger UI on your own LDIO deployment you can add the below config,
and go to `<base-url>/v1/swagger`.

```yaml
springdoc:
  swagger-ui:
    path: /v1/swagger
```

## LDIO DEBUG Logging

To enable logging the input model for a 
* [LDIO Adapter](./ldio-adapters)
* [LDIO Transformer](./ldio-transformers)
* [LDIO Output](./ldio-outputs)

Make sure you 

* Add the following property in your application config:
    ````yaml
    logging:
        level:
            be.vlaanderen.informatievlaanderen: DEBUG
    ````
* Add the ```debug: true``` property to your transformer or output config.

## LDIO Logging & Monitoring

To provide a better insight in the workings in the LDIO, we expose a prometheus endpoint (`/actuator/prometheus`) that
encloses some metrics (with included tags):

* ldio_data_in_total: Number (Amount of items passed at the start of Transformer Pipeline)
  * pipeline: String (Refers to the pipeline name)
  * ldio_type: String (Refers to the LDIO Input Type of pipeline)
* ldio_data_out_total: Number (Amount of items passed at the end of Transformer Pipeline)
  * pipeline: String (Refers to the pipeline name)

To consult these metrics, make sure the prometheus endpoint is enabled by setting
the following setting:

````yaml
management:
  endpoints:
    web:
      exposure:
        include:
          - prometheus
````

[Apache NiFi]: https://nifi.apache.org/
