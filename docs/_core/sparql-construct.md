---
layout: default
parent: LDI Transformers
title: SPARQL Construct Transformer
---

# SPARQL Construct Transformer

The SPARQL Construct Transformer will modify the model based on the given [SPARQL] Construct Query.

## Inputs

* Query query: The [SPARQL] Construct Query. Parsed as an Apache Jena Query object.
* boolean includeOriginal: Allows the transformer to also include the original statements of the model on top of the query. 

[SPARQL]: https://www.w3.org/TR/rdf-sparql-query/