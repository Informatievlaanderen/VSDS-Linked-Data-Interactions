---
layout: default
parent: LDIO Adapters
title: Json To JsonLd Transformer
---

# LDIO Json To JsonLd Transformer
***be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter***

An LDIO wrapper component for the [LDI Json To JsonLd building block](../../_core/ldi-adapters/json-to-json-ld)

## Config

| Property                  | Description                                                                                                  | Required | Default | Example                   | Supported values    |
|:--------------------------|:-------------------------------------------------------------------------------------------------------------|:---------|:--------|:--------------------------|:--------------------|
| core-context              | URI of a core json-ld context.                                                                               | Yes      | N/A     | http://example.com/my-api | HTTP and HTTPS urls |
| max-jsonld-cache-capacity | A cache is used when fetching json-ld contexts. The size of this cache can be configured with this property. | No       | 100     | 100                       | Integer             |