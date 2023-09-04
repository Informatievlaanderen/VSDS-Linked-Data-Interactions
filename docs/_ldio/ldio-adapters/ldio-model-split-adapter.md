---
layout: default
parent: LDIO Adapters
title: Model Split Adapter
---

# LDIO RML Adapter
***be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitAdapter***

An LDIO wrapper component for the [LDI Model Split Adapter building block](../../_core/ldi-adapters/model-split-adapter.md)

## Config


| Property            | Description                                                                    | Required | Default | Example                                                 | Supported values |
|:--------------------|:-------------------------------------------------------------------------------|:---------|:--------|:--------------------------------------------------------|:-----------------|
| split-subject-type  | Path defining the subjects that need to be selected. To create new models for. | Yes      | N/A     | http://schema.org/Movie                                 | String           |
| split-base-adapter  | The adapter that has to convert the content into a model before splitting it.  | Yes      | N/A     | be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter  | String           |

When the base adapter requires properties of themselves, they can be added under the same config as the model split adapter.
For example:

```yaml
        adapter:
          name: be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitAdapter
          config:
            split-subject-type: http://schema.org/Movie
            split-base-adapter: be.vlaanderen.informatievlaanderen.ldes.ldi.RmlAdapter
            mapping: "mapping.ttl"
```
