---
  layout: default
  parent: LDIO Inputs
  title: LDES Client with Connector
---

# LDIO Ldes Client Connector

***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnector***

An EDC (Eclipse dataspace Connector) LDIO wrapper component for the [LDI LDES Client building block](../../core/ldi-inputs/ldes-client)

This component adds EDC support to [the ldio ldes client](./ldio-ldes-client.md). If you'd like to know how to configure the LDES Client,
we refer to [the ldio ldes client](./ldio-ldes-client.md).
The additional functionality provided by this component makes it possible to use the Ldes Client to consume an LDES through an EDC connector.
This component exposes two endpoints:

1. http://<host>:<port>/<pipelines.name>/transfer
   The Ldio component will start the data transfer with the connector. You have to send the transfer request to
   the LdioLdesClientConnector instead of the EDC consumer connector. The LDIO Ldes Client Connector will start the transfer
   with the connector and also keep the transfer alive while consuming the LDES (e.g. request a new token when it expires).
3. http://<host>:<port>/<pipelines.name>/token
   This endpoint should never be called directly. This is the callback to be provided in the transfer request.
   The EDC connector will use this callback endpoint to provide the LDES Client with a token.

![img](./art/ldes-client-connector.svg)

## Config

| Property               | Description                                                                                                     | Required | Default      | Example                                                         | Supported values    |
|:-----------------------|:----------------------------------------------------------------------------------------------------------------|:---------|:-------------|:----------------------------------------------------------------|:--------------------|
| connector-transfer-url | The transfer url of the EDC connector which has to be called to start a transfer                                | Yes      | N/A          | http://consumer-connector:29193/management/v2/transferprocesses | HTTP and HTTPS urls |
| proxy-url-to-replace   | Makes it possible to proxy a part of the url of the LDES**. Indicates which part of the url should be replaced. | No       | empty string | http://ldes-behind-connectors.dev                               | string              |
| proxy-url-replacement  | Makes it possible to proxy a part of the url of the LDES**. Indicates the replacement url part.                 | No       | memory       | http://consumer-connector:29193                                 | string              |
** The url mentioned here are the actual url's used by the LDES Server (hostname). These are included in the results bodies to indicate relations, etc. This is a temporary solution until the client and server support relative urls.


## Examples

```yaml
input:
  name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClientConnector
  config:
    url: http://consumer-connector:29291/public
    connector-transfer-url: http://consumer-connector:29193/management/v2/transferprocesses
    proxy-url-to-replace: http://localhost:8081/devices
    proxy-url-replacement: http://consumer-connector:29291/public
    source-format: application/n-quads
```
