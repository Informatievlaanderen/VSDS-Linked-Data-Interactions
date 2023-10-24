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

	@Nested
	class ExtractNestedProperties {

		@Test
		void shouldReturnNestedProperties_whenFound() {
			ComponentProperties properties = new ComponentProperties(
					Map.of("adapter.name", "my-adapter",
							"adapter.config.context", "example.com",
							"adapter.config.alt", "alternative"));

			ComponentProperties nestedProperties = properties.extractNestedProperties("adapter.config");

			assertEquals(2, nestedProperties.getConfig().size());
			assertEquals("example.com", nestedProperties.getProperty("context"));
			assertEquals("alternative", nestedProperties.getProperty("alt"));
		}

		@Test
		void shouldReturnEmpty_whenNotFound() {
			ComponentProperties properties = new ComponentProperties(Map.of("foo", "bar"));

			ComponentProperties nestedProperties = properties.extractNestedProperties("adapter.config");

			assertTrue(nestedProperties.getConfig().isEmpty());
		}

		@Test
		void shouldReturnEmpty_whenThereAreNoProperties() {
			ComponentProperties properties = new ComponentProperties();

			ComponentProperties nestedProperties = properties.extractNestedProperties("adapter.config");

			assertTrue(nestedProperties.getConfig().isEmpty());
		}

		@Test
		void shouldReturnEmpty_whenKeyIsNullOrEmpty() {
			ComponentProperties properties = new ComponentProperties(Map.of("a", "b"));

			assertTrue(properties.extractNestedProperties(null).getConfig().isEmpty());
			assertTrue(properties.extractNestedProperties("").getConfig().isEmpty());
		}

		@Test
		void shouldReturnEmpty_whenKeyMatchesValueInsteadOfNestedStructure() {
			ComponentProperties properties = new ComponentProperties(Map.of("a", "b"));
			assertTrue(properties.extractNestedProperties("a").getConfig().isEmpty());
		}
	}

}