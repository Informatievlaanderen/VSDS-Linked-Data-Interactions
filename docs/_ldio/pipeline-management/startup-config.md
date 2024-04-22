---
layout: default
parent: Pipeline Management
title: Startup Configuration
---

### Startup Configuration

On startup, pipelines can be defined by creating an application YAML file in the LDIO directory
(in docker, this correlates to `/ldio/application.yml`) that looks as follows:

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
