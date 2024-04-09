---
layout: default
parent: Pipeline Management
title: Pipeline Status
---
# Status

An individual ldio-pipeline can be in one of several different statuses.
These different statuses and their behaviour are dependent on the input component of the pipeline.

# Overview Of The Status Flow


<div class="mermaid" style="width: 400px; height: 130px;">
graph LR;
    INIT --> RUNNING;
    INIT --> STOPPED;
    RUNNING --> STOPPED;
    RUNNING --> HALTED;
    HALTED --> RUNNING;
    HALTED --> STOPPED;
</div>

The above diagram shows the flow between the different statuses of the pipeline.

## INIT

The startup step of the ldio pipeline.
This is the preparation step before the input component can receive data and pass it on to the rest of the pipeline.
In most components this step will take little time. The only exception for now is [the client-connector](../ldio-inputs/ldio-ldes-client-connector).
It is not possible to pause the pipeline while in this state.

## RUNNING

This status indicates that the ldio pipeline is running and that the input component is ready to or currently receiving or fetching data.
This is the only state in which the pipeline can be paused.

## HALTED

Every ldio pipeline can be paused, the exact behavior of which depends on the ldio input component used.
A more in depth explanation can be found on the pages for [the individual input components.](../ldio-inputs/index)
Currently, the HALTED status can only be reached through manually pausing the pipeline through [the pipeline-api](./pipeline-api).

## STOPPED

When a pipeline is deleted, it will first change to the STOPPED status, this ensures the state is correctly saved in stateful components and that the entire pipeline can be gracefully shutdown.
This status will only last a short while before the pipeline is deleted and the status can no longer be queried.
The pipeline can be stopped from any other status.

## Status Change Source

The ldio pipeline keeps track of if the last status change was triggered manually or automatic.
Manually changing the status can be done through [the pipeline-api](./pipeline-api).
When in the RUNNING state, this indicates if the pipeline has been started (automatic) or unpaused (manual).