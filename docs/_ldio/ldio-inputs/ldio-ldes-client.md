---
layout: default
parent: LDIO Inputs
title: LDES Client
---

# LDIO Ldes Client

***Ldio:LdesClient***

An LDIO wrapper component for the [LDI LDES Client building block](../../core/ldi-inputs/ldes-client)

## Config

### General properties

| Property       | Description                                                                             | Required | Default     | Example                                   | Supported values                                              |
|:---------------|:----------------------------------------------------------------------------------------|:---------|:------------|:------------------------------------------|:--------------------------------------------------------------|
| urls           | List of URLs of the LDES data sources                                                   | Yes      | N/A         | http://localhost:8080/my-ldes             | HTTP and HTTPS urls                                           |
| source-format  | The 'Content-Type' that should be requested to the server.                              | No       | text/turtle | application/n-quads                       | Any type supported by [Apache Jena](https://jena.apache.org/) |
| state          | 'sqlite', 'memory', 'file' or 'postgres' to indicate how the state should be persisted. | No       | memory      | sqlite                                    | 'sqlite', 'files' or 'memory'                                 |
| keep-state     | Indicates if the state should be persisted on shutdown (n/a for in memory states)       | No       | false       | false                                     | true or false                                                 |
| timestamp-path | The property-path used to determine the timestamp on which the members will be ordered  | No       | N/A         | http://www.w3.org/ns/prov#generatedAtTime | A property path                                               |

> **_NOTE:_** The default `source-format` is `text/turtle`, as this rdf format supports relative uri's. However, if 
> relative uri's are not used, `application/n-quads` or even the binary format `application/rdf+protobuf` are better 
> options, as these formats are faster to parse.

### Postgres properties

| Property          | Description                                    | Required | Default | Example                                                        | Supported values |
|:------------------|:-----------------------------------------------|:---------|:--------|:---------------------------------------------------------------|:-----------------|
| postgres.url      | JDBC url of the Postgres database.             | No       | N/A     | jdbc:postgresql://test.postgres.database.azure.com:5432/sample | String           |
| postgres.username | Username used to connect to Postgres database. | No       | N/A     | myUsername@test                                                | String           |
| postgres.password | Password used to connect to Postgres database. | No       | N/A     | myPassword                                                     | String           |

### Version materialisation properties

| Property                            | Description                                                                            | Required | Default                              | Example                                | Supported values |
|:------------------------------------|:---------------------------------------------------------------------------------------|:---------|:-------------------------------------|:---------------------------------------|:-----------------|
| materialisation.enabled             | Indicates if the client should return state-objects (true) or version-objects (false). | No       | false                                | true                                   | true or false    |
| materialisation.version-of-property | Property that points to the versionOfPath.                                             | No       | http://purl.org/dc/terms/isVersionOf | "http://purl.org/dc/terms/isVersionOf" | true or false    |

This component uses the "LDIO Http Requester" to make the HTTP request.
Refer to [LDIO Http Requester](../ldio-core) for the config.

> **_NOTE:_**  Setting the keep-state property to true makes it so that the state can not be deleted through the
> pipeline-management api

## Examples

```yaml
  input:
    name: Ldio:LdesClient
    config:
      urls:
        - http://localhost:8080/my-ldes
      sourceFormat: text/turtle
      materialisation:
        enabled: true
      retries:
        enabled: true
      auth:
        type: OAUTH2_CLIENT_CREDENTIALS
        client-id: clientId
        client-secret: secret
        token-endpoint: http://localhost:8000/token
```

```yaml
  input:
    name: Ldio:LdesClient
    config:
      urls:
        - http://localhost:8080/my-ldes
      sourceFormat: text/turtle
      retries:
        enabled: true
      state: postgres
      postgres:
        url: jdbc:postgresql://test.postgres.database.azure.com:5432/sample
        username: myUsername@test
        password: myPassword
```

## Pausing

When paused, this component will stop processing the current fragment and not make any calls to the server.
When resumed, it will continue with the fragment where it stopped and continue as normal.