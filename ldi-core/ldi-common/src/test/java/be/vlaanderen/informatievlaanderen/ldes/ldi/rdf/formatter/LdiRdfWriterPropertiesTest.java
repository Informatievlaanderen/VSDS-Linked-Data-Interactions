package be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter;

import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.*;
import static org.junit.jupiter.api.Assertions.*;

class LdiRdfWriterPropertiesTest {

	private final String FRAME_TYPE_PERSON = "http://schema.org/Person";

	private final Map<String, String> properties = Map.of(CONTENT_TYPE, "application/ld+json",
			FRAME, FRAME_TYPE_PERSON);

	@Test
	void validateGetLang() {
		assertEquals(Lang.JSONLD, new LdiRdfWriterProperties(properties).getLang());
	}

	@Test
	void validateGetFrame() {
		assertEquals(FRAME_TYPE_PERSON, new LdiRdfWriterProperties(properties).getJsonLdFrame());
	}

}