---
layout: default
parent: LDIO Outputs
title: HTTP Sparql Out
---

# HTTP Sparql Out

***Ldio:HttpSparqlOut***

The HTTP SPARQL Out component can be used to write data to a SPARQL host, with Virtuoso as the most common known one.

## Config

| Property                     | Description                                                                                                                                 | Required | Default | Example                                  | Supported values |
|:-----------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------|:---------|:--------|:-----------------------------------------|:-----------------|
| _endpoint_                   | The url of the sparql host                                                                                                                  | Yes      | N/A     | http://localhost:8890/sparql             | URL              |
| _graph_                      | The graph whereto must be written                                                                                                           | No       | N/A     | http://example.graph.com                 | String           |
| _skolemisation.skolemDomain_ | If the skolem domain is set, skolemisation will be triggered before the triples are written to the sparql host                              | No       | N/A     | http://example.org                       | Any valid IRI    |
| _replacement.enabled_        | Whether the old nodes must be replaced by the new ones                                                                                      | No       | true    | false                                    | Boolean value    |
| _replacement.depth_          | How deep the default delete query must delete nested nodes from the existing subject, will be ignored if `replacement.deleteFunction`is set | No       | 10      | 15                                       | Integer          |
| _replacement.deleteFunction_ | If this property is set, then the default delete function will be overridden with this delete function                                      | No       | N/A     | `DELETE { ?s ?p ?o}  WHERE { ?s ?p ?o }` | String           |

{% include ldio-core/http-requester.md %}

### Replacement

Replacement includes that all old nodes from certain subjects must be deleted before the new nodes with the same subject
can be inserted. \
By default, a delete query is constructed by the service that delete all nodes, including nested nodes to a level,
specified by the `replacement.depth` property, deep. If for some reason, the constructed delete query is not sufficient,
or the query is too complex, a custom delete query can be configured. This query will override the default query created 
by the service, which also mean the `replacement.depth` property will be ignored.

### Skolemisation

Not all sparql hosts can deal that well with blank nodes, therefore, those nodes can first be skolemised. However, to
skolemise nodes, a skolem domain is required, which can be set by the `skolemisation.skolemDomain` property, which
directly enables the service. More information about skolemisation can be found on
the [skolemisation-transformer page](./../ldio-transformers/ldio-skolemisation-transformer)