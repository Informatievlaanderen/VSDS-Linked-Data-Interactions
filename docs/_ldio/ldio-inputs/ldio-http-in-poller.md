---
layout: default
parent: LDIO Inputs
title: HTTP In Poller
---

# LDIO HTTP In Poller
***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller***

The LDIO Http In Poller is a basic Http Poller that will poll a target URL on a specified interval. 

## Config

| Property                            | Description                                                                                   | Required | Default   | Example                     | Supported values                              |
|:------------------------------------|:----------------------------------------------------------------------------------------------|:---------|:----------|:----------------------------|:----------------------------------------------|
| url                                 | Target URL to poll from.                                                                      | Yes      | N/A       | http://example.com/my-api   | HTTP and HTTPS urls (lists are supported)     |
| interval                            | Polling interval declared in ISO 8601 format.                                                 | Yes      | N/A       | PT1S                        | ISO 8601 formatted String                     |
| continueOnFail                      | Indicated if continue if polling results in failure                                           | No       | true      | true                        | true or false                                 |
| auth.type                           | The type of authentication required by the LDES server                                        | No       | NO_AUTH   | OAUTH2_CLIENT_CREDENTIALS   | NO_AUTH, API_KEY or OAUTH2_CLIENT_CREDENTIALS |
| auth.api-key                        | The api key when using auth.type 'API_KEY'                                                    | No       | N/A       | myKey                       | String                                        |
| auth.api-key-header                 | The header for the api key when using auth.type 'API_KEY'                                     | No       | X-API-KEY | X-API-KEY                   | String                                        |
| auth.client-id                      | The client identifier when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                        | No       | N/A       | myId                        | String                                        |
| auth.client-secret                  | The client secret when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                            | No       | N/A       | mySecret                    | String                                        |
| auth.token-endpoint                 | The token endpoint when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                           | No       | N/A       | http://localhost:8000/token | HTTP and HTTPS urls                           |
| retries.enabled                     | Indicates if the http client should retry http requests when the server cannot be reached.    | No       | true      | true                        | true or false                                 |
| retries.max                         | Max number of retries the http client should do when retries.enabled = true                   | No       | 5         | 100                         | Integer                                       |
| retries.statuses-to-retry           | Custom comma seperated list of http status codes that can trigger a retry in the http client. | No       | N/A       | 410,451                     | Comma seperated list of Integers              |
| rate-limit.enabled                  | Indicates if the http client should limit http requests when calling the server.              | No       | false     | false                       | true or false                                 |
| rate-limit.max-requests-per-minute  | Max number of requests per minute the http client should do when rate-limit.enabled = true    | No       | 500       | 500                         | Integer                                       |

The Http In Poller supports polling multiple endpoints. Example configuration:

```yaml
name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller
config:
  auth:
    type: API_KEY
    api-key: my-key
    api-key-header: X-API-Key
  url:
    - https://webhook.site/6cb49dd1-aa05-4e77-8870-f06903805b30
    - https://webhook.site/e8078b99-4b09-496d-baa8-8ba309dec6b6
  interval: PT3S
```

When using multiple endpoints, the other config (auth config, interval, etc.) applies to all endpoints.