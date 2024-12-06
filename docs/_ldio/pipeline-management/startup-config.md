---
layout: default
parent: Pipeline Management
title: Startup Configuration
---

## Startup Configuration

On startup, pipelines can be defined by creating an application YAML file in the LDIO directory
(in docker, this correlates to `/ldio/application.yml`) that looks as follows:

````yaml
orchestrator:
  pipelines:
    - name: my-first-pipeline
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
        - name: name of LDI Output
          config:
            foo: bar
````

### Since the introduction of dynamic pipelines

Since version 2.1.0, it is possible to manage pipelines on the fly. If pipelines must be instantiated on startup, those
pipelines can be added to a configured directory.

First of all, to configure the directory, the `/ldio/application.yml` should look like this:

````yaml
orchestrator:
  directory: <PIPELINE_DIRECTORY>
````

The folder contains a yaml file for each pipeline. It is the preferred way to call the file the same way as the name of
the pipeline, in this case, that would be `my-first-pipeline.yml`:

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
  - name: name of LDI Output
    config:
      foo: bar
```
