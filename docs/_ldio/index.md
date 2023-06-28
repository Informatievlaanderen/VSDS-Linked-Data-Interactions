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

## LDIO Process Flow 

````mermaid
sequenceDiagram
    LDI Input->>+LDI Adapter: Received Content.
    loop For every Linked Data Model
        LDI Adapter->>-LDI Input: Returns Model
        
    end
    LDI Input->>ComponentExecutor: Process Model
    loop For every LDI Transformer in pipeline
        ComponentExecutor->>+LDI Transformer: Start Transformation
        LDI Transformer->>-ComponentExecutor: Return Transformed Model
    end

    loop For every LDI Output in pipeline
        ComponentExecutor->>+LDI Output: Start Output
        Note right of LDI Output: Model exported!
    end
````


[Apache NiFi]: https://nifi.apache.org/
