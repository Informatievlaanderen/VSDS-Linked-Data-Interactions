# HTTP input poller

This component serves as an input for a ldi pipeline.
Its purpose is to repeatedly call an external endpoint after a given interval and pass the content of the response to a LDI adapter.


### Configuration

There are 3 properties that can be configured, the target url and the polling interval.
The target url is the endpoint which this component will periodically call to receive the content that will be pased to the adapter.
The interval is the time inbetween calls. It can be set in the [ISO 8601](https://tc39.es/proposal-temporal/docs/duration.html) format.


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