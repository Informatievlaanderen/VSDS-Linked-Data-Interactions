package be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityProtocolNotSupportedExceptionTest {

	@Test
	void testExceptionMessage() {
		SecurityProtocolNotSupportedException exception = new SecurityProtocolNotSupportedException("key");
		assertEquals(
				"java.lang.IllegalArgumentException: Invalid 'key', the supported protocols are: [NO_AUTH, SASL_SSL_PLAIN]",
				exception.getMessage());
	}

}