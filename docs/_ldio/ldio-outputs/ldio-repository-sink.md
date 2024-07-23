---
layout: default
parent: LDIO Outputs
title: Repository Sink
---

# Repository Sink

***Ldio:RepositorySink***

The repository Sink is used to flush an LDES stream into a triplestore.
Any triplestore that supports the RDF4J remote repository API can be used.

## Config

| Property        | Description                                                                                                      | Required | Default | Example                 | Supported values |
|:----------------|:-----------------------------------------------------------------------------------------------------------------|:---------|:--------|:------------------------|:-----------------|
| _sparql-host_   | The url of the server hosting the repository                                                                     | Yes      | N/A     | http://repositoryServer | URL              |
| _repository-id_ | The rdf4j repository id                                                                                          | Yes      | N/A     | repoId                  | String           |
| _named-graph_   | If set, the triples will be written to this named graph                                                          | No       | N/A     | http://name             | Any valid IRI    |
| _batch-size_    | Number of members or models that will be committed at once                                                       | No       | 500     | 500                     | Integer          |
| _batch-timeout_ | If the batch size has not been reached within this number of milliseconds, the members will be committed anyway. | No       | 120000  | 30000                   | Integer          |

### Batching

To increase the performance of this materialiser, members will be committed in batch to the triple store. However, it's
important to notice that this can have an impact on the data integrity. First of all, there could be a delay, with a
maximum delay of the configured batch timeout, when the triple store will be up-to-date. Secondly, if something goes
wrong halfway of a batch, all the members in that batch will not be committed to triple story and thus will be gone.

So the more important data integrity is, the lower the `batch-size` and `batch-timeout` should be configured. If a more
performant repository materialiser is desired, `batch-size` and `batch-timeout` should be configured somewhat higher. 
