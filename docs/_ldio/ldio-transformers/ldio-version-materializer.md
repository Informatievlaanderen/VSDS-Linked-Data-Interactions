---
layout: default
parent: LDIO Transformers
title: Version Materializer
---

# LDIO Version Materializer

***Ldio:VersionMaterialiser***

The Version Materializer will transform a Version Object to a State Object.

## Config

| Property              | Description                                                                                                        | Required | Default | Example                                | Supported values |
|:----------------------|:-------------------------------------------------------------------------------------------------------------------|:---------|:--------|:---------------------------------------|:-----------------|
| _versionOf-property_  | Property that points to the versionOfPath.                                                                         | Yes      | N/A     | "http://purl.org/dc/terms/isVersionOf" | String           |
| _restrict-to-members_ | Only returns the statements of the node containing the versionOf property, including potential nested blank nodes. | No       | false   | false                                  | true or false    |

## Example

### Version Object

A version object is an entity that represents the state of an object at a specific point in time and associates it with
a unique identifier (member ID). This identifier serves as the subject within the context of the RDF data model.

An example would be:

```turtle
@prefix time: <http://www.w3.org/2006/time#> .
@prefix ex:   <http://example.org/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix dc: <http://purl.org/dc/terms/> .
@prefix prov: <http://www.w3.org/ns/prov#> .

<http://example.org/member#2024-01-01T13:00:00+01:00>
  a ex:Something ;
  ex:created [
    a time:Instant ;
    time:inXSDDateTimeStamp "2024-01-01T13:00:00+01:00"^^xsd:DateTime
  ] ;
  prov:generatedAtTime "2024-01-01T13:00:00+01:00"^^xsd:DateTime ;
  dc:isVersionOf <http://example.org/Something> ;
```

### State Object

For each version object, the transformer will generate a corresponding state object and remove all statements that include the versionOf predicate.

An example of the created state object by the previous version object would be:

```turtle
@prefix time: <http://www.w3.org/2006/time#> .
@prefix ex:   <http://example.org/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix prov: <http://www.w3.org/ns/prov#> .

<http://example.org/member>
  a ex:Something ;
  ex:created [
    a time:Instant ;
    time:inXSDDateTimeStamp "2023-08-18T13:08:00+01:00"^^xsd:DateTime
  ] .
  prov:generatedAtTime "2024-01-01T13:00:00+01:00"^^xsd:DateTime ;
```

### Configuration
The YAML configuration of this example would be as follows:

```yaml
transformers:
  - name: Ldio:VersionMaterialiser
    config:
      versionOf-property: http://purl.org/dc/terms/isVersionOf
```