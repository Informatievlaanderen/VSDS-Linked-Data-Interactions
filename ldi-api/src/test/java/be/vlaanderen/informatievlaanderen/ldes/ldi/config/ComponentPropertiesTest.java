package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComponentPropertiesTest {

	@Nested
	class GetProperty {
		@Test
		void test() {
			ComponentProperties componentProperties = new ComponentProperties(
					Map.of("key", "value", "keyTwo", "valueTwo"));

			assertEquals("value", componentProperties.getProperty("key"));
			assertEquals("valueTwo", componentProperties.getProperty("keyTwo"));
			IllegalArgumentException keyThree = assertThrows(IllegalArgumentException.class,
					() -> componentProperties.getProperty("keyThree"));
			assertEquals("Missing value for key keyThree", keyThree.getMessage());
		}
	}
}