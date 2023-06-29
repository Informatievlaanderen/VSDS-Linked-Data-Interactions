---
layout: default
parent: LDI Inputs
title: LDES Client
---

# Ldes Client

The LDES Client contains the functionality to replicate and synchornise an LDES, and to persist its state for that process. More information on the functionalites can be found [here][VSDS Tech Docs].

This is achieved by configuring the processor with an initial fragment URL. When the processor is triggered, the fragment will be processed, and all relations will be added to the (non-persisted) queue.

As long as the processor runs, a queue that accepts new fragments to process is maintained. The processor also keeps track of the mutable and immutable fragments already processed.

It will be ignored when an attempt is made to queue a known immutable fragment. Fragments in the mutable fragment store will be queued when they're expired. Should a fragment be processed from a stream that does not set the max-age in the Cache-control header, a default expiration interval will be used to set an expiration date on the fragment.

Processed members of mutable fragments are also kept in state. They are ignored if presented more than once.

[VSDS Tech Docs]: https://informatievlaanderen.github.io/VSDS-Tech-Docs/docs/LDES_client.html