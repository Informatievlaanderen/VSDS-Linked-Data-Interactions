# Linked Data Interactions LDES Discoverer

A lightweight application that discovers the structure of an LDES or a view by retrieving all the tree node relations of
that LDES or view.

A use case for this could be when you are only interested in a part of the event stream. To know what part you can
follow, the structure can be discovered first.

## Config

| Property      | Description                                                         | Required | Default             | Example                   | Supported values                                              |
|:--------------|:--------------------------------------------------------------------|:---------|:--------------------|:--------------------------|:--------------------------------------------------------------|
| url           | Url where from the discoverer needs to start                        | Yes      | N/A                 | http://example.com/my-api | HTTP and HTTPS url                                            |
| source-format | The 'Content-Type' that should be requested to the server.          | No       | application/n-quads | text/turtle               | Any type supported by [Apache Jena](https://jena.apache.org/) |
| output-format | The RDF format that will be used to display all the found relations | No       | text/turtle         | application/ld+json       | Any type supported by [Apache Jena](https://jena.apache.org/) |

## How to run

In this example, we will try to discover the structure of an event stream of Geomobility.

For simplicity, we recommend passing the config as arguments to the application

### Maven

```shell
mvn spring-boot:run -Dspring-boot.run.arguments=--url="https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11"
```

### Running the JAR

First make sure the source code has been compiled to a JAR file. If not, execute following command:

```shell
mvn package -DskipTests
```

Now the JAR can be executed with the following command:

```shell
java -jar ./target/ldes-discoverer.jar --url="https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11"
```

### Docker

```shell
docker run ghcr.io/informatievlaanderen/ldes-discoverer --url="https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11"
```

> **NOTE**: when an url contains a `&` symbol, which will be picked up by the shell as an operator. In this example, if
> the url was not wrapped in quotation marks, the shell will try to execute three different command, where the last two
> will be `month=05` and `day=11`, which will fail of course.
>
> To resolve this, make sure the url is encapsulated in quotation marks.

### Example output

```turtle
@prefix prov: <http://www.w3.org/ns/prov#> .
@prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix tree: <https://w3id.org/tree#> .

<https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=05&pageNumber=3>
        tree:relation  [ rdf:type   tree:Relation;
                         tree:node  <https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=05&pageNumber=4>
                       ] .

<https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=05&pageNumber=2>
        tree:relation  [ rdf:type   tree:Relation;
                         tree:node  <https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=05&pageNumber=3>
                       ] .

<https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11>
        tree:relation  [ rdf:type    tree:InBetweenRelation;
                         tree:node   <https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=00>;
                         tree:path   prov:generatedAtTime;
                         tree:value  "2023-05-11T00"
                       ];
        tree:relation  [ rdf:type    tree:InBetweenRelation;
                         tree:node   <https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=05>;
                         tree:path   prov:generatedAtTime;
                         tree:value  "2023-05-11T05"
                       ];
        [...]
        tree:relation  [ rdf:type    tree:InBetweenRelation;
                         tree:node   <https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=20>;
                         tree:path   prov:generatedAtTime;
                         tree:value  "2023-05-11T20"
                       ] .

[...]

<https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=05>
        tree:relation  [ rdf:type   tree:Relation;
                         tree:node  <https://brugge-ldes.geomobility.eu/observations/by-time?year=2023&month=05&day=11&hour=05&pageNumber=1>
                       ] .

```