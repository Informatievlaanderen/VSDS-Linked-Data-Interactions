package be.vlaanderen.informatievlaanderen.ldes.ldio;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimestampFromCurrentTimeExtractorTest {

	@Test
	void extractTimestamp() {
		Model model = ModelFactory.createDefaultModel();

		LocalDateTime result = new TimestampFromCurrentTimeExtractor().extractTimestamp(model);

		assertNotNull(result);
		assertTrue(result.isBefore(LocalDateTime.now()));
		// We give a generous 5 minutes to run the test.
		assertTrue(result.isAfter(LocalDateTime.now().minusMinutes(5)));
	}

}