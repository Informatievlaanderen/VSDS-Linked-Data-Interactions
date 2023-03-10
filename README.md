# VSDS Linked Data Interactions

The Linked Data Interactions Repo (LDI) is a bundle of basic SDKs used to receive, generate, transform and output Linked Data.
This project is set up in the context of the [VSDS Project](https://vlaamseoverheid.atlassian.net/wiki/spaces/VSDSSTART/overview) to ease adopting LDES on data consumer and producer side.

## LDI API

The LDI API provides a bundle of generic interfaces and classes to be used in the LDI SDKs

For further information, please refer to the JavaDoc.

## LDI Core

The LDI Core module contains the SDKs maintained by the VSDS team in order to accommodate the onboarding of LDES onboarders.

Each SDK can be wrapped in a desired implementation framework (LDI-orchestrator, NiFi, ...) to be used.

More documentation on the individual SDKs can be found [here](ldi-core/README.md)

## Implementation Modules

The VSDS team currently supports and maintains two implementation frameworks in which the SDKs can be run. 

### Apache NiFi 

Apache NiFi is a powerful data integration tool that enables organisations to manage and process their data flows in real-time.

For further details on how to use our components in NiFi, please refer to the in-NiFi documentation.

### LDI Orchestrator

As NiFi can be quite extensive for setting up a basic Linked Data transformation, we provide an alternative lightweight Spring Boot based framework.

For further details on how to use our components in the LDI Orchestrator, please refer to the [LDI Orchestrator documentation](./ldi-orchestrator/README.md)