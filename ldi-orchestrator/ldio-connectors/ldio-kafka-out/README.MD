# Kafka OUT
This output component sends messages to a [kafka topic](https://kafka.apache.org).
The following configuration is required:

```json
outputs:
  - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut
  config:
    content-type: { any content type supported by Apache Jena }
    bootstrap-servers: { uri of the bootstrap server }
    topic: { name of the topic }
```

Example:
```json
outputs:
  - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut
  config:
    content-type: application/n-quads
    bootstrap-servers: localhost:9092
    topic: quickstart-events
```