---
title: The Linked Data Interactions Orchestrator
layout: home
nav_order: 0
---

# The Linked Data Interactions Orchestrator

A lightweight application maintained by the LDI team. Its creation came when a more lightweight alternative for [Apache NiFi] was needed.

## Setup Basic Configuration

To set up a basic LDIO configuration, all that is needed is passing a YAML configuration.

This can look as follows:

````yaml
orchestrator:
  pipelines:
    - name: my-first-pipeline
      input:
        name: fully-qualified name of LDI Input
        config:
          foo: bar
        adapter:
          name: fully-qualified name of LDI Adapter
          config:
            foo: bar
      transformers:
        - name: fully-qualified name of LDI Transformer
          config:
            foo: bar
      outputs:
        - name: fully-qualified name of LDI Transformer
          config:
            foo: bar
````

- Note that one orchestrator can have multiple pipelines 
- Note that one pipeline can have multiple LDI Transformers and LDI Outputs 

## LDIO DEBUG logging

To enable logging the input model for a 
* [LDIO Adapter](./ldio-adapters)
* [LDIO Transformer](./ldio-transformers)
* [LDIO Output](./ldio-outputs)

Make sure you 

* Add the following property in your application config:
    ````yaml
    logging:
        level:
            be.vlaanderen.informatievlaanderen: DEBUG
    ````
* Add the ```debug: true``` property to your transformer or output config.

## Complete Workflow

````mermaid
flowchart LR
    subgraph LDIO Input
        HttpIn(Http In)
        HttpPoller(Http Poller)
        LdesClient(LDES Client)
        ArchiveFileIn(Archive File In)
        KafkaIn(Kafka In)
    end

    HttpIn--Non Linked Data -->Adapter
    HttpPoller--Non Linked Data -->Adapter
    LdesClient--Non Linked Data -->Adapter
    ArchiveFileIn--Non Linked Data -->Adapter
    KafkaIn--Non Linked Data -->Adapter

    subgraph LDIO Adapter
        Adapter{Adapter}

        Adapter-->RdfAdapter
        Adapter-->RmlAdapter
        Adapter-->JsonToJsonLdAdapter
        Adapter-->NgsiV2Adapter

        RdfAdapter(Rdf Adapter)
        RmlAdapter(Rml Adapter)
        JsonToJsonLdAdapter(JSON to JSON LD Adapter)
        NgsiV2Adapter(NGSI v2 to LD Adapter)
    end

    RdfAdapter--Linked Data-->TransformerIn
    RmlAdapter--Linked Data-->TransformerIn
    JsonToJsonLdAdapter--Linked Data-->TransformerIn
    NgsiV2Adapter--Linked Data-->TransformerIn

    subgraph LDIO Transformers
        TransformerIn{Transformer}
        TransformerOut{Transformer}

        TransformerIn --> HttpEnricherWrapper
        TransformerIn --> GeoJsonToWkt
        TransformerIn --> ModelSplitter
        TransformerIn --> SPARQLConstruct
        TransformerIn --> VersionMaterialiser
        TransformerIn --> VersionObjectCreator

        GeoJsonToWkt(GeoJSON to WKT Transformer)
        ModelSplitter(Model Split Transformer)
        SPARQLConstruct(SPARQL Construct Transformer)
        VersionMaterialiser(Version Materialiser)
        VersionObjectCreator(Version Object Creator)

        subgraph HttpEnricherWrapper[Http Enricher]
            HttpEnricher(Http Enricher)
            HttpEnrichAdapter{Adapter}
            HttpEnrichAdapter --> HttpEnricher
        end

        HttpEnricherWrapper -- Model --> TransformerOut
        GeoJsonToWkt -- Model --> TransformerOut
        ModelSplitter -- Model[] --> TransformerOut
        SPARQLConstruct -- Model --> TransformerOut
        VersionMaterialiser -- Model --> TransformerOut
        VersionObjectCreator -- Model --> TransformerOut

        TransformerOut -- Model --> TransformerIn
    end

    TransformerOut -- Model --> Output

    subgraph LDIO Out
        Output{Output}

        Output --> HttpOut
        Output --> KafkaOut
        Output --> ConsoleOut
        Output --> ArchiveOut
        Output --> AzureBlobOut

        HttpOut
        KafkaOut
        ConsoleOut
        ArchiveOut
        AzureBlobOut
    end
````


[Apache NiFi]: https://nifi.apache.org/
