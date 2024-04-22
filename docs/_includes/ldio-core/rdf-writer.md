### Additional RDF Writer Properties

| Property                | Description                                                      | Required | Default     | Supported values                                                                                                        | Example                                                           |
|:------------------------|:-----------------------------------------------------------------|:---------|:------------|:------------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------|
| rdf-writer.content-type | Target content type.                                             | No       | text/turtle | Any type supported by [Apache Jena](https://jena.apache.org/documentation/io/rdf-input.html#determining-the-rdf-syntax) | application/ld+json                                               |
| rdf-writer.frame        | Additional JSON-LD Frame to format the outputted JSON-LD Object. | No       | N/A         | Any valid JSON Object that describes a JSON-LD Frame                                                                    | See https://www.w3.org/TR/json-ld11-framing/#sample-library-frame |

### RDF Writer Example

Format as N-Quads:

```yaml
      config:
        rdf-writer:
          content-type: application/n-quads
```

Format as JSON-LD with given frame:

```yaml
      config:
        rdf-writer:
          content-type: application/ld+json
          frame: |
            {
              "@context": {"@vocab": "http://example.org/"},
              "@type": "Library",
              "contains": {
                "@type": "Book",
                "contains": {
                  "@type": "Chapter"
                }
              }
            }
```