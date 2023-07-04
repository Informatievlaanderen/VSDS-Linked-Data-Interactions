package be.vlaanderen.informatievlaanderen.ldes.ldi.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReplaceFunctionsTest {
	ReplaceFunctions replaceFunctions = new ReplaceFunctions();

	@Test
	void testReplace() {
		String text = "Parking Centre";
		String output = replaceFunctions.replaceFunction(text, "a", "e");

		assertEquals("Perking Centre", output);
	}
}
