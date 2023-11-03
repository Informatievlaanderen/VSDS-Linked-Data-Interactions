---
layout: default
parent: LDIO Transformers
title: SPARQL Construct
---

# LDIO SPARQL Construct
***be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer***

An LDIO wrapper component for the [LDI SPARQL Construct building block](../../core/ldi-transformers/sparql-construct)

## SPARQL functions

We support some additional geo functions that can call inside your SPARQL Construct query,

with the following namespace: 

prefix geoc: <https://opengis.net/def/function/geosparql/custom#>




| Function               | Description                                                 | Input                                                  | Output                 |
|:-----------------------|:------------------------------------------------------------|:-------------------------------------------------------|:-----------------------|
| geoc:lineAtIndex       | get LineString from MultiLineString by index.               | MultiLineString(wktLiteral) & index                    | LineString(wktLiteral) |
| geoc:firstCoordinate   | get first Coordinate of LineString.                         | LineString(wktLiteral)                                 | Coordinate(wktLiteral) |
| geoc:lastCoordinate    | get last Coordinate of LineString.                          | LineString(wktLiteral)                                 | Coordinate(wktLiteral) |
| geoc:lineLength        | calculate total line length of LineString.                  | LineString(wktLiteral)                                 | distance in meters     |
| geoc:midPoint          | calculate midpoint of LineString.                           | LineString(wktLiteral)                                 | Coordinate(wktLiteral) |
| geoc:pointAtFromStart  | calculate point on LineString by distance.                  | LineString(wktLiteral) & distance in meters            | Coordinate(wktLiteral) |
| geoc:distanceFromStart | calculate distance from start of LineString to given point. | LineString(wktLiteral) & Coordinate(wktLiteral)        | distance in meters     |