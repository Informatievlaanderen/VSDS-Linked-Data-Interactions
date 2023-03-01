# Linked Data Interactions Orchestrator

To provide and alternative for extensive data orchestration frameworks, we provide a lightweight Spring Boot based framework for basic use of our Linked Data Interactions SDKs.

## Basic configuration

The Linked Data Interactions Orchestrator can be easily configured with a yaml configuration.

What follows is a template of a default orchestrator. This will need to be extended based on the Input, Output and Transformers that are used.
  ```yaml
  orchestrator:
    input:
      name: { Class name of an LdtoInput }
      config: { mapped config properties }
    transformers:
      { chronological list of LdtoTransformers }
      - name: { Class name of an LdtoTransformer }
        config: { mapped config properties }
    outputs:
      { list of LdtoOutputs which will be ran in parallel }
      - name: { Class name of an LdtoOutput }
        config: { config properties map }
  ```

## LDIO Built-in Components

### 1. Input

#### 1.1 LdioHttpIn (be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn)

The LdioHttpIn serves as a basic Http Listening component which allows the user to send Linked Data as Input for a
transformation workflow.

To configure this processor, the following config can be added:

```yaml
  name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn
```

The port can be configured through the default spring property:

```yaml
  server.port: { desired port the LdtoHttpIn component will listen on }
```

### 2. Output

#### 2.1 LdioHttpOut (be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut)

The LdioHttpOut will perform a basic HTTP POST of the transformed Linked Data towards a provided endpoint

To configure this processor, the following config can be added:

```yaml
  name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut
  config:
    content-type: { desired content type to send to endpoint. By default, this will be set to 'application/n-quads'}
    endpoint: { Http endpoint to send transformed linked data to }
  ```

#### 2.2 LdioConsoleOut (be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut)

The LdioConsoleOut provides a debug tool to locally run a transformation and checking its endpoint in the console.

To configure this processor, the following config can be added:

```yaml
  name: be.vlaanderen.informatievlaanderen.ldes.ldio.LdioConsoleOut
  config:
    content-type: { desired content type to send to endpoint. By default, this will be sent as 'application/n-quads'}
```

## LDI Wrappers

Each LDI SDK will be wrapped as a LDIO component. For further details on the functionality of each SDK, please refer to the [main LDI readme](../README.md).

### 1. Transformers

#### 1.1 SparqlConstructTransformer (be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer)

To configure this processor, the following config can be added:

```yaml
  name: be.vlaanderen.informatievlaanderen.ldes.ldi.SparqlConstructTransformer
  config:
    query: { SPARQL Construct query to transform data }
    infer: { Flag that allows the result of the construct query to be added to the provided linked data }
```