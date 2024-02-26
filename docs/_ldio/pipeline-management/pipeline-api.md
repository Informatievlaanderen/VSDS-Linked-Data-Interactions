---
layout: default
parent: Pipeline Management
title: Pipeline Management API
---

# Pipeline Management API

> This will be moved to swagger in the future

## Listing all pipelines (`GET`)

By performing a GET request to the `/admin/api/v1/pipeline` endpoint, a list of all active pipelines is shown.

````bash
curl --location 'http://localhost:8080/admin/api/v1/pipeline'
````

Result:

````yaml
[
  {
    "name": "demo",
    "status": "RUNNING",
    "description": "",
    "input": {
      "name": "Ldio:HttpIn",
      "adapter": {
        "name": "Ldio:RdfAdapter",
        "config": { }
      },
      "config": { }
    },
    "transformers": [ ],
    "outputs": [
      {
        "name": "Ldio:ConsoleOut",
        "config": { }
      }
    ]
  }
]
````

## Creating a pipeline (`POST`)

By posting a valid pipeline to the `/admin/api/v1/pipeline` endpoint, a new pipeline can be created.

### JSON Example:

```bash
curl --location 'http://localhost:8080/admin/api/v1/pipeline' \
--header 'Content-Type: application/json' \
  --data '{
    "name": "my-first-pipeline",
    "input": {
      "name": "Ldio:HttpIn",
      "adapter": {
        "name": "Ldio:RdfAdapter"
      }
    },
    "outputs": [
      {
        "name": "Ldio:ConsoleOut"
      }
    ]
}'
```

### YAML Example:

````bash
curl --location 'http://localhost:8080/admin/api/v1/pipeline' \
--header 'Content-Type: application/yaml' \
--data 'name: my-second-pipeline
input:
  name: '\''Ldio:HttpIn'\''
  adapter:
    name: '\''Ldio:RdfAdapter'\''
outputs:
  - name: '\''Ldio:ConsoleOut'\''
'
````

## Deleting a pipeline

A deletion of a pipeline can be achieved by performing a DELETE request to the
`/admin/api/v1/pipeline/{pipelineName}` endpoint, with a `pipelineName` being the pipeline that needs to be deleted.

````bash
curl --location --request DELETE 'http://localhost:8080/admin/api/v1/pipeline/my-first-pipeline'
````