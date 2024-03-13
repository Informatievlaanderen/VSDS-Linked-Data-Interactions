---
title: Linked Data Interactions LDES Discoverer
layout: home
nav_order: 1
---

# Linked Data Interactions LDES Discoverer

A lightweight application that discovers the structure of an LDES or a view by retrieving all the tree node relations of
that LDES or view.

A use case for this could be when you are only interested in a part of the event stream. To know what part you can
follow, the structure can be discovered first.

## Config

| Property      | Description                                                | Required | Default             | Example                   | Supported values                                                                                                        |
|:--------------|:-----------------------------------------------------------|:---------|:--------------------|:--------------------------|:------------------------------------------------------------------------------------------------------------------------|
| url           | Url where from the discoverer needs to start               | Yes      | N/A                 | http://example.com/my-api | HTTP and HTTPS url                                                                                                      |
| source-format | The 'Content-Type' that should be requested to the server. | No       | application/n-quads | text/turtle               | Any type supported by [Apache Jena](https://jena.apache.org/documentation/io/rdf-input.html#determining-the-rdf-syntax) |

## How to run

This tutorial will show how to use the discoverer in Docker.
In this example, we will try to discover the structure of an event stream of Geomobility.

For simplicity, we recommend passing the config as arguments, like shown below:

```shell
docker run ldes/ldes-discoverer --url="http://ldes-server/observations"
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