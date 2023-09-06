package be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComponentPropertiesTest {

	@Nested
	class GetPropertyList {

		private static final String key = "url";

		@Test
		void ShouldReturnProperty_WhenNotAnArray() {
			ComponentProperties properties = new ComponentProperties(Map.of(key, "example.com"));

			assertEquals(1, properties.getPropertyList(key).size());
			assertEquals("example.com", properties.getPropertyList(key).get(0));
		}

		@Test
		void ShouldReturnAllProperties_WhenAnArray() {
			ComponentProperties properties = new ComponentProperties(
					Map.of("url.0", "example.com",
							"url.1", "other-example.com"));

			assertEquals(2, properties.getPropertyList(key).size());
			assertEquals("example.com", properties.getPropertyList(key).get(0));
			assertEquals("other-example.com", properties.getPropertyList(key).get(1));
		}

		@Test
		void ShouldReturnEmpty_WhenPropertyNotFound() {
			ComponentProperties properties = new ComponentProperties(Map.of());

			assertTrue(properties.getPropertyList(key).isEmpty());
		}
	}

}