---
layout: default
parent: LDIO Transformers
title: Http Enricher Transformer
---

# LDIO Http Enricher

***Ldio:HttpEnricher***

A transformer which allows to send a GET or POST HTTP request to a dynamic URL provided by the model.
The response is converted to linked data and added to the incoming model.

## Config

| Property                    | Description                                                                                                 | Required | Default | Supported values                 | Example                                             |
|:----------------------------|:------------------------------------------------------------------------------------------------------------|:---------|:--------|:---------------------------------|:----------------------------------------------------|
| _adapter.name_              | This transformer requires an [ldio-adapter](../ldio-adapters) to convert the responses to linked data.      | Yes      | N/A     | Paths of supported LDIO Adapters | Ldio:RdfAdapter                                     |
| _adapter.config.xxx_        | Optional config that may be required by the adapter                                                         | No       | N/A     | Paths of supported LDIO Adapters | Ldio:RdfAdapter                                     |
| _url-property-path_         | Path defining the url that needs to be selected on the model for the http request.                          | Yes      | N/A     | Valid property paths             | <http://example.org/url>                            |
| _header-property-path_      | Path defining the headers that needs to be selected on the model for the http request.                      | No       | N/A     | Valid property paths             | <http://example.org/header>                         |
| _body-property-path_        | Path defining the body that needs to be selected on the model to be added when a POST http request is used. | No       | N/A     | Valid property paths             | <http://example.org/meta>/<http://example.org/body> |
| _http-method-property-path_ | Path defining the http method that needs to be selected on the model for the http request.                  | No       | GET     | GET or POST                      | GET                                                 |

{% include ldio-core/http-requester.md %}

Note that all adapters are supported. When the adapter requires additional config, this can be added as seen below in
the example.

Example:

This example contains a JsonToLdAdapter which needs "core-context" as config.
At the bottom there is also "auth" config provided for the request executor.

```yaml
        - name: Ldio:HttpEnricher
          config:
            adapter:
              name: Ldio:JsonToLdAdapter
              config:
                core-context: file:///ldio/jsonld/observation.jsonld
            url-property-path: <http://example.org/url>
            header-property-path: <http://example.org/meta>/<http://example.org/headers>
            body-property-path: <http://example.org/meta>/<http://example.org/body>
            http-method-property-path: <http://example.org/meta>/<http://example.org/method>
            auth:
              type: API_KEY
              api-key: my-secret
              api-key-header: x-api-key
```