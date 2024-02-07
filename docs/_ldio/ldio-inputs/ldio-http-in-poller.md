---
layout: default
parent: LDIO Inputs
title: HTTP In Poller
---

# LDIO HTTP In Poller
***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller***

The LDIO Http In Poller is a basic Http Poller that will poll a target URL on a specified interval. 

## Config

| Property       | Description                                                        | Required | Default | Example                   | Supported values                                                                                                                                      |
|:---------------|:-------------------------------------------------------------------|:---------|:--------|:--------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|
| url            | Target URL to poll from.                                           | Yes      | N/A     | http://example.com/my-api | HTTP and HTTPS urls (lists are supported)                                                                                                             |
| cron           | Cron expression to declare when the polling should take place [^2] | Yes[^1]  | N/A     | */10 * * * * *            | [Spring Cron Expression](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html) |
| interval       | Polling interval declared in ISO 8601 format.                      | Yes[^1]  | N/A     | PT1S                      | ISO 8601 formatted String                                                                                                                             |
| continueOnFail | Indicated if continue if polling results in failure                | No       | true    | true                      | true or false                                                                                                                                         |

This component uses the "LDIO Http Requester" to make the HTTP request.
Refer to [LDIO Http Requester](../ldio-core) for the config.

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

----

[^1]: Either choose the 'cron' option or the 'interval'. However, **the interval property will become deprecated**.
[^2]: The cron schedules are in timezone 'UTC'.