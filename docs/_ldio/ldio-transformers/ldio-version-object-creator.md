---
layout: default
parent: LDIO Transformers
title: Version Object Creator
---

# LDIO Version Object Creator
***be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator***

An LDIO wrapper component for the [LDI Version Object Creator building block](../../core/ldi-transformers/version-object-creator)

## Config

| Property               | Description                                                                                             | Required | Default           | Example                                                           | Supported values |
|:-----------------------|:--------------------------------------------------------------------------------------------------------|:---------|:------------------|:------------------------------------------------------------------|:-----------------|
| date-observed-property | Property that points to a literal which should be used as timestampPath. Defaults to current timestamp. | No       | Current Timestamp | https://uri.etsi.org/ngsi-ld/default-context/WaterQualityObserved | String           |
| member-type            | Defines the RDF type of the version object                                                              | No       | N/A               | https://uri.etsi.org/ngsi-ld/default-context/Device               | String           |
| delimiter              | Defines how the version object id will be constructed. (versionOf + delimiter + dateObserved)           | No       | /                 | /                                                                 | String           |
| generatedAt-property   | If defined, a statement will be added to the model with the observedAt value and the given property.    | No       | N/A               | http://www.w3.org/ns/prov#generatedAtTime                         | String           |
| versionOf-property     | If defined, a statement will be added to the model with the versionOf value and the given property.     | No       | N/A               | http://purl.org/dc/terms/isVersionOf                              | String           |
