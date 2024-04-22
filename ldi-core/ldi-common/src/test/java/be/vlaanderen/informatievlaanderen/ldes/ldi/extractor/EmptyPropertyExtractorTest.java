package be.vlaanderen.informatievlaanderen.ldes.ldi.extractor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EmptyPropertyExtractorTest {

	@Test
	void getObjects() {
		assertTrue(new EmptyPropertyExtractor().getProperties(null).isEmpty());
	}

}