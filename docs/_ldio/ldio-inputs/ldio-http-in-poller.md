---
layout: default
parent: LDIO Inputs
title: HTTP In Poller
---

# LDIO HTTP In Poller
***be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpInPoller***

The LDIO Http In Poller is a basic Http Poller that will poll a target URL on a specified interval. 

## Config

| Property       | Description                                         | Required | Default | Example                   | Supported values          |
|:---------------|:----------------------------------------------------|:---------|:--------|:--------------------------|:--------------------------|
| url            | Target URL to poll from.                            | Yes      | N/A     | http://example.com/my-api | HTTP and HTTPS urls       |
| interval       | Polling interval declared in ISO 8601 format.       | Yes      | N/A     | PT1S                      | ISO 8601 formatted String |
| continueOnFail | Indicated if continue if polling results in failure | No       | true    | true                      | true or false             |