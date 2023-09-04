---
layout: default
parent: LDI Adapters
title: Model split Adapter
---

# Model split Adapter

The model split adapter splits a single incoming message into multiple message. 
This adapter is a 'supportive' adapter and requires another adapter to actually convert the initial message to a model,
before this can be split in multiple models.

This adapter can work together with any other adapter such as the [rml-adapter](rml-adapter.md) or [rdf-adapter](rdf-adapter.md).


As the most basic Adapter of the LDI Core Building Blocks, the RDF Adapter will take in an RDF string and convert it
into an internal Linked Data model.

## Notes

This Adapter requires another adapter to function.