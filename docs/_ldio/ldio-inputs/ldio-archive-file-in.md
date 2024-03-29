---
layout: default
parent: LDIO Inputs
title: Archive File In
---

# LDIO File Out

***Ldio:ArchiveFileIn***

The LDIO Archive File In is used to read models from files and feed them to the pipeline.
Please refer to the [core documentation](../../core/ldi-inputs/file-archiving) for more information.

## Config

| Property         | Description                       | Required | Default                     | Example          | Supported values                |
|:-----------------|:----------------------------------|:---------|:----------------------------|:-----------------|:--------------------------------|
| archive-root-dir | The root directory of the archive | Yes      | N/A                         | /parcels/archive | Linux (+ Mac) and Windows paths |
| source-format    | The source format of the files    | No       | Deduced from file extension | text/turtle      | Any Jena supported format       |

## Pausing

When paused, this component will stop reading from the archive.
When resumed, it will pick up where it left of, ignoring any changes to the file structure.