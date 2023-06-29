---
layout: default
parent: LDIO Adapters
title: NGSIv2 To LD Adapter
---

# LDIO NGSIv2 To LD Adapter
***be.vlaanderen.informatievlaanderen.ldes.ldi.NgsiV2ToLdAdapter***

An LDIO wrapper component for the [LDI NGSIv2 To LD building block](../../core/ldi-adapters/ngsiv2-to-ld)

## Config

| Property        | Description                                      | Required | Default | Example                   | Supported values    |
|:----------------|:-------------------------------------------------|:---------|:--------|:--------------------------|:--------------------|
| core-context    | URI of a core json-ld context.                   | Yes      | N/A     | http://example.com/my-api | HTTP and HTTPS urls |
| ld-context      | URI of a custom json-ld context.                 | No       | N/A     | http://example.com/my-api | HTTP and HTTPS urls |
| data-identifier | Identifier that points to data in provided json. | Yes      | N/A     | data                      | String              |