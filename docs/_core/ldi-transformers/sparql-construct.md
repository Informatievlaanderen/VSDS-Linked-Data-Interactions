---
layout: default
parent: LDI Transformers
title: SPARQL Construct Transformer
---

# SPARQL Construct Transformer

The SPARQL Construct Transformer will modify the model based on the given [SPARQL] Construct Query.

SPARQL Construct is a query language used in semantic Web technologies to create RDF (Resource Description Framework) graphs from existing RDF data. It allows users to specify a pattern of data they wish to extract from the RDF data and construct a new graph based on that pattern.

The SPARQL Construct query language provides a powerful way to create new RDF data by using existing data as the input. It can be used to transform RDF data into different formats, as well as to simplify the structure of RDF data by aggregating or filtering data.

This SPARQL Construct Transfomer building block can be used to execute model transformations. 

[SPARQL]: https://www.w3.org/TR/rdf-sparql-query/

## Splitting models using SPARQL Construct

This component can be used to split models into multiple models using graphs.
For example, the below query will create a dataset containing multiple models defined by 'GRAPH'.
The SPARQL construct component will extract all named models from the dataset and add all statements from the default model. 
The component will then return a collection of models.

```sparql
CONSTRUCT {
    GRAPH ?s {
        ?s ?p ?o
    }
}
WHERE { ?s ?p ?o }
```