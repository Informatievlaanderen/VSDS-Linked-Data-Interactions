package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NgsiLdURIParserTest {

	private static final String ENTITY_TYPE = "WaterQualityObserved";
	private static final String ENTITY_ID = "waterqualityobserved:Sevilla:D1";

	private static final String NON_COMPLIANT_URI = ENTITY_ID;
	private static final String COMPLIANT_URI = "urn:ngsi-ld:" + ENTITY_TYPE + ":" + ENTITY_ID;

	@Test
	void whenURIIsNotNgsiLdCompliant_thenItIsTranslatedToACompliantValue() {
		String parsed = NgsiLdURIParser.toNgsiLdUri(NON_COMPLIANT_URI, ENTITY_TYPE);
		assertEquals(COMPLIANT_URI, parsed);
	}
}
