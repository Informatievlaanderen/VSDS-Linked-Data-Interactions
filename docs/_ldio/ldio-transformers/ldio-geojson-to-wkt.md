---
layout: default
parent: LDIO Transformers
title: GeoJson To WKT Transformer
---

# LDIO GeoJson To WKT Transformer

***Ldio:GeoJsonToWktTransformer***

The GeoJson to Wkt Transformer will transform any [GeoJson] statements (with
predicate https://purl.org/geojson/vocab#geometry) to a [wkt string][WKT].

When the `transform-to-rdf+wkt-enabled` configuration is enabled, GeoJSON statements will be converted into [RDF+WKT]
format.

For example:

```json
{
  "https://purl.org/geojson/vocab#geojson:geometry": {
    "@type": "Point",
    "https://purl.org/geojson/vocab#geojson:coordinates": [
      100.0,
      0.0
    ]
  }
}
```

becomes:

```json
{
  "http://www.w3.org/ns/locn#geometry": {
    "@value": "POINT (100 0)",
    "@type": "http://www.opengis.net/ont/geosparql#wktLiteral"
  }
}
```

With `transform-to-rdf+wkt-enabled` set to `true` it becomes:

```turtle
@prefix geojson: <https://purl.org/geojson/vocab#> .
@prefix sf: <http://www.opengis.net/ont/sf#> .
@prefix geo: <http://www.opengis.net/ont/geosparql#> .
@prefix locn: <http://www.w3.org/ns/locn#> .

<http://example.com/features/point>
        a       geojson:Feature;
        locn:geometry
                [ a       sf:Point;
                  geo:asWKT
                          "POINT (100 0)"^^geo:wktLiteral
                ] .
```

## Config

| Property                     | Description                                                                          | Required | Default | Example | Supported values |
|:-----------------------------|:-------------------------------------------------------------------------------------|:---------|:--------|:--------|:-----------------|
| transform-to-rdf+wkt-enabled | Transform GeoJson to RDF+WKT format, defaults to false and the default format is WKT | No       | false   | false   | true/false       |

[GeoJson]: https://geojson.org/

[WKT]: https://libgeos.org/specifications/wkt/

[RDF+WKT]: https://semiceu.github.io/Core-Location-Vocabulary/releases/w3c/#locn:geometry