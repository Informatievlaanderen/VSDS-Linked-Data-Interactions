---
layout: default
parent: Examples
title: Basic Http In to Console
nav_order: 1
---

# Basic Http In to Console

## Used Components

- [Http In](../ldio-inputs/ldio-http-in)
- [Console Out](../ldio-outputs/ldio-console-out)

## Setup 

For this setup, we will start with a Http Listener who will take in data and write it back out via the console

***ldio.config.yaml:***
````yaml
orchestrator:
  pipelines:
    - name: data
      description: "This pipeline uses a HTTP listener to read incoming RDF data and writes them to the console"
      input:
        name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn
        adapter:
          name: be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter
      outputs:
        - name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut
````

## Execution

We can now post the following data to `http://{hostname}:{port}/data` whilst including the header `Content-Type: application/n-quads` :
````json
{
  "@context": "http://schema.org/",
  "@type": "Person",
  "name": "Jane Doe",
  "jobTitle": "Professor",
  "telephone": "(425) 123-4567",
  "url": "http://www.janedoe.com"
}
````

If done successfully, you will see in the console the converted model which defaults to application/n-quads: 

````text
_:b0 <http://schema.org/jobTitle> "Professor" .
_:b0 <http://schema.org/name> "Jane Doe" .
_:b0 <http://schema.org/telephone> "(425) 123-4567" .
_:b0 <http://schema.org/url> <http://www.janedoe.com> .
_:b0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Person> .
````