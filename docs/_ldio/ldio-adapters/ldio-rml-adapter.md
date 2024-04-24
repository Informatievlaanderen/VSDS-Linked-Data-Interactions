---
layout: default
parent: LDIO Adapters
title: RML Adapter
---

# LDIO RML Adapter

***Ldio:RmlAdapter***

The RML Adapter allows a user to transform a non-LD object (json/CSV/XML) to an RDF object.

This is done by providing a RML mapping file. For more details on how to form a correct RML mapping, visit
the [RML documentation].

## Config


| Property  | Description                                    | Required | Default | Example     | Supported values |
|:----------|:-----------------------------------------------|:---------|:--------|:------------|:-----------------|
| _mapping_ | Path to content of RML/content of RML mapping. | Yes      | N/A     | mapping.ttl | Path/String      |

[RML documentation]: https://rml.io/specs/rml/