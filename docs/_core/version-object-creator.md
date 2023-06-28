---
layout: default
parent: LDI Transformers
title: Version Object Creator
---

# Version Object Creator

The Version Object Creator will transform a State Object to a Version Object.

## Inputs

| Property                | Description                                                                                             | Null allowed | Supported values |
|:------------------------|:--------------------------------------------------------------------------------------------------------|:-------------|:-----------------|
| dateObservedProperty    | Property that points to a literal which should be used as timestampPath. Defaults to current timestamp. | Yes          | Property         |
| memberTypeResource      | Defines the RDF type of the version object                                                              | No           | Resource         |
| delimiter               | Defines how the version object id will be constructed. (versionOf + delimiter + dateObserved)           | No           | String           |
| generatedAtTimeProperty | If defined, a statement will be added to the model with the observedAt value and the given property.    | Yes          | Property         |
| versionOfProperty       | If defined, a statement will be added to the model with the versionOf value and the given property.     | Yes          | Property         |