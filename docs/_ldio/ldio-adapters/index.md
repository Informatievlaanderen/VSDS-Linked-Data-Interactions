---
layout: default
title: LDIO Adapters
has_children: true
has_toc: true
nav_order: 4
---

# Linked Data Orchestrator Adapters

Adapters are be used in conjunction with the LDI Input. They will transform the provided content into and internal Linked Data model and sends it down the pipeline for further processing.

## Overview

| Adapter             | Description                                                                              | Inputs                                           | Advantages                                                                           | Disadvantages                                                                                          |
| :------------------ | :--------------------------------------------------------------------------------------- | :----------------------------------------------- | :----------------------------------------------------------------------------------- | :----------------------------------------------------------------------------------------------------- |
| _JSON to JSON-LD_   | Receives JSON messages and adds linked data context to transform the messages to JSON-LD | <ul><li>JSON</li></ul>                           | <ul><li>Easy to set up: plug in context</li></ul>                                    | <ul><li>Only works with JSON as input</li> <li>Slower performance when deserializing model</li></ul>   |
| _RDF_               | Takes in an RDF string and converts it into an internal linked data model                | <ul><li>RDF string</li></ul>                     | <ul><li>Easy to set up: no configuration needed</li></ul>                            | <ul><li>Only works with RDF as input</li> <li>Only supports valid RDF MIME types</li></ul>             |
| _RML_               | Transform a non-linked data object (JSON/CSV/XML) to RDF object                          | <ul><li>JSON</li> <li>CSV</li> <li>XML</li></ul> | <ul><li>Most powerful adapter</li> <li>Can convert multiple input objects </li></ul> | <ul><li>RML knowledge needed to do mapping</li></ul>                                                   |
| _NGSIv2 to NGSI-LD_ | Converts NGSIv2 to an NGSI-LD model                                                      | <ul><li>NGSIv2 (JSON)</li></ul>                  |                                                                                      | <ul><li>Only works with NGSIv2 as input</li> <li>Slower performance when deserializing model</li></ul> |
