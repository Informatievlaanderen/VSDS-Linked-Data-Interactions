---
layout: default
parent: LDIO Outputs
title: HTTP Out
---

# LDIO HTTP Out
***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut***

The LDIO HTTP Out is a basic Http Client that will send the given Linked Data model to a target url.

## Config

| Property     | Description          | Required | Default             | Example                     | Supported values                                              |
|:-------------|:---------------------|:---------|:--------------------|:----------------------------|:--------------------------------------------------------------|
| content-type | Target content type. | No       | application/ld+json | application/n-quads         | Any type supported by [Apache Jena](https://jena.apache.org/) |
| endpoint     | Target url.          | Yes      | N/A                 | http://example.com/endpoint | HTTP and HTTPS urls                                           |