---
layout: default
parent: LDIO Inputs
title: HTTP In
nav_order: 3
---

# LDIO HTTP In

<b>LDIO Component Name:</b> <i>`Ldio:LdioHttpIn`</i>

<br>

The LDIO HTTP In is a basic HTTP Listener. This component listens for HTTP messages at the endpoint `http://{hostname}:{port}/{pipeline name}`.

It supports processing input in various content types, including XML (text/xml, application/xml), JSON (application/json), and RDF (text/turtle, application/ld+json, application/n-quads, application/n-triples, application/rdf+xml).

The expected output of this component is also in similar formats, supporting XML, JSON, and RDF content types.

```mermaid
graph LR
    L[endpoint HTTP messages] --> H[Http in Listener]
    H --> S[...]

    subgraph LDIO
    H
    end
```

## Example

```yml
orchestrator:
  pipelines:
    - name: example
      input:
        name: Ldio:LdioHttpIn
        config:
```

This component has no required config
