---
layout: default
parent: LDIO Inputs
title: HTTP In Poller
---

# LDIO HTTP In Poller

***Ldio:HttpInPoller***

The LDIO Http In Poller is a basic Http Poller that will poll a target URL on a specified interval. 

## Config
### General properties

| Property         | Description                                                        | Required | Default | Example                   | Supported values                                                                                                                                      |
|:-----------------|:-------------------------------------------------------------------|:---------|:--------|:--------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|
| _url_            | Target URL to poll from.                                           | Yes      | N/A     | http://example.com/my-api | HTTP and HTTPS urls (lists are supported)                                                                                                             |
| _cron_           | Cron expression to declare when the polling should take place [^2] | Yes[^1]  | N/A     | */10 * * * * *            | [Spring Cron Expression](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html) |
| _interval_       | Polling interval declared in ISO 8601 format.                      | Yes[^1]  | N/A     | PT1S                      | ISO 8601 formatted String                                                                                                                             |
| _continueOnFail_ | Indicated if continue if polling results in failure                | No       | true    | true                      | true or false                                                                                                                                         |

{% include ldio-core/http-requester.md %}

### Multiple urls
The Http In Poller supports polling multiple endpoints. Example configuration:

```yaml
name: Ldio:HttpInPoller
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

## Pausing

When paused, this component will stop making any of the scheduled HTTP-calls.
When resumed, it will restart these calls as if the component had been restarted, meaning any configured periods will start counting from the moment the pipeline was resumed instead of when it was originally created.

----

[^1]: Either choose the 'cron' option or the 'interval'. However, **the interval property will become deprecated**.
[^2]: The cron schedules are in timezone 'UTC'.