---
layout: default
parent: LDIO Inputs
title: Kafka In
---

# LDIO Kafka In

***Ldio:KafkaIn***

The LDIO Kafka In listens to messages from a [kafka topic](https://kafka.apache.org).

Two security protocols are supported:

- [NO SECURITY](#no-security)
- [SASL SSL PLAIN](#sasl-ssl-plain)

## Config

| Property             | Description                                                 | Required | Default         | Example             | Supported values                                                                                                        |
|----------------------|-------------------------------------------------------------|----------|-----------------|---------------------|-------------------------------------------------------------------------------------------------------------------------|
| _content-type_       | Any content type supported by Apache Jena                   | Yes      | N/A             | application/n-quads | Any type supported by [Apache Jena](https://jena.apache.org/documentation/io/rdf-input.html#determining-the-rdf-syntax) |
| _bootstrap-servers_  | Comma separated list of uris of the bootstrap servers       | Yes      | N/A             | localhost:9012      | url                                                                                                                     |
| _topics_             | Names of the topics (comma separated)                       | Yes      | N/A             | quickstart-events   | String                                                                                                                  |
| _group-id_           | Group identifier the consumer belongs to                    | No       | generated value | group-1             | String                                                                                                                  |
| _security-protocol_  | Security protocol to be used to connect to the kafka broker | No       | NO_AUTH         | SASL_SSL_PLAIN      | SASL_SSL_PLAIN or NO_AUTH                                                                                               |
| _sasl-jaas-user_     | Username used in the security protocol                      | No       | null            | client              | String                                                                                                                  |
| _sasl-jaas-password_ | Password used in the security protocol                      | No       | null            | secret              | String                                                                                                                  |

## Example

### NO SECURITY

```yaml
outputs:
  - name: Ldio:KafkaIn
    config:
      content-type: application/n-quads
      topics: quickstart-events
      bootstrap-servers: localhost:9092
```

### SASL SSL PLAIN

```yaml
outputs:
  - name: Ldio:KafkaIn
    config:
      content-type: application/n-quads
      topics: quickstart-events
      bootstrap-servers: localhost:9092
      group-id: testing_group
      security-protocol: SASL_SSL_PLAIN
      sasl-jaas-user: client
      sasl-jaas-password: client-secret
```

## Pausing

When paused, this component will stop listening to the kafka topics.
When resumed, it will try to resync with all topics.