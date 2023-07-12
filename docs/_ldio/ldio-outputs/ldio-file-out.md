---
layout: default
parent: LDIO Outputs
title: File Out
---

# LDIO File Out
***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioFileOut***

The LDIO File Out is used to write models to files based on a timestamp path property on the model.
Please refer to the [core documentation](../../_core/ldi-outputs/file-archiving.md) for more information.

## LDIO Config

| Property         | Description                                   | Required | Default | Example                                    | Supported values                |
|:-----------------|:----------------------------------------------|:---------|:--------|:-------------------------------------------|:--------------------------------|
| archive-root-dir | The root directory where files are written to | Yes      | N/A     | /parcels/archive                           | Linux (+ Mac) and Windows paths |
| timestamp-path   | The timestamp path used for naming the        | Yes      | N/A     | http://www.w3.org/ns/prov#generatedAtTime  | Any valid LD predicate          |