### LDIO Http Requester properties

#### Authentication properties

| Property            | Description                                                            | Required | Default   | Supported values                              | Example                     |
|:--------------------|:-----------------------------------------------------------------------|:---------|:----------|:----------------------------------------------|:----------------------------|
| _auth.type_           | The type of authentication required by the LDES server                 | No       | NO_AUTH   | NO_AUTH, API_KEY or OAUTH2_CLIENT_CREDENTIALS | OAUTH2_CLIENT_CREDENTIALS   |
| _auth.api-key_        | The api key when using auth.type 'API_KEY'                             | No       | N/A       | String                                        | myKey                       |
| _auth.api-key-header_ | The header for the api key when using auth.type 'API_KEY'              | No       | X-API-KEY | String                                        | X-API-KEY                   |
| _auth.client-id_      | The client identifier when using auth.type 'OAUTH2_CLIENT_CREDENTIALS' | No       | N/A       | String                                        | myId                        |
| _auth.client-secret_  | The client secret when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'     | No       | N/A       | String                                        | mySecret                    |
| _auth.token-endpoint_ | The token endpoint when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'    | No       | N/A       | HTTP and HTTPS urls                           | http://localhost:8000/token |
| _auth.scope_          | The Oauth2 scope when using auth.type 'OAUTH2_CLIENT_CREDENTIALS'      | No       | N/A       | HTTP and HTTPS urls                           | http://localhost:8000/token |

#### Retry properties

| Property                  | Description                                                                                   | Required | Default | Supported values                 | Example |
|:--------------------------|:----------------------------------------------------------------------------------------------|:---------|:--------|:---------------------------------|:--------|
| _retries.enabled_           | Indicates if the http client should retry http requests when the server cannot be reached.    | No       | true    | Boolean value                    | true    |
| _retries.max_               | Max number of retries the http client should do when retries.enabled = true                   | No       | 5       | Integer                          | 100     |
| _retries.statuses-to-retry_ | Custom comma seperated list of http status codes that can trigger a retry in the http client. | No       | N/A     | Comma seperated list of Integers | 410,451 |

When retries are enabled, the following statuses are always retried, regardless of the configured statuses-to-retry:

- 429
- 5xx (500 and above)

#### Rate limit properties

| Property           | Description                                                                                                                       | Required | Default | Supported values  | Example |
|:-------------------|:----------------------------------------------------------------------------------------------------------------------------------|:---------|:--------|:------------------|:--------|
| _rate-limit.enabled_ | Indicates if the http client should limit http requests when calling the server.                                                  | No       | false   | true or false     | false   |
| _rate-limit.limit_   | Limit of requests per period, which is defined below, that the http client should do when `rate-limit.enabled = true`             | No       | 500     | Integer           | 100     |
| _rate-limit.period_  | Period in which the limit of requests, which is defined above, can be reached by the http client when `rate-limit.enabled = true` | No       | PT1M    | ISO 8601 Duration | PT1H    |

#### Http headers

| Property                  | Description                                                                                      | Required | Default | Supported values | Example |
|:--------------------------|:-------------------------------------------------------------------------------------------------|:---------|:--------|:-----------------|:--------|
| _http.headers.[].key/value_ | A list of custom http headers can be added. A key and value has to be provided for every header. | No       | N/A     | String           | role    |


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

