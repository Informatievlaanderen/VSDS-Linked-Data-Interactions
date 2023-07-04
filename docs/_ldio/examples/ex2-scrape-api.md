---
layout: default
parent: Examples
title: Scraping an API
nav_order: 2
has_toc: true
---

# Scraping an API

## Used Components

- [Http In Poller](../ldio-inputs/ldio-http-in-poller)
- [RML Adapter](../ldio-adapters/ldio-rml-adapter.md)
- [Version Object Creator](../ldio-transformers/ldio-version-object-creator)
- [Console Out](../ldio-outputs/ldio-console-out)

## Setup 

For this setup, we will periodically scrape a public API, map it with RML to Linked Data, Transform it to a Version Object and write it to console.

### RML Mapping

Since RML can sometimes be hard on human eyes, we'll convert our YARRRML to RML via [Matey].

Through this, we can convert this YARRRML to the following RML.

```yaml
prefixes:
 ex: "http://example.com/"
 cs: "http://www.cheapshark.com/"
 ldi: "http://www.vlaanderen.be/ns/ldi#"

mappings:
  person:
    sources:
      - ['deals.json~jsonpath', '$[*]']
    s: http://www.cheapshark.com/gamedeals/$(gameID)
    g: http://www.cheapshark.com/gamedeals/$(gameID)/$(lastChange)
    po:
      - [a, cs:GameDeal]
      - [cs:title, $(title)]
      - [cs:metacriticLink, $(metacriticLink)]
      - [cs:thumb, $(thumb)~iri]
      - p: cs:releaseDate
        o:
            function: ldi:epochToIso8601
            parameters:
            - [ldi:epoch, $(releaseDate) ]
            datatype: xsd:DateTime
      - p: cs:lastChange
        o:
            function: ldi:epochToIso8601
            parameters:
            - [ldi:epoch, $(lastChange) ]
            datatype: xsd:DateTime
            
      - [cs:isOnSale, $(isOnSale), xsd:Boolean]
      - [cs:normalPrice, $(normalPrice), xsd:Double]
      - [cs:salePrice, $(salePrice), xsd:Double]
````

[***mapping.ttl***](./ex2/ex2-mapping.ttl)

Let's save the mapping.ttl in our current directory.

***ldio.config.yaml:***
```yaml
orchestrator:
  pipelines:
    - name: data
      input:
        name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller
        config:
          url: https://www.cheapshark.com/api/1.0/deals?pageSize=1000
          interval: PT30M
        adapter:
          name: be.vlaanderen.informatievlaanderen.ldes.ldi.RmlAdapter
          config:
            mapping: "mapping.ttl"
      transformers:
        - name: be.vlaanderen.informatievlaanderen.ldes.ldi.VersionObjectCreator
          config:
            date-observed-property: "http://www.cheapshark.com/lastChange"
            member-type: "http://www.cheapshark.com/GameDeal"
            generatedAt-property: "https://w3id.org/ldes#timestampPath"
            versionOf-property: "https://w3id.org/ldes#versionOfPath"
      outputs:
        - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut
          config:
            content-type: text/turtle
```

## Execution

Once started, you should be seeing data in your console similar to 
````text
<http://www.cheapshark.com/gamedeals/157072/2023-06-28T21:31:20.000Z>
    a       <http://www.cheapshark.com/GameDeal> ;
    <http://www.cheapshark.com/isOnSale> "1"^^<http://www.w3.org/2001/XMLSchema#Boolean> ;
    <http://www.cheapshark.com/lastChange> "2023-06-28T21:31:20.000Z"^^<http://www.w3.org/2001/XMLSchema#DateTime> ;
    <http://www.cheapshark.com/metacriticLink> "/game/pc/one-piece-burning-blood---gold-edition" ;
    <http://www.cheapshark.com/normalPrice> "74.98"^^<http://www.w3.org/2001/XMLSchema#Double> ;
    <http://www.cheapshark.com/releaseDate> "2016-09-01T00:00:00.000Z"^^<http://www.w3.org/2001/XMLSchema#DateTime> ;
    <http://www.cheapshark.com/salePrice> "6.45"^^<http://www.w3.org/2001/XMLSchema#Double> ;
    <http://www.cheapshark.com/thumb> <https://gamersgatep.imgix.net/a/3/4/026d064cc7e1fb721f497398a3435dfcfbe0c43a.jpg?auto=&w=> ;
    <http://www.cheapshark.com/title> "ONE PIECE BURNING BLOOD GOLD EDITION" ;
    <https://w3id.org/ldes#timestampPath> "2023-06-28T21:31:20.000Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> ;
    <https://w3id.org/ldes#versionOfPath> <http://www.cheapshark.com/gamedeals/157072> .
````
[Matey]: https://rml.io/yarrrml/matey/#