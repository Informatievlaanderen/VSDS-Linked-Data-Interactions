---
layout: default
parent: LDIO Adapters
title: Json To JsonLd Transformer
---

# LDIO Json To JsonLd Adapter

***Ldio:JsonToLdAdapter***

The json-to-ld-adapter receives json messages and adds a linked data context to transform the messages to json-ld.

## Config

| Property                    | Description                                                                                                                                               | Required | Default | Example                   | Supported values                                 |
|:----------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------|:---------|:--------|:--------------------------|:-------------------------------------------------|
| _context_                   | URI of json-ld context Or an JSON-LD context object.                                                                                                      | Yes      | N/A     | http://example.com/my-api | URI or Json Object (containing "@context" entry) |
| _force-content-type_        | Flag that indicates if `application/json` should be forced as mime type.                                                                                  | No       | false   | true                      | true or false                                    |
| _max-jsonld-cache-capacity_ | After retrieving an external JSON-LD context, it is cached for reuse. This property allows to specify the size of this cache (number of stored contexts). | No       | 100     | 100                       | Integer                                          |
