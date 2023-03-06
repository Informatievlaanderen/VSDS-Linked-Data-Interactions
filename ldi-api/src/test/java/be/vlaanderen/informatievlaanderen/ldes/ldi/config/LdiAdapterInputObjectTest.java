package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LdiAdapterInputObjectTest {
	@Test
	void test() {
		String quadData = "_:b0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Person> .";
		String contentType = "application/ld+json";

		LdiAdapter.InputObject inputObject = LdiAdapter.InputObject.of(quadData, contentType);

		assertEquals(quadData, inputObject.content());
		assertEquals(contentType, inputObject.contentType());
	}
}
