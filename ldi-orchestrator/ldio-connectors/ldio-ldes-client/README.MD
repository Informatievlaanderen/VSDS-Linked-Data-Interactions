# LDIO LDES CLIENT
This is an LDI Input component that reads an LDES from a server.

The following configuration is supported:

| Property            | Description                                                                       | Required | Default             | Example                       | Supported values                                              |
|---------------------|-----------------------------------------------------------------------------------|----------|---------------------|-------------------------------|---------------------------------------------------------------|
 | url                 | The url of the LDES server                                                        | Yes      | N/A                 | http://localhost:8080/my-ldes | HTTP and HTTPS urls                                           |
 | source-format       | The 'Content-Type' that should be requested to the server.                        | No       | application/ld+json | application/n-quads           | Any type supported by [Apache Jena](https://jena.apache.org/) |
 | state               | 'sqlite' or 'memory' to indicate how the state should be persisted.               | No       | memory              | sqlite                        | 'sqlite' or 'memory'                                          |
 | keep-state          | Indicates if the state should be persisted on shutdown (n/a for in memory states) | No       | false               | false                         | true or false                                                 |
 | auth.type           | The type of authentication required by the LDES server                            | No       | NO_AUTH             | OAUTH2_CLIENT_CREDENTIALS     | NO_AUTH, API_KEY or OAUTH2_CLIENT_CREDENTIALS                 |
 | auth.api-key        | The api key when using auth.type 'API_KEY'                                        | No       | N/A                 | myKey                         | String                                                        |
 | auth.api-key-header | The header for the api key when using auth.type 'API_KEY'                         | No       | X-API-KEY           | X-API-KEY                     | String                                                        |
 | auth.client-id      | The client identifier when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'            | No       | N/A                 | myId                          | String                                                        |
 | auth.client-secret  | The client secret when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                | No       | N/A                 | mySecret                      | String                                                        |
 | auth.token-endpoint | The token endpoint when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'               | No       | N/A                 | http://localhost:8000/token   | HTTP and HTTPS urls                                           |

Example config:

```agsl
orchestrator:
  input:
    name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient
    config:
      url: http://localhost:8080/my-ldes
      sourceFormat: text/turtle
      auth:
        type: OAUTH2_CLIENT_CREDENTIALS
        client-id: clientId
        client-secret: secret
        token-endpoint: http://localhost:8000/token
```