---
layout: default
title: Pipeline Management
has_children: true
has_toc: true
nav_order: 1
---

# Management of Pipelines

Pipelines in LDIO can be created in YAML or JSON configuration (although all example configurations are made in YAML,
these can also be formatted in JSON).

A default pipeline looks as follows:

```yaml
  name: my-first-pipeline
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
```

- Note that one orchestrator can have multiple pipelines
- Note that one pipeline can have multiple LDI Transformers and LDI Outputs

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
To pause a pipeline, simply call the following endpoint:
````
  {base-url}/admin/api/v1/pipeline/{pipeline-name}/halt
````
And to resume a paused pipeline:
````
  {base-url}/admin/api/v1/pipeline/{pipeline-name}/resume
````

The exact behaviour of a paused pipeline depends on its input component and can be found in the [documentation of these components](docs/_ldio/ldio-inputs/index.md).
However, it will always complete its current run through the pipeline and then seize sending any output.