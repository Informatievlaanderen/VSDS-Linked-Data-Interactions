---
layout: default
parent: LDIO Inputs
title: LDES Client
---

# LDIO Ldes Client

***be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient***

An LDIO wrapper component for the [LDI LDES Client building block](../../core/ldi-inputs/ldes-client)

## Config

| Property          | Description                                                                             | Required | Default             | Example                                                        | Supported values                                              |
|:------------------|:----------------------------------------------------------------------------------------|:---------|:--------------------|:---------------------------------------------------------------|:--------------------------------------------------------------|
| url               | The url of the LDES server                                                              | Yes      | N/A                 | http://localhost:8080/my-ldes                                  | HTTP and HTTPS urls                                           |
| source-format     | The 'Content-Type' that should be requested to the server.                              | No       | application/ld+json | application/n-quads                                            | Any type supported by [Apache Jena](https://jena.apache.org/) |
| state             | 'sqlite', 'memory', 'file' or 'postgres' to indicate how the state should be persisted. | No       | memory              | sqlite                                                         | 'sqlite', 'files' or 'memory'                                 |
| keep-state        | Indicates if the state should be persisted on shutdown (n/a for in memory states)       | No       | false               | false                                                          | true or false                                                 |
| timestamp-path    | The property-path used to determine the timestamp on which the members will be ordered  | No       | N/A                 | http://www.w3.org/ns/prov#generatedAtTime                      | A property path                                               |
| postgres.url      | JDBC url of the Postgres database.                                                      | No       | N/A                 | jdbc:postgresql://test.postgres.database.azure.com:5432/sample | String                                                        |
| postgres.username | Username used to connect to Postgres database.                                          | No       | N/A                 | myUsername@test                                                | String                                                        |
| postgres.password | Password used to connect to Postgres database.                                          | No       | N/A                 | myPassword                                                     | String                                                        |

This component uses the "LDIO Http Requester" to make the HTTP request.
Refer to [LDIO Http Requester](../ldio-core) for the config.

## Examples

```yaml
  input:
    name: be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient
    config:
      url: http://localhost:8080/my-ldes
      sourceFormat: text/turtle
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
    name: be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient
    config:
      url: http://localhost:8080/my-ldes
      sourceFormat: text/turtle
      retries:
        enabled: true
      state: postgres
      postgres:
        url: jdbc:postgresql://test.postgres.database.azure.com:5432/sample
        username: myUsername@test
        password: myPassword
```