---
layout: default
parent: LDIO Inputs
title: Archive File In
---

# LDIO File Out
***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioArchiveFileIn***

The LDIO Archive File In is used to read models from files and feed them to the pipeline.
Please refer to the [core documentation](../../_core/ldi-inputs/file-archiving.md) for more information.

## Config

| Property         | Description                       | Required | Default                     | Example          | Supported values                |
|:-----------------|:----------------------------------|:---------|:----------------------------|:-----------------|:--------------------------------|
| archive-root-dir | The root directory of the archive | Yes      | N/A                         | /parcels/archive | Linux (+ Mac) and Windows paths |
| source-format    | The source format of the files    | No       | Deduced from file extension | text/turtle      | Any Jena supported format       |