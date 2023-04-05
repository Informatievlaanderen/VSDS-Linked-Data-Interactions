package be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor;

import org.apache.jena.rdf.model.Model;

public class EmptyKafkaKeyExtractor implements KafkaKeyExtractor {

	@Override
	public String getKey(Model model) {
		return null;
	}

}
