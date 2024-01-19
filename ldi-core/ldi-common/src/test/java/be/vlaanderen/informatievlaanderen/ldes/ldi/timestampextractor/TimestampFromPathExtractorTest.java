package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimestampFromPathExtractorTest {

	private Model inputModel;

	@BeforeEach
	void setUp() {
		inputModel = RDFParser.source("model-with-timestamp20220520.nq").toModel();
	}

	@Test
	void ExtractTimestamp_ShouldThrowException_WhenTimestampNotFound() {
		Property timeProperty = ResourceFactory.createProperty("not-existing-property");
		TimestampFromPathExtractor timestampFromPathExtractor = new TimestampFromPathExtractor(timeProperty);

		assertThrows(IllegalArgumentException.class, () -> timestampFromPathExtractor.extractTimestamp(inputModel));
	}

	@Test
	void ExtractTimestamp_ShouldTimestamp_WhenFound() {
		Property timeProperty = ResourceFactory.createProperty("http://www.w3.org/ns/prov#generatedAtTime");

		LocalDateTime result = new TimestampFromPathExtractor(timeProperty).extractTimestamp(inputModel);

		assertEquals(LocalDateTime.of(2022, 5, 20, 9, 58, 15), result);
	}

}
