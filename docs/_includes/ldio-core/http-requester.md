### Additional LDIO Http Requester properties

| Property                  | Description                                                                                                                       | Required | Default   | Supported values                              | Example                     |
|:--------------------------|:----------------------------------------------------------------------------------------------------------------------------------|:---------|:----------|:----------------------------------------------|:----------------------------|
| auth.type                 | The type of authentication required by the LDES server                                                                            | No       | NO_AUTH   | NO_AUTH, API_KEY or OAUTH2_CLIENT_CREDENTIALS | OAUTH2_CLIENT_CREDENTIALS   |
| auth.api-key              | The api key when using auth.type 'API_KEY'                                                                                        | No       | N/A       | String                                        | myKey                       |
| auth.api-key-header       | The header for the api key when using auth.type 'API_KEY'                                                                         | No       | X-API-KEY | String                                        | X-API-KEY                   |
| auth.client-id            | The client identifier when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                                                            | No       | N/A       | String                                        | myId                        |
| auth.client-secret        | The client secret when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                                                                | No       | N/A       | String                                        | mySecret                    |
| auth.token-endpoint       | The token endpoint when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                                                               | No       | N/A       | HTTP and HTTPS urls                           | http://localhost:8000/token |
| auth.scope                | The Oauth2 scope when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'                                                                 | No       | N/A       | HTTP and HTTPS urls                           | http://localhost:8000/token |
| retries.enabled           | Indicates if the http client should retry http requests when the server cannot be reached.                                        | No       | true      | true or false                                 | true                        |
| retries.max               | Max number of retries the http client should do when retries.enabled = true                                                       | No       | 5         | Integer                                       | 100                         |
| retries.statuses-to-retry | Custom comma seperated list of http status codes that can trigger a retry in the http client.                                     | No       | N/A       | Comma seperated list of Integers              | 410,451                     |
| rate-limit.enabled        | Indicates if the http client should limit http requests when calling the server.                                                  | No       | false     | true or false                                 | false                       |
| rate-limit.limit          | Limit of requests per period, which is defined below, that the http client should do when `rate-limit.enabled = true`             | No       | 500       | Integer                                       | 100                         |
| rate-limit.period         | Period in which the limit of requests, which is defined above, can be reached by the http client when `rate-limit.enabled = true` | No       | PT1M      | ISO 8601 Duration                             | PT1H                        |
| http.headers.[].key/value | A list of custom http headers can be added. A key and value has to be provided for every header.                                  | No       | N/A       | String                                        | role                        |

```yaml
      config:
        http:
          headers:
            - key: role
              value: developer
            - key: alt-role
              value: programmer
        auth:
          type: API_KEY
          api-key: my-secret
          api-key-header: x-api-key
        retries:
          enabled: true
          max: 10
          statuses-to-retry: 410,451
        rate-limit:
          enabled: true
          period: P1D
          limit: 1000
```

### Retry

When retries are enabled, the following statuses are always retried, regardless of the configured statuses-to-retry:

- 5xx (500 and above)
- 429

### Rate limiter

> **NOTE**: Since 1.14.0, rate-limiter.max-requests-per-minute is deprecated, as this was too restrictive for
> configuring the rate limiter. Here is an example on how to convert the old config.
> 
> *Old config:*
> ```yaml
>       config:
>         rate-limit:
>           enabled: true
>           max-requests-per-minute: 500
> ```
> 
> *New config:*
> ```yaml
>       config:
>         rate-limit:
>           enabled: true
>           limit: 500
>           period: PT1M
> ```