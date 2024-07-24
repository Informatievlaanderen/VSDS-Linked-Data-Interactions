---
layout: default
parent: Examples
title: Enrich a model
nav_order: 3
has_toc: true
---

# Enrich A Model

As part of this example, we will store some Car data in a Graph Database. 
We will later use that data to extend our user data Model to include the Car data.

## Used Components

- [Http In](../ldio-inputs/ldio-http-in)
- [RDF Adapter](../ldio-adapters/ldio-rdf-adapter.md)
- [Console Out](../ldio-outputs/ldio-console-out)
- [Repository Sink](../ldio-outputs/ldio-repository-sink)

## Setup

For this setup, we will have two pipelines:
- A **to-graph** pipeline that will take in our "Car" Linked Data and send it straight to a GraphDB
- A **enriched** pipeline that will extend the data with the saved car data

### RDF4J Server

To save the "Car" data, we first need to set up a GraphDB Server.
This can be done by mounting a rdf4j workbench image.

````shell
docker run -d -p 8081:8080 -e JAVA_OPTS="-Xms1g -Xmx4g" eclipse/rdf4j-workbench:latest
````

Once spun up, a simple repository can be configured via doing the following curl command:

***test-db.ttl:***
```ttl
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.
@prefix rep: <http://www.openrdf.org/config/repository#>.
@prefix sr: <http://www.openrdf.org/config/repository/sail#>.
@prefix sail: <http://www.openrdf.org/config/sail#>.
@prefix ms: <http://www.openrdf.org/config/sail/memory#>.

[] a rep:Repository ;
   rep:repositoryID "test" ;
   rdfs:label "test memory store" ;
   rep:repositoryImpl [
      rep:repositoryType "openrdf:SailRepository" ;
      sr:sailImpl [
	 sail:sailType "openrdf:MemoryStore" ;
	 ms:persist true ;
	 ms:syncDelay 120
      ]
   ].
```

````shell
curl -X PUT -H "Content-Type: text/turtle" --data-binary @test-db.ttl http://localhost:8081/rdf4j-server/repositories/test
````

### LDIO

***ldio.config.yaml:***
```yaml
orchestrator:
  pipelines:
    - name: "to-graph"
      input:
        name: "Ldio:HttpIn"
        adapter:
          name: "Ldio:RdfAdapter"
      outputs:
        - name: "Ldio:RepositoryMaterialiser"
          config:
            sparql-host: http://localhost:8081/rdf4j-server
            repository-id: test
    - name: "enriched"
      input:
        name: "Ldio:HttpIn"
        adapter:
          name: "Ldio:RdfAdapter"
      transformers:
        - name: "Ldio:SparqlConstructTransformer"
          config:
            query: "
              PREFIX schema: <http://schema.org/>
            
              CONSTRUCT {
                ?s ?p ?o .
                ?car ?cp ?co .
              }
              WHERE { 
                ?s ?p ?o .
                ?s schema:hasCar ?car
                SERVICE <http://localhost:8081/rdf4j-server/repositories/test> { 
                  ?car ?cp ?co .
                }
              }
            "
      outputs:
        - name: "Ldio:ConsoleOut"
```

## Execution

### 1. Ingestion of Car Data

First, we will post these three turtle files to our "to-graph" pipeline at endpoint http://localhost:8080/to-graph 
with the Content-Type header set to 'text/turtle'

```ttl
@prefix schema: <http://schema.org/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

<http://example.com/cars/Volvo/XC40>
  a schema:Car ;
  schema:brand "Volvo"^^xsd:string ;
  schema:max-speed "180"^^xsd:integer ;
  schema:model "XC40"^^xsd:string .
```

```ttl
@prefix schema: <http://schema.org/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

<http://example.com/cars/Ferrari/F40>
  a schema:Car ;
  schema:brand "Ferrari"^^xsd:string ;
  schema:max-speed "315"^^xsd:integer ;
  schema:model "F40"^^xsd:string .
```

```ttl
@prefix schema: <http://schema.org/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

<http://example.com/cars/Reliant/Robin>
  a schema:Car ;
  schema:brand "Reliant"^^xsd:string ;
  schema:max-speed "136"^^xsd:integer ;
  schema:model "Robin"^^xsd:string .
```

### 2. Send un-enriched member to pipeline

Secondly, we will post our un-enriched User model to our "enriched" pipeline at endpoint http://localhost:8080/enriched
with the Content-Type header set to 'text/turtle'.

This pipeline will not only include the posted statements, but will include the models from the GraphDB based on their URI.

```ttl
@prefix schema: <http://schema.org/> .

<http://example.com/people/SpideyBoy>
  schema:hasCar <http://example.com/cars/Ferrari/F40>, <http://example.com/cars/Volvo/XC40> ;
  schema:jobTitle "Spidey Boy" ;
  schema:name "Peter Parker" ;
  a schema:Person .
```

### 3. Result: an Enriched Model

After posting the User model, you should be seeing data in your console similar to
```ttl
@prefix Ferrari: <http://example.com/cars/Ferrari/> .
@prefix Volvo:   <http://example.com/cars/Volvo/> .
@prefix rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix schema:  <http://schema.org/> .

Volvo:XC40  rdf:type      schema:Car ;
        schema:brand      "Volvo" ;
        schema:max-speed  180 ;
        schema:model      "XC40" .

<http://example.com/people/SpideyBoy>
        rdf:type         schema:Person ;
        schema:hasCar    Ferrari:F40 , Volvo:XC40 ;
        schema:jobTitle  "Spidey Boy" ;
        schema:name      "Peter Parker" .

Ferrari:F40  rdf:type     schema:Car ;
        schema:brand      "Ferrari" ;
        schema:max-speed  315 ;
        schema:model      "F40" .
```