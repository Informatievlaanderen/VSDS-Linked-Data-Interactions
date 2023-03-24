# GEOJSON to WKT
This transformer component replaces:
`https://purl.org/geojson/vocab#geojson:geometry` 
with `http://www.w3.org/ns/locn#geometry` containing a `http://www.opengis.net/ont/geosparql#wktLiteral` as value.

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

Requirements:
- The expected input format (e.g. `application/n-quads`, `application/ld+json`, etc.) should be configured as `DATA_SOURCE_FORMAT`. 
- The model should contain valid geojson+ld.