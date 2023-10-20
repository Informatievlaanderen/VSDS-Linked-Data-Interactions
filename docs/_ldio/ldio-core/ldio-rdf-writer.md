---
layout: default
parent: LDIO Core
title: LDI RDF Writer
---

# LDI RDF Writer

To easily output RDF in the correct format, a generic RDF Writer is introduced.  
The RDF Writer supports the below config:


| Property     | Description                                                           | Required | Default             | Example                                   | Supported values                                              |
|:-------------|:----------------------------------------------------------------------|:---------|:--------------------|:------------------------------------------|:--------------------------------------------------------------|
| content-type | Target content type.                                                  | No       | application/n-quads | application/ld+json                       | Any type supported by [Apache Jena](https://jena.apache.org/) |
| frame-type   | RDF type of the objects that need to be included for JSON-LD framing. | No       | N/A                 | http://purl.org/goodrelations/v1#Offering | Any RDF type                                                  |

## Example
```yaml
      config:
        rdf-writer:
          content-type: application/n-quads
          frame-type: http://purl.org/goodrelations/v1#Offering
```