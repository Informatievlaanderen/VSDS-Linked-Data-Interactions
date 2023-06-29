---
title: Introduction
layout: home
nav_order: 0
---

# Building blocks

As the LDI strives to be an easily reusable project, each of our building blocks are framework independent and is being maintained as part in our LDI Core.

Each of the LDI Core Building Blocks falls under one of four categories:
* [LDI Input](ldi-inputs): A component that will receive data (not necessarily LD) to then feed the LDI pipeline.
* [LDI Adapter](ldi-adapters): To be used in conjunction with the LDI Input, the LDI Adapter will transform the provided content into and internal Linked Data model and sends it down the pipeline.
* [LDI Transformer](ldi-transformers): A component that takes in a Linked Data model, transforms/modifies it and then puts it back on the pipeline.
* [LDI Output](ldi-outputs): A component that will take in Linked Data and will export it to external sources.

````mermaid
stateDiagram-v2
    direction LR
    [*] --> LDI_Input
    LDI_Input --> LDI_Transformer : LD
    LDI_Transformer --> LDI_Output : LD
    LDI_Output --> [*]

    state LDI_Input {
        direction LR
        [*] --> LDI_Adapter : Non LD

        state LDI_Adapter {
            direction LR
            [*] --> adapt
            adapt --> [*]
        }

        LDI_Adapter --> [*] : LD
    }
    
    state LDI_Transformer {
        direction LR
        [*] --> transform
        transform --> [*]
    }
    state LDI_Output {
        direction LR
        [*] --> [*]
    }
````