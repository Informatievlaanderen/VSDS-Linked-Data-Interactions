---
layout: default
parent: LDIO Adapters
title: RDF Adapter
---

# LDIO RDF Adapter
***be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter***

An LDIO wrapper component for the [LDI RDF Adapter building block](../../core/ldi-adapters/rdf-adapter)

## Config


| Property                  | Description                                                                                                  | Required | Default | Example | Supported values |
|:--------------------------|:-------------------------------------------------------------------------------------------------------------|:---------|:--------|:--------|:-----------------|
| max-jsonld-cache-capacity | A cache is used when fetching json-ld contexts. The size of this cache can be configured with this property. | No       | 100     | 100     | Integer          |