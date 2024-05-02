package be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TimestampFromCurrentTimeExtractorTest {

	@Test
	void test_ExtractTimestamp() {
		Model model = ModelFactory.createDefaultModel();

		LocalDateTime result = new TimestampFromCurrentTimeExtractor().extractTimestamp(model);

		assertThat(result)
				.isNotNull()
				.as("A generous 5 minutes to run the test is given")
				.isBetween(LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusMinutes(5));
	}

	@Test
	void test_ExtractTimestampWithSubject() {
		Model model = ModelFactory.createDefaultModel();

		LocalDateTime result = new TimestampFromCurrentTimeExtractor().extractTimestampWithSubject(null, model);

		assertThat(result)
				.isNotNull()
				.as("A generous 5 minutes to run the test is given")
				.isBetween(LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusMinutes(5));
	}
}
