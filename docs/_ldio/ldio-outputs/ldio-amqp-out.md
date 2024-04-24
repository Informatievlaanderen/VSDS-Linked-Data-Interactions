---
layout: default
parent: LDIO Outputs
title: AMQP Out
---

# LDIO AMQP Out

***Ldio:AmqpOut***

The LDIO AMQP Out sends messages to an [AMQP 1.0 queue](https://www.amqp.org/resources/specifications).
The content-type configured in the rdf-writer.content-type
is added as a header to the message with key "contentType".

## Config

| Property     | Description                         | Required | Default | Supported values                                                                                                                                 | Example             |
|--------------|-------------------------------------|----------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------|---------------------|
| _remote-url_ | URI to AMQP queue                   | Yes      | N/A     | In line with `amqp[s]://hostname:port[?option=value[&option2=value...]]` or `amqpws[s]://hostname:port[/path][?option=value[&option2=value...]]` | amqp://server:61616 |
| _queue_      | Name of the queue                   | Yes      | N/A     | String                                                                                                                                           | quickstart-events   |
| _username_   | Username used in authentication     | Yes      | N/A     | String                                                                                                                                           | client              |
| _password_   | Password used in the authentication | Yes      | N/A     | String                                                                                                                                           | secret              |

{% include ldio-core/rdf-writer.md %}

## Example

```yaml
      outputs:
        - name: Ldio:AmqpOut
          config:
            remote-url: amqp://localhost:61616
            username: artemis
            password: artemis
            queue: example
            rdf-writer:
              content-type: application/n-quads
```