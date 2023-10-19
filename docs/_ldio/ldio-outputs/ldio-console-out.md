---
layout: default
parent: LDIO Outputs
title: Console Out
---

# LDIO Console Out
***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut***

The LDIO Console Out will output its given model to the console.

## Config

| Property     | Description                                                           | Required | Default             | Example                                   | Supported values                                              |
|:-------------|:----------------------------------------------------------------------|:---------|:--------------------|:------------------------------------------|:--------------------------------------------------------------|
| content-type | Target content type.                                                  | No       | application/n-quads | application/ld+json                       | Any type supported by [Apache Jena](https://jena.apache.org/) |
| frame-type   | RDF type of the objects that need to be included for JSON-LD framing. | No       | N/A                 | http://purl.org/goodrelations/v1#Offering | Any RDF type                                                  |