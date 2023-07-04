---
layout: default
parent: LDIO Transformers
title: Version Materializer
---

# LDIO Version Materializer
***be.vlaanderen.informatievlaanderen.ldes.ldi.VersionMaterialiser***

An LDIO wrapper component for the [LDI Version Materializer building block](../../core/ldi-transformers/version-materializer)

## Config

| Property            | Description                                                                                                                                                             | Required | Default | Example                                | Supported values |
|:--------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:---------|:--------|:---------------------------------------|:-----------------|
| versionOf-property  | Property that points to the versionOfPath.                                                                                                                              | Yes      | N/A     | "http://purl.org/dc/terms/isVersionOf" | String           |
| restrict-to-members | Builds a model limited to statements about the ldes:member, including potential nested blank nodes.  Excludes statements about referenced entities, provided as context | No       | false   | false                                  | true or false    |