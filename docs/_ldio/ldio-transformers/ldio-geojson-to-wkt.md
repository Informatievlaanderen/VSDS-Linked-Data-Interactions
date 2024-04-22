---
layout: default
parent: LDIO Transformers
title: GeoJson To WKT Transformer
---

# LDIO GeoJson To WKT Transformer

***Ldio:GeoJsonToWktTransformer***

The GeoJson to Wkt Transformer will transform any [GeoJson] statements (with
predicate https://purl.org/geojson/vocab#geometry) to a [wkt string][WKT].

For example:

```json
{
  "https://purl.org/geojson/vocab#geojson:geometry": {
    "@type": "Point", 
    "https://purl.org/geojson/vocab#geojson:coordinates": [100.0, 0.0]
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

## Config

This component has no required config

[GeoJson]: https://geojson.org/

[WKT]: https://libgeos.org/specifications/wkt/