package be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor;

import org.apache.jena.rdf.model.Model;

public interface KafkaKeyExtractor {

	String getKey(Model model);

}
