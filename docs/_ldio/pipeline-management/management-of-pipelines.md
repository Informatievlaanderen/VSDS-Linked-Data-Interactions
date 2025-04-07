---
layout: default
parent: Pipeline Management
title: Management of Pipelines
nav_order: 1
---

# Management of Pipelines

Pipelines in LDIO can be created in YAML or JSON configuration (although all example configurations are made in YAML,
these can also be formatted in JSON).

A default pipeline looks as follows:

```yaml
  name: my-first-pipeline
  input:
    name: name of LDI Input
    config:
      foo: bar
    adapter:
      name: name of LDI Adapter
      config:
        foo: bar
  transformers:
    - name: name of LDI Transformer
      config:
        foo: bar
  outputs:
    - name: name of LDI Transformer
      config:
        foo: bar
```

- Note that one orchestrator can have multiple pipelines
- Note that one pipeline can have multiple LDI Transformers and LDI Outputs

## Anatomy of a pipeline

Each pipeline is built up of the following components:

* [LDIO Input](ldi-inputs): A component that will receive data (not necessarily LD) to then feed the LDIO pipeline.
* [LDIO Adapter](ldi-adapters): To be used in conjunction with the LDIO Input, the LDIO Adapter will transform the
  provided content into and internal Linked Data model and sends it down the pipeline.
* [LDIO Transformer](ldi-transformers): A component that takes in a Linked Data model, transforms/modifies it and then
  puts it back on the pipeline.
* [LDIO Output](ldi-outputs): A component that will take in Linked Data and will export it to external sources.

````mermaid
stateDiagram-v2
    direction LR

    LDI_Input --> LDI_Transformer : LD
    LDI_Transformer --> LDI_Output : LD

    state LDI_Input {
        direction LR
        [*] --> LDI_Adapter : Non LD

        state LDI_Adapter {
            direction LR
            [*] --> adapt
            adapt --> [*]
        }

        LDI_Adapter --> [*] : LD
    }
    
    state LDI_Transformer {
        direction LR
        [*] --> transform
        transform --> [*]
    }
    state LDI_Output {
        direction LR
        [*] --> [*]
    }
````

## Persistence of Pipelines

By default, all pipelines defined after startup (via management API) will be lost on restart.

To prevent this behaviour, add the `orchestrator.directory` property as follows:

```yaml
orchestrator:
  directory: "{directory in application folder}"
```

If this directory does not exist, it will be created.

> **_NOTE:_**  An application config can be defined by creating an application YAML file in the LDIO directory
(in docker, this correlates to `/ldio/application.yml`).


## Pausing & Resuming LDIO

Sometimes it might be preferred to pause an LDIO pipeline instead of deleting and recreating it.
The endpoints to manage pipelines can be found [here](pipeline-api.md)

The exact behaviour of a paused pipeline depends on its input component and can be found in the [documentation of these components](docs/_ldio/ldio-inputs/index.md).
However, it will always complete its current run through the pipeline and then seize sending any output.

## Using properties in pipelines

You can externalize configuration from your pipelines. This is especially handy when you're using infrastructure configuration.
This works in the same way as you can externalize configuration in [Spring boot](https://docs.spring.io/spring-boot/reference/features/external-config.html).
The property placeholders will be resolved when you initialize your pipeline. 

* Note that you can only use property placeholders in the config objects of your input, adapters, transformers and outputs.

For example,

```yaml
  name: my-first-pipeline
  input:
    name: name of LDI Input
    config:
      password: ${DB_PASSWORD:postgres}
```

lets you make use of the value of the DB_PASSWORD environment variable. When this variable is not specified, the default value (in our example "postgres") will be used.