---
title: Linked Data Interactions LDES Discoverer
layout: home
nav_order: 0
---

# Linked Data Interactions LDES Discoverer

A lightweight application that discovers the structure of an LDES or a view by retrieving all the tree node relations of
that LDES or view.

A use case for this could be when you are only interested in a part of the event stream. To know what part you can
follow, the structure can be discovered first.

## Config

### Base configuration

| Property        | Description                                                | Required | Default     | Example                   | Supported values                                                                                                        |
|:----------------|:-----------------------------------------------------------|:---------|:------------|:--------------------------|:------------------------------------------------------------------------------------------------------------------------|
| _url_           | Url where from the discoverer needs to start               | true     | N/A         | http://example.com/my-api | HTTP and HTTPS url                                                                                                      |
| _source_-format | The 'Content-Type' that should be requested to the server. | false    | text/turtle | application/n-quads       | Any type supported by [Apache Jena](https://jena.apache.org/documentation/io/rdf-input.html#determining-the-rdf-syntax) |

### Optional config

#### Authentication

| Property         | Description                                                            | Default   | Example                     | Supported values                              |
|:-----------------|:-----------------------------------------------------------------------|:----------|:----------------------------|:----------------------------------------------|
| _auth-type_      | The type of authentication required by the LDES server                 | NO_AUTH   | OAUTH2_CLIENT_CREDENTIALS   | NO_AUTH, API_KEY or OAUTH2_CLIENT_CREDENTIALS |
| _api-key_        | The api key when using auth-type 'API_KEY'                             | N/A       | myKey                       | String                                        |
| _api-key-header_ | The header for the api key when using auth-type 'API_KEY'              | X-API-KEY | X-API-KEY                   | String                                        |
| _client-id_      | The client identifier when using auth-type 'OAUTH2_CLIENT_CREDENTIALS' | N/A       | myId                        | String                                        |
| _client-secret_  | The client secret when using auth-type 'OAUTH2_CLIENT_CREDENTIALS'     | N/A       | mySecret                    | String                                        |
| _token-endpoint_ | The token endpoint when using auth-type 'OAUTH2_CLIENT_CREDENTIALS'    | N/A       | http://localhost:8000/token | HTTP and HTTPS urls                           |
| _scope_          | The Oauth2 scope when using auth-type 'OAUTH2_CLIENT_CREDENTIALS'      | N/A       | http://localhost:8000/token | HTTP and HTTPS urls                           |

#### Further customization

| Property            | Description                                                                                                         | Default | Example                | Supported values                 |
|:--------------------|:--------------------------------------------------------------------------------------------------------------------|:--------|:-----------------------|:---------------------------------|
| _disable-retry_     | Boolean flag that disables retrying to send http requests when the server cannot be reached. (enabled when omitted) | N/A     | N/A                    | N/A                              |
| _retry-limit_       | Max number of retries the http client should do (only on absence of disable-retry)                                  | 5       | 100                    | Integer                          |
| _retry-statuses_    | Custom comma seperated list of http status codes that can trigger a retry in the http client.                       | N/A     | 410,451                | Comma seperated list of Integers |
| _rate-limit_        | Limit of requests per period, which is defined below, that the http client should do                                | N/A     | 500                    | Integer                          |
| _rate-limit-period_ | Period in which the limit of requests, which is defined above, can be reached by the http client                    | PT1M    | PT1H                   | ISO 8601 Duration                |
| _header_            | Parameter for each individual header that is required                                                               | N/A     | Connection: keep-alive | String                           |

## How to run

This tutorial will show how to use the discoverer in Docker.
In this example, we will try to discover the structure of an event stream called observations.

For simplicity, we recommend passing the config as arguments.

**Run the ldes-discoverer with minimal config**

```shell
docker run ldes/ldes-discoverer --url="http://ldes-server/observations"
```

**Run the ldes-discoverer with rate-limit, authentication and two additional headers**

```shell
docker run ldes/ldes-discoverer --url="http://ldes-server/observations" --retry-limit=3 --rate-limit=400 \
 --header="Connection: keep alive" --header="X-Source-App: ldes-discoverer" \
 --auth-type=API_KEY --api-key="my-secret-api-key"
```

> **NOTE**: when an url contains a `&` symbol, which will be picked up by the shell as an operator.
> For instance, running
> `docker run ldes/ldes-discoverer --url=http://ldes-server/observations?year=2023&month=05&day=11` will result in the
> shell trying to execute three different command, where the last two will be `month=05` and `day=11`, which will fail
> of course.
>
> To resolve this, make sure the url is encapsulated in quotation marks.

### Example output

In the logging of the application, both the total number of relations and the relations itself will be displayed. This
will be present as the last logging statement of the app and would look something like this:

```text
2024-02-08T14:26:25.279+01:00  INFO 48176 --- [           main] b.v.i.l.l.d.common.LdesDiscovererExecutor        : http://ldes-server/observations contains a total of 10 relations:
http://ldes-server/observations
+- http://ldes-server/observations/by-time
|  +- http://ldes-server/observations/by-time?year=2022
|  |  +- http://ldes-server/observations/by-time?year=2022&month=08
|  +- http://ldes-server/observations/by-time?year=2023
|     +- http://ldes-server/observations/by-time?year=2023&month=05
|        +- http://ldes-server/observations/by-time?year=2023&month=05&day=07
|        +- http://ldes-server/observations/by-time?year=2023&month=05&day=16
|        +- http://ldes-server/observations/by-time?year=2023&month=05&day=20
+- http://ldes-server/observations/paged
   +- http://ldes-server/observations/paged?pageNumber=1
```