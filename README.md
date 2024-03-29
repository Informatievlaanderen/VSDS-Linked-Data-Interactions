# VSDS Linked Data Interactions

The Linked Data Interactions Repo (LDI) is a bundle of basic components used to receive, generate, transform and output Linked Data.
This project is set up in the context of the [VSDS Project](https://vlaamseoverheid.atlassian.net/wiki/spaces/VSDSSTART/overview) to ease adopting LDES on data consumer and producer side.

## LDI API

The LDI API provides a bundle of generic interfaces and classes to be used in the LDI Components

For further information, please refer to the JavaDoc.

## LDI Core

The LDI Core module contains the components maintained by the VSDS team in order to accommodate the onboarding of LDES onboarders.

Each Component can be wrapped in a desired implementation framework (LDI-orchestrator, NiFi, ...) to be used.

More documentation on the individual component can be found [here](ldi-core/README.md)

## Implementation Modules

The VSDS team currently supports and maintains two implementation frameworks in which the Components can be run. 

What follows is a list of all available components for each framework.

| Component                   	| Component Description                                                	| LDI Orchestrator 	 | LDI NiFi 	|
|-----------------------------	|----------------------------------------------------------------------	|--------------------|----------	|
| Create Version Object       	| Will transform a state object into a version object                  	| x                	 | x        	|
| Version Materialisation     	| Will transform a version object into a state object                  	| x                	 | x        	|
| Geo Json to WKT             	| Will transform any available Geo JSON data to WKT format             	| x                	 | x        	|
| LDES Client                 	| Allows replicating and synchronizing with a Linked Data Event Stream 	| x                	 | x        	|
| NGSI v2 to LD               	| Allows transformation of NGSIv2 JSON data into NGSI-LD               	| x                	 | x        	|
| SPARQL Construct            	| Allows transformation of Linked Data via provided SPARQL query       	| x                	 | x        	|
| SPARQL SELECT               	| Allows querying of Linked Data via SPARQL query                      	| 	                  | x        	|
| RDF4J Repo Materialisation  	| Allows materialisation of an LDES stream into a triplestore          	| x               	  | x        	|

### Apache NiFi 

Apache NiFi is a powerful data integration tool that enables organisations to manage and process their data flows in real-time.

For further details on how to use our components in NiFi, please refer to the in-NiFi documentation.

### LDI Orchestrator

As NiFi can be quite extensive for setting up a basic Linked Data transformation, we provide an alternative lightweight Spring Boot based framework.

For further details on how to use our components in the LDI Orchestrator, please refer to the [LDI Orchestrator documentation](./ldi-orchestrator/README.md)