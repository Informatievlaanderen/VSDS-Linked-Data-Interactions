---
layout: default
parent: LDIO Adapters
title: Json To JsonLd Transformer
---

# LDIO Json To JsonLd Adapter
***Ldio:JsonToLdAdapter***

The json-to-ld-adapter receives json messages and adds a linked data context to transform the messages to json-ld.

## Config

| Property                  | Description                                                                                                                                               | Required | Default | Example                   | Supported values                                 |
|:--------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------|:---------|:--------|:--------------------------|:-------------------------------------------------|
| context                   | URI of json-ld context Or an JSON-LD context object.                                                                                                      | Yes      | N/A     | http://example.com/my-api | URI or Json Object (containing "@context" entry) |
| max-jsonld-cache-capacity | After retrieving an external JSON-LD context, it is cached for reuse. This property allows to specify the size of this cache (number of stored contexts). | No       | 100     | 100                       | Integer                                          |
