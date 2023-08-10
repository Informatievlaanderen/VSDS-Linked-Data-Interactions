# LDI Core

This module contains all Linked Data Interaction Components

## 1. Ldes Client

This module contains the LDES client SDK that replicates and synchronises an LDES and keeps (non-persisted) state for that process.

Wrappers can call the SDK to do the actual work of scheduling fragment fetching and extracting members.

The main goal for the LDES Client SDK is to replicate an LDES and then synchronize it.

This is achieved by configuring the processor with an initial fragment url. When the processor is triggered, the fragment will be processed and all relations will be added to the (non-persisted) queue.

As long as the processor is running, a queue is maintained that accepts new fragments to process. The processor also keeps track of the mutable and immutable fragments that have already been processed.

When an attempt is made to queue a known immutable fragment, it will be ignored. Fragments in the mutable fragment store will be queued when they're expired. Should a fragment be processed from a stream that does not set the max-age in the Cache-control header, a default expiration interval will be used to set an expiration date on the fragment.

Processed members of mutable fragments are also kept in state. They are ignored if presented more than once.

## 2. SPARQL Construct Transformer

The SparqlConstructTransformer allows the user to transform its Linked Data based on a provided
[SPARQL Construct](https://www.w3.org/TR/rdf-sparql-query/) query.

## 3. Version Object Creator

To support the creation of version objects, e.g. when transforming data in the [NGSI LD format](https://vloca-kennishub.vlaanderen.be/NGSI_(LD)) to LDES.

## 4. Version Materialiser

As a counterpart to the Version Object Creator, this processor will turn a  [versioned LDES stream](https://w3id.org/ldes/specification#version-materializations) into an unversioned LDES stream.

## 5. NgsiV2 to LD Adapter

To support the ingestion of input data in [NGSI V2 format](https://fiware-tutorials.readthedocs.io/en/stable/getting-started/),
the NgsiV2ToLd processor will take the inputted NGSI V2 data and transform it to [NGSI LD](https://vloca-kennishub.vlaanderen.be/NGSI_(LD)).
More information can be found [here](ngsiv2-to-ld-adapter/README.md).