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

| Adapter             | Description                                                                              | Advantages                                                     | Disadvantages                                                                   |
|:--------------------|:-----------------------------------------------------------------------------------------|:---------------------------------------------------------------|:--------------------------------------------------------------------------------|
| _JSON to JSON-LD_   | Receives JSON messages and adds linked data context to transform the messages to JSON-LD | Easy to set up: plug in context | Only works with JSON as input </br>  Slower performance when deserializing model |
| _NGSIv2 to NGSI-LD_ | Converts NGSIv2 to an NGSI-LD model                                                      |                                                                | Only works with NGSIv2 as input </br>  Slower performance when deserializing model |
| _RDF_               | Takes in an RDF string and converts it into an internal linked data model                | Easy to set up: no configuration needed        | Only works with RDF as input </br>  Only supports valid RDF MIME types                                    |
| _RML_               | Transform a non-linked data object (JSON/CSV/XML) to RDF object                          | Most powerful adapter </br> Can convert multiple input objects       | RML knowledge needed to do mapping       |
