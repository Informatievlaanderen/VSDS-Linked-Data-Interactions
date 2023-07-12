---
layout: default
parent: LDI Outputs
title: File archiving
---

# Archive File Out

The Archive File Out is used to write models to files based on a timestamp path property on the model.
Every file is written to NQuads with the extracted timestamp as name: 2023-11-21-05-05-00-000000000.nq
When two files have the same name, a sequence nr is added, for example: 2023-11-21-05-05-00-000000000-2.nq

The files are ordered in directories based on the date. For every day, there is one directory.
For example: 2023-11-21-05-05-00-000000000.nq will be located at  archive-root-dir/2023/11/21.

## Config

| Property         | Description                                   | Required | Default | Example                                    | Supported values                |
|:-----------------|:----------------------------------------------|:---------|:--------|:-------------------------------------------|:--------------------------------|
| archive-root-dir | The root directory where files are written to | Yes      | N/A     | /parcels/archive                           | Linux (+ Mac) and Windows paths |
| timestamp-path   | The timestamp path used for naming the        | Yes      | N/A     | http://www.w3.org/ns/prov#generatedAtTime  | Any valid LD predicate          |

## Example
A complete demo of the archiving functionality with both LDIO and NiFi can be found in the [E2E repo](https://github.com/Informatievlaanderen/VSDS-LDES-E2E-testing/tree/main/tests/033.archiving)

{: .note }
Only LD streams that contain a timestamp-path can be archived with these components