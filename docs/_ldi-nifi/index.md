---
title: Linked Data Interactions For Apache NiFi
layout: home
nav_order: 0
---

# Linked Data Interactions For Apache NiFi

[Apache Nifi] is an easy to use, powerful, and reliable system to process and distribute data.

## Usage

All the following processors can be found in the processor list when using the ldes/ldi-workbench-nifi docker image.

These processors can be [added][Adding a processor in NiFi] by filtering on the ***be.vlaanderen.informatievlaanderen.ldes.ldi.nifi*** group or by filtering on the ***vsds*** tag

- [Create Version Processor](../core/ldi-transformers/version-object-creator)
- [GeoJson to WKT Processor](../core/ldi-transformers/geojson-to-wkt)
- [Json to Json LD Processor](../core/ldi-adapters/json-to-json-ld)
- [Ngsi V2 to LD Processor](../core/ldi-adapters/ngsiv2-to-ld) 
- [RDF4j Repository Materialization Processor](../core/ldi-outputs/repository-materialiser)
- [SPARQL Interactions Processor](./processors/sparql-interactions)
- [Version Materialization Processor](../core/ldi-transformers/version-materializer)
- [Archive File Out Processor](../core/ldi-outputs/file-archiving)
- [Archive File In Processor](../core/ldi-outputs/file-archiving)

{: .note }
All documentation and notes about configuration are available in the NiFi component itself.

[Apache NiFi]: https://nifi.apache.org/
[Adding a processor in NiFi]: https://nifi.apache.org/docs/nifi-docs/html/getting-started.html#adding-a-processor