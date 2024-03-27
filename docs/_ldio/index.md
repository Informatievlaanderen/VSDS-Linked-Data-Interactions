---
title: The Linked Data Interactions Orchestrator
layout: home
nav_order: 0
---

# The Linked Data Interactions Orchestrator

A lightweight application maintained by the LDI team. Its creation came when a more lightweight alternative for [Apache NiFi] was needed.

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
