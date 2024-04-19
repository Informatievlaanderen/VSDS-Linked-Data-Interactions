---
layout: default
parent: LDIO Adapters
title: RDF Adapter
---

# LDIO RDF Adapter

***Ldio:RdfAdapter***

As the most basic adapter, the RDF Adapter will take in an RDF string and convert it
into an internal Linked Data model based on the given content type.

## Notes

This Adapter only supports valid RDF mime types

## Config

| Property                  | Description                                                                                                                                               | Required | Default | Example | Supported values |
|:--------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------|:---------|:--------|:--------|:-----------------|
| max-jsonld-cache-capacity | After retrieving an external JSON-LD context, it is cached for reuse. This property allows to specify the size of this cache (number of stored contexts). | No       | 100     | 100     | Integer          |

## Example

A simple pipeline with the RDF adapter can be created with the following configuration

```yaml
orchestrator:
  pipelines:
    - name: park-n-ride-pipeline
      description: "Polls for CSV park-and-ride data, converts to linked data and creates versions"
      input:
        name: Ldio:HttpIn
        adapter:
          name: Ldio:RdfAdapter
      outputs:
        - name: Ldio:ConsoleOut
```

An example of an input message can be sent to: `http://localhost:<port>/park-n-ride-pipeline`
```json
{
    "@context": {
        "@vocab": "https://example.org/ns/mobility#",
        "urllinkaddress": "@id",
        "type": "@type",
        "lastupdate": {
            "@type": "http://www.w3.org/2001/XMLSchema#dateTime"
        }
    },
    "name": "Parking",
    "lastupdate": "2023-11-30T21:45:15+01:00",
    "type": "offStreetParkingGround",
    "urllinkaddress": "https://stad.gent/nl/mobiliteit-openbare-werken/parkeren/park-and-ride-pr/pr-gentbrugge-arsenaal",
    "numberofspaces": 0,
    "availablespaces": 0,
    "location": {
        "lon": 3.7583663653,
        "lat": 51.0325480691
    }
}
```

Which will be translated to the following linked data model:

```turtle
@prefix mobility: <https://example.org/ns/mobility#> .
@prefix rdf:      <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .

<https://stad.gent/nl/mobiliteit-openbare-werken/parkeren/park-and-ride-pr/pr-gentbrugge-arsenaal>
        rdf:type                  mobility:offStreetParkingGround;
        mobility:availablespaces  0;
        mobility:lastupdate       "2023-11-30T21:45:15+01:00"^^<http://www.w3.org/2001/XMLSchema#dateTime>;
        mobility:location         [ mobility:lat  5.10325480691E1;
                                    mobility:lon  3.7583663653E0
                                  ];
        mobility:name             "Parking";
        mobility:numberofspaces   0 .

```