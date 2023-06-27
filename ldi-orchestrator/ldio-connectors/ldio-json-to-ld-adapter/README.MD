# JSON-to-ld-adapter

The json-to-ld-adapter receives json messages and adds a linked data context to transform the messages to json-ld.

## Example config
 
  ```yaml
  name: "be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter"
  config:
    corecontext: { URI of a core json-ld context }
    ldcontext: { URI of a custom json-ld context, optional }
  ```

## Expected input

The adapter expects a message with exactly 1 json object and the mimetype application/json.
