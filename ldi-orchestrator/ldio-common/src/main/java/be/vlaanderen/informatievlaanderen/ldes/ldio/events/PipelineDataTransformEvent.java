package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdiOrder;
import org.apache.jena.rdf.model.Model;

public record PipelineDataTransformEvent(String targetComponent, LdiOrder ldiOrder, Model data) {
}
