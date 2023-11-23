---
title: The Linked Data Interactions Orchestrator
layout: home
nav_order: 0
---

# The Linked Data Interactions Orchestrator

A lightweight application maintained by the LDI team. Its creation came when a more lightweight alternative for [Apache NiFi] was needed.

## Setup Basic Configuration

To set up a basic LDIO configuration, all that is needed is passing a YAML configuration.

This can look as follows:

````yaml
orchestrator:
  pipelines:
    - name: my-first-pipeline
      input:
        name: fully-qualified name of LDI Input
        config:
          foo: bar
        adapter:
          name: fully-qualified name of LDI Adapter
          config:
            foo: bar
      transformers:
        - name: fully-qualified name of LDI Transformer
          config:
            foo: bar
      outputs:
        - name: fully-qualified name of LDI Transformer
          config:
            foo: bar
````

- Note that one orchestrator can have multiple pipelines 
- Note that one pipeline can have multiple LDI Transformers and LDI Outputs 

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
