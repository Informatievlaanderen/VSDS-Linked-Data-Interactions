# VSDS Linked Data Transformation Orchestrator

The Linked Data Transformation Orchestrator (LDTO) is a lightweight component meant to run Linked Data Transformations.
This project is set up in the context of the [VSDS Project](https://vlaamseoverheid.atlassian.net/wiki/spaces/VSDSSTART/overview) to ease adopting LDES on data consumer and producer side.

## Contents

### LDTO API

The LDTO API provides basic tooling to set up a LDTO Input/Transformer/Output.

For further information, please refer to the JavaDoc.

### LDTO Application

The LDTO application contains a runnable Spring Boot application which, when provided with a properties file, will orchestrate the Linked Data transformation.

#### Application Properties

What follows is a template of a default orchestrator. This will need to be extended based on the Input, Output and Transformers that are used.
  ```yaml
  server.port: { desired port the optional LdtoHttpIn component will listen on }
  orchestrator:
    input:
      name: { bean name of an LdtoInput. By default, this should be the camelCased version of the class name }
      config: { config properties map }
    transformers: { chronological list of desired transformations }
    output:
      name: { bean name of an LdtoOutput. By default, this should be the camelCased version of the class name }
      config: { config properties map }
  ```

### LDTO Built In Starter Kit

Since the Orchestration on its own can perform no actions, a VSDS team supported Started Kit is provided to offer 
basic functionalities.

#### Contents

##### 1. Input

###### 1.1 LdtoHttpIn

The LdtoHttpIn serves as a basic Http Listening component which allows the user to send Linked Data as Input for a 
transformation workflow.

The port can be configured through the default spring properties:

```yaml
server.port: { desired port the LdtoHttpIn component will listen on }
```

*This component requires no further config*

##### 2. Transformers

###### 2.1 SparqlConstructTransformer

The SparqlConstructTransformer allows the user to transform its Linked Data based on a provided 
[SPARQL Construct](https://www.w3.org/TR/rdf-sparql-query/) query. 

```yaml
  config:
    query: { SPARQL Construct query to transform data }
    infer: { Flag that allows the result of the construct query to be added to the provided linked data }
  ```

##### 3. Output

###### 3.1 LdtoHttpOut

The LdtoHttpOut will perform a basic HTTP POST of the transformed Linked Data towards a provided endpoint

```yaml
  config:
    content-type: { desired content type to send to endpoint. By default, this will be set to 'application/n-quads'}
    endpoint: { Http endpoint to send transformed linked data to }
  ```

###### 3.2 LdtoConsoleOut

The LdtoConsoleOut provides a debug tool to locally run a transformation and checking its endpoint in the console.

```yaml
  config:
    content-type: { desired content type to send to endpoint. By default, this will be sent as 'application/n-quads'}
  ```