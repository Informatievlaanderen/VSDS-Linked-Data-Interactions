package be.vlaanderen.informatievlaanderen.ldes.ldi.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeFunctionsTest {
	TimeFunctions timeFunctions = new TimeFunctions();

	@Test
	void test_epocToIso() {
		String dateTime = timeFunctions.replaceFunction(1687449613L * 1000);

		assertEquals("2023-06-22T16:00:13.000Z", dateTime);
	}
}
