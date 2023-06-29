---
layout: default
parent: LDIO Transformers
title: SPARQL Construct
---

# LDIO SPARQL Construct
***be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer***

An LDIO wrapper component for the [LDI SPARQL Construct building block]

## Config


| Property | Description                                              | Required | Default | Example  | Supported values |
|:---------|:---------------------------------------------------------|:---------|:--------|:---------|:-----------------|
| query    | Path to content of SPARQL Query/content of SPARQL query. | Yes      | N/A     | query.rq | Path/String      |
| infer    | Include original model in end result.                    | No       | false   | false    | true or false    |

[LDI SPARQL Construct building block]: /core/sparql-construct