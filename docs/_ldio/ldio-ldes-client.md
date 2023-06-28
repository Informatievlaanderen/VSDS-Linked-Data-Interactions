---
layout: default
title: LDIO LDES Client
grand_parent: Linked Data Interactions Orchestrator
parent: LDI Input
---

The following configuration is supported:

| Property                  | Description                                                                                   | Required | Default             | Example                       | Supported values                                              |
|:--------------------------|:----------------------------------------------------------------------------------------------|:---------|:--------------------|:------------------------------|:--------------------------------------------------------------|
| url                       | The url of the LDES server                                                                    | Yes      | N/A                 | http://localhost:8080/my-ldes | HTTP and HTTPS urls                                           |
| source-format             | The 'Content-Type' that should be requested to the server.                                    | No       | application/ld+json | application/n-quads           | Any type supported by [Apache Jena](https://jena.apache.org/) |
| state                     | 'sqlite' or 'memory' to indicate how the state should be persisted.                           | No       | memory              | sqlite                        | 'sqlite' or 'memory'                                          |
| keep-state                | Indicates if the state should be persisted on shutdown (n/a for in memory states)             | No       | false               | false                         | true or false                                                 |
| auth.type                 | The type of authentication required by the LDES server                                        | No       | NO_AUTH             | OAUTH2_CLIENT_CREDENTIALS     | NO_AUTH, API_KEY or OAUTH2_CLIENT_CREDENTIALS                 |
| auth.api-key              | The api key when using auth.type 'API_KEY'                                                    | No       | N/A                 | myKey                         | String                                                        |
| auth.api-key-header       | The header for the api key when using auth.type 'API_KEY'                                     | No       | X-API-KEY           | X-API-KEY                     | String                                                        |
| auth.client-id            | The client identifier when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                        | No       | N/A                 | myId                          | String                                                        |
| auth.client-secret        | The client secret when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                            | No       | N/A                 | mySecret                      | String                                                        |
| auth.token-endpoint       | The token endpoint when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                           | No       | N/A                 | http://localhost:8000/token   | HTTP and HTTPS urls                                           |
| retries.enabled           | Indicates if the http client should retry http requests when the server cannot be reached.    | No       | true                | true                          | true or false                                                 |
| retries.max               | Max number of retries the http client should do when retries.enabled = true                   | No       | 5                   | 100                           | Integer                                                       |
| retries.statuses-to-retry | Custom comma seperated list of http status codes that can trigger a retry in the http client. | No       | N/A                 | 410,451                       | Comma seperated list of Integers                              |
