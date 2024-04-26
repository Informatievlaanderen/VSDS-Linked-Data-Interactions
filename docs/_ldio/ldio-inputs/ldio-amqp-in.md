---
layout: default
parent: LDIO Inputs
title: AMQP In
---

# LDIO AMQP In

***Ldio:AmqpIn***

The LDIO AMQP In listens to messages from
an [AMQP 1.0 queue](https://www.amqp.org/resources/specifications).

## Config

| Property       | Description                                             | Required | Default             | Example             | Supported values                                                                                                                                 |
|----------------|---------------------------------------------------------|----------|---------------------|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| _remote-url_   | URI to AMQP queue                                       | Yes      | N/A                 | amqp://server:61616 | In line with `amqp[s]://hostname:port[?option=value[&option2=value...]]` or `amqpws[s]://hostname:port[/path][?option=value[&option2=value...]]` |
| _queue_        | Name of the queue                                       | Yes      | N/A                 | quickstart-events   | String                                                                                                                                           |
| _username_     | Username used in authentication                         | Yes      | N/A                 | client              | String                                                                                                                                           |
| _password_     | Password used in the authentication                     | Yes      | N/A                 | secret              | String                                                                                                                                           |
| _content-type_ | Content-type suggestion* for received messages of queue | No       | application/n-quads | application/n-quads | Any content type supported by [Apache Jena](https://jena.apache.org/documentation/io/rdf-input.html#determining-the-rdf-syntax)                  |

* When the header of the message contains a "contentType" property, the listener will use the content type provided by
  the header.

## Example

```yaml
      input:
        name: Ldio:AmqpIn
        config:
          remote-url: amqp://localhost:61616
          username: artemis
          password: artemis
          queue: example
          content-type: application/ld+json
```

## Pausing

When paused, this component will not receive any messages from the queue and will start syncing with the queue when
unpaused.