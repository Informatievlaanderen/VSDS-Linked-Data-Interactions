---
layout: default
parent: LDIO Inputs
title: AMQP In
---

# LDIO AMQP In

***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpIn***

The LDIO AMQP In listens to messages from
an [AMQP 1.0 queue](https://www.amqp.org/resources/specifications).

## Config

| Property     | Description                                 | Required | Default             | Example             | Supported values                                                                                                                                 |
|--------------|---------------------------------------------|----------|---------------------|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| remote-url   | URI to AMQP queue                           | Yes      | N/A                 | amqp://server:61616 | In line with `amqp[s]://hostname:port[?option=value[&option2=value...]]` or `amqpws[s]://hostname:port[/path][?option=value[&option2=value...]]` |
| queue        | Name of the queue                           | Yes      | N/A                 | quickstart-events   | String                                                                                                                                           |
| username     | Username used in authentication             | Yes      | N/A                 | client              | String                                                                                                                                           |
| password     | Password used in the authentication         | Yes      | N/A                 | secret              | String                                                                                                                                           |
| content-type | Content-type for received messages of queue | No       | application/n-quads | application/n-quads | Any content type supported by Apache Jena                                                                                                        |

## Example

```yaml
      input:
        name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpIn
        config:
          remote-url: amqp://localhost:61616
          username: artemis
          password: artemis
          queue: example
          content-type: application/ld+json
```