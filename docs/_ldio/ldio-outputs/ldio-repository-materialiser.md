---
layout: default
parent: LDIO Outputs
title: Repository Materialiser
---

# Repository Materialiser

***Ldio:RepositoryMaterialiser***

The repository materialiser is used to materialise an LDES stream into a triplestore.
Any triplestore that supports the RDF4J remote repository API can be used.

## Config

| Property      | Description                                                                                                 | Required | Default | Example                 | Supported values |
|:--------------|:------------------------------------------------------------------------------------------------------------|:---------|:--------|:------------------------|:-----------------|
| sparql-host   | The url of the server hosting the repository                                                                | Yes      | N/A     | http://repositoryServer | URL              |
| repository-id | The rdf4j repository id                                                                                     | Yes      | N/A     | repoId                  | String           |
| named-graph   | If set, the triples will be written to this named graph                                                     | No       | N/A     | http://name             | Any valid IRI    |
| batch-size    | Number of members or models that will be committed at once                                                  | No       | 10000   | 500                     | Integer          |
| batch-timeout | If the batch size has not reached in this amount of milliseconds yet, the members will be committed anyway. | No       | 120000  | 30000                   | Integer          |

### Batching

To increase the performance of this materialiser, members will be committed in batch to the triple store. However, it's
important to notice that this can have an impact on the data integrity. First of all, there could be a delay, with a
maximum delay of the configured batch timeout, when the triple store will be up-to-date. Secondly, if something goes
wrong halfway of a batch, all the members in that batch will not be committed to triple story and thus will be gone.

So the more important data integrity is, the lower the `batch-size` and `batch-timeout` should be configured. If a more
performant repository materialiser is desired, `batch-size` and `batch-timeout` should be configured somewhat higher. 
