package be.vlaanderen.informatievlaanderen.ldes.ldio.events;

import org.apache.jena.rdf.model.Model;

public record PipelineDataOutputEvent(String targetComponent, Model data) {
}
