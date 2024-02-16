---
layout: default
parent: LDIO Adapters
title: RML Adapter
---

# LDIO RML Adapter

<b>LDIO Component Name:</b> <i>`Ldio:RmlAdapter`</i>

<br>

The RML Adapter allows a user to transform a non-LD object (json/CSV/XML) to an RDF object.

This is done by providing a [RML] mapping file. However, as RML is written in RDF, creating a new mapping can be challenging for a new user.

That's where [YARRRML] comes into play. Along with the online editor [Matey], it's easy to build one's mapping and export it into RML.

[RML]: https://rml.io/specs/rml/
[YARRRML]: https://rml.io/yarrrml/spec/

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

| Property | Description                                    | Required | Default | Example     | Supported values |
| :------- | :--------------------------------------------- | :------- | :------ | :---------- | :--------------- |
| mapping  | Path to content of RML/content of RML mapping. | Yes      | N/A     | mapping.ttl | Path/String      |
