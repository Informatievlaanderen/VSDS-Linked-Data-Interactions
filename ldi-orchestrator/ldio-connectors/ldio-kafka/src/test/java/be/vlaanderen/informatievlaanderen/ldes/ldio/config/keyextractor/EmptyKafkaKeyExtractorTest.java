package be.vlaanderen.informatievlaanderen.ldes.ldio.config.keyextractor;

import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.EmptyKafkaKeyExtractor;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class EmptyKafkaKeyExtractorTest {

	@Test
	void getKeyShouldReturnNull() {
		assertNull(new EmptyKafkaKeyExtractor().getKey(null));
		assertNull(new EmptyKafkaKeyExtractor().getKey(ModelFactory.createDefaultModel()));
	}

}