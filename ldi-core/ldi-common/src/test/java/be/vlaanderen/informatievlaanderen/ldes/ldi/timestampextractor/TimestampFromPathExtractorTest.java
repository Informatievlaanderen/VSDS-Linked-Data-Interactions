package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimestampFromPathExtractorTest {

	public static final Property TIME_PROPERTY = ResourceFactory.createProperty("http://www.w3.org/ns/prov#generatedAtTime");
	public static final Property INVALID_TIME_PROPERTY = ResourceFactory.createProperty("not-existing-property");
	public TimestampFromPathExtractor timestampFromPathExtractor;
	private Model inputModel;
	private Property memberSubject;

	@BeforeEach
	void setUp() {
		inputModel = RDFParser.source("model-with-timestamp20220520.nq").toModel();
		memberSubject = ResourceFactory.createProperty("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1");
	}

	@Test
	void ExtractTimestamp_ShouldThrowException_WhenTimestampNotFound() {
		timestampFromPathExtractor = new TimestampFromPathExtractor(INVALID_TIME_PROPERTY);

		assertThatThrownBy(() -> timestampFromPathExtractor.extractTimestamp(inputModel))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No timestamp as literal found on member");	}

	@Test
	void ExtractTimestamp_ShouldTimestamp_WhenFound() {
		timestampFromPathExtractor = new TimestampFromPathExtractor(TIME_PROPERTY);

		LocalDateTime result = timestampFromPathExtractor.extractTimestamp(inputModel);

		assertThat(result).isEqualTo("2022-05-20T09:58:15");
	}

	@Test
	void ExtractTimestampWithSubject_ShouldThrowException_WhenTimestampNotFound() {
		timestampFromPathExtractor = new TimestampFromPathExtractor(INVALID_TIME_PROPERTY);

		assertThatThrownBy(() -> timestampFromPathExtractor.extractTimestampWithSubject(memberSubject, inputModel))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No timestamp as literal found on member");
	}

	@Test
	void ExtractTimestampWithSubject_ShouldTimestamp_WhenFound() {
		timestampFromPathExtractor = new TimestampFromPathExtractor(TIME_PROPERTY);

		LocalDateTime result = timestampFromPathExtractor.extractTimestampWithSubject(memberSubject, inputModel);

		assertThat(result).isEqualTo("2022-05-20T09:58:15");
	}

}
