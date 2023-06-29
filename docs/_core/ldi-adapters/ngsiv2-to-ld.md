---
layout: default
parent: LDI Adapters
title: NGSI V2 to LD Adapter
---

# NGSI v2 to LD Adapter

This adapter will transform a [NGSI V2] input into [NGSI LD].

[Jackson] is used to first deserialize the input to java objects which can then be serialized to the LD format.

## Notes

The algorithm applies several deviations from the standard formats. These deviations are:

1. The observedAt attribute is added to every property,
   its value is determined by the dateObserved attribute of the input.
2. The timestamp attribute of a metadata property normally determines the observedAt property but is ignored in this algorithm.

[NGSI V2]: https://fiware.github.io/specifications/ngsiv2/stable/
[NGSI LD]: https://ngsi-ld-tutorials.readthedocs.io/en/latest/
[Jackson]: https://github.com/FasterXML/jackson