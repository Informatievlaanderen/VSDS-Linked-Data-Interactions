---
layout: default
parent: LDIO Adapters
title: Json To JsonLd Transformer
---

# LDIO Json To JsonLd Adapter
***Ldio:JsonToLdAdapter***

The json-to-ld-adapter receives json messages and adds a linked data context to transform the messages to json-ld.

## Config

| Property                  | Description                                                                                                  | Required | Default | Example                   | Supported values                                 |
|:--------------------------|:-------------------------------------------------------------------------------------------------------------|:---------|:--------|:--------------------------|:-------------------------------------------------|
| context                   | URI of json-ld context Or an JSON-LD context object.                                                         | Yes      | N/A     | http://example.com/my-api | URI or Json Object (containing "@context" entry) |
| max-jsonld-cache-capacity | A cache is used when fetching json-ld contexts. The size of this cache can be configured with this property. | No       | 100     | 100                       | Integer                                          |
