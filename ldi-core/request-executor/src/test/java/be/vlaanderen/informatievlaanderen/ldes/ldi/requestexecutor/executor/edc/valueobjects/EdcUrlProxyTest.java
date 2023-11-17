package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EdcUrlProxyTest {

	@Test
	void proxy() {
		EdcUrlProxy urlProxy = new EdcUrlProxy("www.to-replace.com", "www.replacement.com");

		String result = urlProxy.proxy("www.to-replace.com/by-page?pageNumber=22");

		assertEquals("www.replacement.com/by-page?pageNumber=22", result);
	}

}