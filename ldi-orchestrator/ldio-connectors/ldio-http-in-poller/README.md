# HTTP input poller

This component serves as an input for a ldi pipeline.
Its purpose is to repeatedly call an external endpoint after a given interval and pass the content of the response to a LDI adapter.


### Configuration

| Property       | Description                                             | Required | Default | Example                       | Supported values                                                        |
|----------------|---------------------------------------------------------|----------|---------|-------------------------------|-------------------------------------------------------------------------|
| url            | The target url which the poller will call to            | Yes      | N/A     | http://localhost:8080/my-ldes | HTTP and HTTPS urls                                                     |
| interval       | The time between calls to the endpoint                  | Yes      | N/A     | PT1M                          | [ISO 8601](https://tc39.es/proposal-temporal/docs/duration.html) format |
| continueOnFail | If the poller should continue or not of an error occurs | No       | true    | true                          | true or false                                                           |


#### Example

```agsl
orchestrator:
  input:
    name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller
    config:
      url: http://localhost:8080/get
      interval: PT1M
      continueOnFail: true
```