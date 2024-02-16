---
layout: default
parent: Apache Nifi processors
title: Version Materializer
---

# Apache Nifi Version Materializer

<b>Apache Nifi processor name:</b> <i>``VersionMaterializerProcessor```</i>

<br>

![Alt text](image.png)

The Version Materializer will transform a Version Object into a State Object.



```mermaid
graph LR
    L[version objects] --> H[Version Materializer]
    H --> S[state objects]

    subgraph Apache Nifi pipeline
    L
    H
    S
    end
```


## Config

| Property            | Description                                                                                                                                                            | Required | Default | Example                                | Supported values |
| :------------------ | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------- | :------ | :------------------------------------- | :--------------- |
| versionOf-property  | Property that points to the versionOfPath.                                                                                                                             | Yes      | N/A     | "http://purl.org/dc/terms/isVersionOf" | String           |
| restrict-to-members | Builds a model limited to statements about the ldes:member, including potential nested blank nodes. Excludes statements about referenced entities, provided as context | No       | false   | false                                  | true or false    |
