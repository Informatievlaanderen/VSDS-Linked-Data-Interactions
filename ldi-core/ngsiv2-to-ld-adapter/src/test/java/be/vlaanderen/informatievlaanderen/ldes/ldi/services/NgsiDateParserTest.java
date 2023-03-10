package be.vlaanderen.informatievlaanderen.ldes.ldi.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NgsiDateParserTest {

	private String correct = "2022-09-09T09:10:00.000Z";
	private String incomplete = "2022-09-09T09:10:00.000";

	@Test
	void whenDateIsCorrect_thenItIsPassedUnchanged() {
		assertEquals(correct, NgsiLdDateParser.normaliseDate(correct));
	}

	@Test
	void whenDateNeedsParsing_thenItIsParsed() {
		assertEquals(correct, NgsiLdDateParser.normaliseDate(incomplete));
	}
}
