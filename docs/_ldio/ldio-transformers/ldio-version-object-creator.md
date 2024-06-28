---
layout: default
parent: LDIO Transformers
title: Version Object Creator
---

# LDIO Version Object Creator

***Ldio:VersionObjectCreator***

The Version Object Creator will transform a State Object to a Version Object.

## Config

| Property                 | Description                                                                                                                    | Required | Default                                   | Example                                   | Supported values |
|:-------------------------|:-------------------------------------------------------------------------------------------------------------------------------|:---------|:------------------------------------------|:------------------------------------------|:-----------------|
| _date-observed-property_ | Property path (IRI format '<>') that points to a literal which should be used as timestampPath. Defaults to current timestamp. | No       | Current Timestamp                         | \<https://example.org/ObservedAt\>        | String           |
| _member-type_            | Defines the RDF type of the object to be transformed to a version object.                                                      | Yes      | N/A                                       | https://example.org/Person                | String           |
| _delimiter_              | Defines how the version object id will be constructed. (versionOf + delimiter + dateObserved)                                  | No       | /                                         | /                                         | String           |
| _generatedAt-property_   | A statement will be added to the model with the generatedAt value and the given property.                                      | No       | http://www.w3.org/ns/prov#generatedAtTime | http://www.w3.org/ns/prov#generatedAtTime | String           |
| _versionOf-property_     | A statement will be added to the model with the versionOf value and the given property.                                        | No       | http://purl.org/dc/terms/isVersionOf      | http://purl.org/dc/terms/isVersionOf      | String           |

## Example

### State Object

A state object is an entity that represents the most recent state of an object, and in this context, the subject is not
unique. Below is an example presented in RDF format: Below is an example presented in RDF format:

```turtle
@prefix time: <http://www.w3.org/2006/time#> .
@prefix ex:   <http://example.org/> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

<http://example.org/member>
  a ex:Something ;
  ex:created [
    a time:Instant ;
    time:inXSDDateTimeStamp "2023-08-18T13:08:00+01:00"^^xsd:DateTime
  ] .
```

A property path can be provided for the date-observed-property. You can provide the following
date-observed-property: `<http://example.org/created>/<http://www.w3.org/2006/time#inXSDDateTimeStamp>`
to select `time:inXSDDateTimeStamp` within `ex:created`.

### Version Object

A version object is an entity that represents the state of an object at a specific point in time and associates it with
a unique identifier (member ID). This identifier serves as the subject within the context of the RDF data model.

The transformer will perform three key tasks:

- Modify the named subject with a unique identifier based on the provided date-observed-property and delimiter
- Add a timestamp statement
- Add a versionOf statement

An example of the created version object by the previous state object would be:

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

### Configuration

The YAML configuration of this example would be as follows:

```yaml
transformers:
  - name: Ldio:VersionObjectCreator
    config:
      member-type: 
        - http://example.org/Something
        - http://example.org/SomethingElse
      delimiter: "#"
      date-observed-property: <http://example.org/created>/<http://www.w3.org/2006/time#inXSDDateTimeStamp>
      generatedAt-property: http://www.w3.org/ns/prov#generatedAtTime
      versionOf-property: http://purl.org/dc/terms/isVersionOf
```