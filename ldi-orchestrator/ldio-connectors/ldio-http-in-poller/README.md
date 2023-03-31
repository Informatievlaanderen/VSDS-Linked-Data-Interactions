# HTTP input poller

This component serves as an input for a ldi pipeline.


### Configuration

There are 2 properties that can be configured, the target url and the polling interval.
The target url is the endpoint which this component will periodically call to 

#### Example

```agsl
orchestrator:
  input:
    name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller
    config:
      url: http://localhost:8080/get
      interval: PT1M
```