# Kafka OUT

This output component sends messages to a [kafka topic](https://kafka.apache.org).
Two security protocols are supported:
- [NO SECURITY](#no-security)
- [SASL SSL PLAIN](#sasl-ssl-plain)

## NO SECURITY

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

## SASL SSL PLAIN

The following configuration is required:

```json
outputs:
  - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut
    config:
      content-type: { any content type supported by Apache Jena }
      bootstrap-servers: { uri of the bootstrap server }
      topic: { name of the topic }
      security-protocol: SASL_SSL_PLAIN
      sasl-jaas-user: { username }
      sasl-jaas-password: { password }
```

Example:
```json
outputs:
  - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut
    config:
      content-type: application/n-quads
      bootstrap-servers: localhost:9092
      topic: quickstart-events
      security-protocol: SASL_SSL_PLAIN
      sasl-jaas-user: client
      sasl-jaas-password: client-secret
```