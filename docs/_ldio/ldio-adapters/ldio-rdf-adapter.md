---
layout: default
parent: LDIO Adapters
title: RDF Adapter
---

# LDIO RDF Adapter

***Ldio:RdfAdapter***

As the most basic adapter, the RDF Adapter will take in an RDF string and convert it
into an internal Linked Data model based on the given content type.

## Notes

This Adapter only supports valid RDF mime types

## Config


| Property                  | Description                                                                                                           | Required | Default | Example | Supported values |
|:--------------------------|:----------------------------------------------------------------------------------------------------------------------|:---------|:--------|:--------|:-----------------|
| max-jsonld-cache-capacity | A cache is used when fetching json-ld contexts. The number of cached contexts can be configured with this property.   | No       | 100     | 100     | Integer          |