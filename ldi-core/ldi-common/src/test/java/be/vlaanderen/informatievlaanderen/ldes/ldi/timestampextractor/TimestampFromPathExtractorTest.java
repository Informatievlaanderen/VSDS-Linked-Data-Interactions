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

	public static final Property TIME_PROPERTY = ResourceFactory.createProperty("http://www.w3.org/ns/prov#generatedAtTime");
	public TimestampFromPathExtractor timestampFromPathExtractor;
	private Model inputModel;
	private Property memberSubject;

	@BeforeEach
	void setUp() {
		inputModel = RDFParser.source("model-with-timestamp20220520.nq").toModel();
		memberSubject = ResourceFactory.createProperty("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1");
		timestampFromPathExtractor = new TimestampFromPathExtractor(TIME_PROPERTY);
	}

	@Test
	void ExtractTimestamp_ShouldThrowException_WhenTimestampNotFound() {
		assertThrows(IllegalArgumentException.class, () -> timestampFromPathExtractor.extractTimestamp(inputModel));
	}

	@Test
	void ExtractTimestamp_ShouldTimestamp_WhenFound() {
		LocalDateTime result = timestampFromPathExtractor.extractTimestamp(inputModel);

		assertEquals(LocalDateTime.of(2022, 5, 20, 9, 58, 15), result);
	}

	@Test
	void ExtractTimestampWithSubject_ShouldThrowException_WhenTimestampNotFound() {
		assertThrows(IllegalArgumentException.class, () -> timestampFromPathExtractor.extractTimestampWithSubject(memberSubject, inputModel));
	}

	@Test
	void ExtractTimestampWithSubject_ShouldTimestamp_WhenFound() {
		LocalDateTime result = timestampFromPathExtractor.extractTimestampWithSubject(memberSubject, inputModel);

		assertEquals(LocalDateTime.of(2022, 5, 20, 9, 58, 15), result);
	}

}
