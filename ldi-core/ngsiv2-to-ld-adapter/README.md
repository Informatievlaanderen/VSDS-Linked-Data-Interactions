# NgsiV2 to LD Adapter

To transform a NgsiV2 input into NgsiLD,
jackson is used to first deserialize the input to java objects which can then be serialized to the LD format.

## Notes

The algorithm applies several deviations from the standard formats. These deviations are:

1) The observedAt attribute is added to every property,
its value is determined by the dateObserved attribute of the input.
2) The timestamp attribute of a metadata property normally determines the observedAt property but is ignored in this algorithm.