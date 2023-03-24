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

No config is required. A valid geojson+ld as input is the only requirement.