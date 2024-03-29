---
layout: default
parent: LDIO Outputs
title: AMQP Out
---

# LDIO AMQP Out

***Ldio:AmqpOut***

The LDIO AMQP Out sends messages to an [AMQP 1.0 queue](https://www.amqp.org/resources/specifications).
The content-type configured in the [rdf-writer](../ldio-core/ldio-rdf-writer.md) 
is added as a header to the message with key "contentType".

## Config

| Property    | Description                         | Required | Default        | Example             | Supported values                                                                                                                                 |
|-------------|-------------------------------------|----------|----------------|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| remote-url  | URI to AMQP queue                   | Yes      | N/A            | amqp://server:61616 | In line with `amqp[s]://hostname:port[?option=value[&option2=value...]]` or `amqpws[s]://hostname:port[/path][?option=value[&option2=value...]]` |
| queue       | Name of the queue                   | Yes      | N/A            | quickstart-events   | String                                                                                                                                           |
| username    | Username used in authentication     | Yes      | N/A            | client              | String                                                                                                                                           |
| password    | Password used in the authentication | Yes      | N/A            | secret              | String                                                                                                                                           |
| rdf-writer  | LDI RDF Writer Config               | No       | Empty Config   | N/A                 | [LDI RDF Writer Config](../ldio-core/ldio-rdf-writer)                                                                                            |

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