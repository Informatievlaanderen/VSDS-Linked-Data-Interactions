# Kafka OUT

This output component listens to messages from a [kafka topic](https://kafka.apache.org).

Two security protocols are supported:
- [NO SECURITY](#no-security)
- [SASL SSL PLAIN](#sasl-ssl-plain)

## NO SECURITY

The following configuration is supported:

```json
outputs:
  - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut
    config:
      content-type: { any content type supported by Apache Jena }
      topics: { comma separated list of topics }
      bootstrap-servers: { uri of the bootstrap server }
      group-id: { optional group identifier }
```

Example:
```json
outputs:
  - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut
    config:
      content-type: application/n-quads
      topics: quickstart-events
      bootstrap-servers: localhost:9092
      group-id: testing_group
```

## SASL SSL PLAIN

The following configuration is supported:

```json
outputs:
  - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut
    config:
      content-type: { any content type supported by Apache Jena }
      topics: { comma separated list of topics }
      bootstrap-servers: { uri of the bootstrap server }
      group-id: { optional group identifier }
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
      topics: quickstart-events
      bootstrap-servers: localhost:9092
      group-id: testing_group
      security-protocol: SASL_SSL_PLAIN
      sasl-jaas-user: client
      sasl-jaas-password: client-secret
```