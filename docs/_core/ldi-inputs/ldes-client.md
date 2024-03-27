---
layout: default
parent: LDI Inputs
title: LDES Client
---

# Ldes Client

The LDES Client contains the functionality to replicate and synchronise an LDES, and to persist its state for that process. More information on the functionalities can be found [here][VSDS Tech Docs].

This is achieved by configuring the processor with an initial fragment URL. When the processor is triggered, the fragment will be processed, and all relations will be added to the (non-persisted) queue.

As long as the processor runs, a queue that accepts new fragments to process is maintained. The processor also keeps track of the mutable and immutable fragments already processed.

It will be ignored when an attempt is made to queue a known immutable fragment. Fragments in the mutable fragment store will be queued when they're expired. Should a fragment be processed from a stream that does not set the max-age in the Cache-control header, a default expiration interval will be used to set an expiration date on the fragment.

Processed members of mutable fragments are also kept in state. They are ignored if presented more than once.

Within a fragment, members can be ordered based on a timestamp. The path to this timestamp has to be configured. If this path is missing, the members are ordered randomly.

To allow the possibility to filter out already received members, the exactly-once-filter can be enabled.

This causes the ids of all processed members to be kept in state. They are ignored if presented more than once.

Enabling this filter may have a significant impact on performance.

[VSDS Tech Docs]: https://informatievlaanderen.github.io/VSDS-Tech-Docs/introduction/LDES_client

The client has different state persistence strategies:

## MEMORY
In this case we persist the state in memory.

| Advantages         | Disadvantages                                                |
|--------------------|--------------------------------------------------------------|
| Fastest processing | Not suitable for large datasets (500k +), heap will overflow |
| Easiest setup      | State is lost when the client stops/restarts                 |

## SQLITE
In this case an in memory SQLITE database is used to store the state.

| Advantages                     | Disadvantages       |
|--------------------------------|---------------------|
| Easy setup                     | Slowest processing* |
| State is not lost between runs |                     |

* We use a transaction for every processed record. [SQLITE is limited by the cpu](https://www.sqlite.org/faq.html#q19)

## PostgreSQL
In this case a PostreSQL database is used

| Advantages                             | Disadvantages      |
|----------------------------------------|--------------------|
| State is not lost between runs         | Database is needed |
| Fastest processing for larger datasets |                    |

