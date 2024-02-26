---
layout: default
parent: LDIO Adapters
title: RDF Adapter
---

# LDIO RDF Adapter

<b>LDIO Component Name:</b> <i>`Ldio:RdfAdapter`</i>

<br>

An LDIO wrapper component for the [LDI RDF Adapter building block](../../core/ldi-adapters/rdf-adapter)

```mermaid
graph LR
    L[...] --> H[RDF writer]
    H --> S[correct RDF]

    subgraph LDIO
    H
    end
```

## Example

```yml
orchestrator:
  pipelines:
    - name: example
      adapter:
        name: Ldio:RdfAdapter
```

## Config

This component has no required config
