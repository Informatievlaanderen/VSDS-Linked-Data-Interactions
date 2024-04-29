---
layout: default
parent: LDIO Outputs
title: HTTP Out
---

# LDIO HTTP Out

***Ldio:HttpOut***

The LDIO HTTP Out is a basic Http Client that will send the given Linked Data model to a target url.

## Config

| Property   | Description | Required | Default | Example                     | Supported values    |
|:-----------|:------------|:---------|:--------|:----------------------------|:--------------------|
| _endpoint_ | Target url. | Yes      | N/A     | http://example.com/endpoint | HTTP and HTTPS urls |

{% include ldio-core/rdf-writer.md %}

{% include ldio-core/http-requester.md %}