---
layout: default
parent: LDIO Outputs
title: HTTP Out
---

# LDIO HTTP Out

***Ldio:HttpOut***

The LDIO HTTP Out is a basic Http Client that will send the given Linked Data model to a target url.

## Config

| Property   | Description           | Required | Default      | Example                     | Supported values                                      |
|:-----------|:----------------------|:---------|:-------------|:----------------------------|:------------------------------------------------------|
| endpoint   | Target url.           | Yes      | N/A          | http://example.com/endpoint | HTTP and HTTPS urls                                   |
| rdf-writer | LDI RDF Writer Config | No       | Empty Config | N/A                         | [LDI RDF Writer Config](../ldio-core/ldio-rdf-writer) |