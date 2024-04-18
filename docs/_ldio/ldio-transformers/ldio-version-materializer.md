---
layout: default
parent: LDIO Transformers
title: Version Materializer
---

# LDIO Version Materializer

***Ldio:VersionMaterialiser***

The Version Materializer will transform a Version Object to a State Object.

## Config

| Property            | Description                                                                                                        | Required | Default | Example                                | Supported values |
|:--------------------|:-------------------------------------------------------------------------------------------------------------------|:---------|:--------|:---------------------------------------|:-----------------|
| versionOf-property  | Property that points to the versionOfPath.                                                                         | Yes      | N/A     | "http://purl.org/dc/terms/isVersionOf" | String           |
| restrict-to-members | Only returns the statements of the node containing the versionOf property, including potential nested blank nodes. | No       | false   | false                                  | true or false    |