package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LdiAdapterContentTest {
	@Test
	void test() {
		String quadData = "_:b0 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://schema.org/Person> .";
		String mimeType = "application/ld+json";

		LdiAdapter.Content content = LdiAdapter.Content.of(quadData, mimeType);

		assertEquals(quadData, content.content());
		assertEquals(mimeType, content.mimeType());
	}
}
