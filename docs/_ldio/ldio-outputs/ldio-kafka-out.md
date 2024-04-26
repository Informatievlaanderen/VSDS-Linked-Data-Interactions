---
layout: default
parent: LDIO Outputs
title: Kafka Out
---

# LDIO Kafka Out

***Ldio:KafkaOut***

The LDIO Kafka Out sends messages to a [kafka topic](https://kafka.apache.org).
Two security protocols are supported:

- [NO SECURITY](#no-security)
- [SASL SSL PLAIN](#sasl-ssl-plain)

## Config

| Property             | Description                                                           | Required | Default | Example                                   | Supported values                                                                     |
|----------------------|-----------------------------------------------------------------------|----------|---------|-------------------------------------------|--------------------------------------------------------------------------------------|
| _bootstrap-servers_  | Comma separated list of uris of the bootstrap servers                 | Yes      | N/A     | localhost:9012                            | url                                                                                  |
| _topic_              | Name of the topic                                                     | Yes      | N/A     | quickstart-events                         | String                                                                               |
| _key-property-path_  | Optional property path to extract the kafka key from the data model   | No       | null    | <http://purl.org/dc/terms/title>          | [ARQ property path](https://jena.apache.org/documentation/query/property_paths.html) |
| _security-protocol_  | Security protocol to be used to connect to the kafka broker           | No       | NO_AUTH | SASL_SSL_PLAIN                            | SASL_SSL_PLAIN or NO_AUTH                                                            |
| _sasl-jaas-user_     | Username used in the security protocol                                | No       | null    | client                                    | String                                                                               |
| *sasl-jaas-passwor*d | Password used in the security protocol                                | No       | null    | secret                                    | String                                                                               |
| _frame-type_         | RDF type of the objects that need to be included for JSON-LD framing. | No       | N/A     | http://purl.org/goodrelations/v1#Offering | Any RDF type                                                                         |

{% include ldio-core/rdf-writer.md %}

## Example

### NO SECURITY

```yaml
outputs:
  - name: Ldio:KafkaOut
    config:
      bootstrap-servers: localhost:9092
      topic: quickstart-events
      key-property-path: <https://purl.org/geojson/vocab#properties>/<http://purl.org/dc/terms/title>
```

### SASL SSL PLAIN

```yaml
outputs:
  - name: Ldio:KafkaOut
    config:
      bootstrap-servers: localhost:9092
      topic: quickstart-events
      key-property-path: <https://purl.org/geojson/vocab#properties>/<http://purl.org/dc/terms/title>
      security-protocol: SASL_SSL_PLAIN
      sasl-jaas-user: client
      sasl-jaas-password: client-secret
```