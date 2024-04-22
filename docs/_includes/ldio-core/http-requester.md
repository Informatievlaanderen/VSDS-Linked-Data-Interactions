### LDIO Http Requester properties

#### Authentication properties

| Property            | Description                                                            | Required | Default   | Supported values                              | Example                     |
|:--------------------|:-----------------------------------------------------------------------|:---------|:----------|:----------------------------------------------|:----------------------------|
| auth.type           | The type of authentication required by the LDES server                 | No       | NO_AUTH   | NO_AUTH, API_KEY or OAUTH2_CLIENT_CREDENTIALS | OAUTH2_CLIENT_CREDENTIALS   |
|                     |                                                                        |          |           |                                               |                             |
| auth.api-key        | The api key when using auth.type 'API_KEY'                             | No       | N/A       | String                                        | myKey                       |
| auth.api-key-header | The header for the api key when using auth.type 'API_KEY'              | No       | X-API-KEY | String                                        | X-API-KEY                   |
|                     |                                                                        |          |           |                                               |                             |
| auth.client-id      | The client identifier when using auth.type 'OAUTH2_CLIENT_CREDENTIALS' | No       | N/A       | String                                        | myId                        |
| auth.client-secret  | The client secret when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'     | No       | N/A       | String                                        | mySecret                    |
| auth.token-endpoint | The token endpoint when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'    | No       | N/A       | HTTP and HTTPS urls                           | http://localhost:8000/token |
| auth.scope          | The Oauth2 scope when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'      | No       | N/A       | HTTP and HTTPS urls                           | http://localhost:8000/token |

#### Retry properties

| Property                  | Description                                                                                   | Required | Default | Supported values                 | Example |
|:--------------------------|:----------------------------------------------------------------------------------------------|:---------|:--------|:---------------------------------|:--------|
| retries.enabled           | Indicates if the http client should retry http requests when the server cannot be reached.    | No       | true    | Boolean value                    | true    |
| retries.max               | Max number of retries the http client should do when retries.enabled = true                   | No       | 5       | Integer                          | 100     |
| retries.statuses-to-retry | Custom comma seperated list of http status codes that can trigger a retry in the http client. | No       | N/A     | Comma seperated list of Integers | 410,451 |

When retries are enabled, the following statuses are always retried, regardless of the configured statuses-to-retry:

- 429
- 5xx (500 and above)

#### Rate limit properties

| Property           | Description                                                                                                                       | Required | Default | Supported values  | Example |
|:-------------------|:----------------------------------------------------------------------------------------------------------------------------------|:---------|:--------|:------------------|:--------|
| rate-limit.enabled | Indicates if the http client should limit http requests when calling the server.                                                  | No       | false   | true or false     | false   |
| rate-limit.limit   | Limit of requests per period, which is defined below, that the http client should do when `rate-limit.enabled = true`             | No       | 500     | Integer           | 100     |
| rate-limit.period  | Period in which the limit of requests, which is defined above, can be reached by the http client when `rate-limit.enabled = true` | No       | PT1M    | ISO 8601 Duration | PT1H    |

#### Http headers

| Property                  | Description                                                                                      | Required | Default | Supported values | Example |
|:--------------------------|:-------------------------------------------------------------------------------------------------|:---------|:--------|:-----------------|:--------|
| http.headers.[].key/value | A list of custom http headers can be added. A key and value has to be provided for every header. | No       | N/A     | String           | role    |


<details>
    <summary>Example Http Requester config</summary>

<div markdown="1">
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
</div>


</details>

