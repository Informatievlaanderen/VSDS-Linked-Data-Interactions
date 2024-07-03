package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ComponentPropertiesTest {
	private static final String pipelineName = "Pname";
	private static final String componentName = "Cname";

	@Nested
	class GetPropertyList {

		private static final String key = "url";


		@Test
		void ShouldReturnProperty_WhenNotAnArray() {
			ComponentProperties properties = new ComponentProperties(pipelineName, componentName, Map.of(key, "example.com"));

			assertEquals(1, properties.getPropertyList(key).size());
			assertEquals("example.com", properties.getPropertyList(key).get(0));
		}

		@Test
		void ShouldReturnAllProperties_WhenAnArray() {
			ComponentProperties properties = new ComponentProperties(pipelineName, componentName,
					Map.of("url.0", "example.com",
							"url.1", "other-example.com"));

			assertEquals(2, properties.getPropertyList(key).size());
			assertEquals("example.com", properties.getPropertyList(key).get(0));
			assertEquals("other-example.com", properties.getPropertyList(key).get(1));
		}

		@Test
		void ShouldReturnEmpty_WhenPropertyNotFound() {
			ComponentProperties properties = new ComponentProperties(pipelineName, componentName, Map.of());

			assertTrue(properties.getPropertyList(key).isEmpty());
		}
	}

	@Nested
	class ExtractNestedProperties {

		@Test
		void shouldReturnNestedProperties_whenFound() {
			ComponentProperties properties = new ComponentProperties(pipelineName, componentName,
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
			ComponentProperties properties = new ComponentProperties(pipelineName, componentName, Map.of("foo", "bar"));

			ComponentProperties nestedProperties = properties.extractNestedProperties("adapter.config");

			assertTrue(nestedProperties.getConfig().isEmpty());
		}

		@Test
		void shouldReturnEmpty_whenThereAreNoProperties() {
			ComponentProperties properties = new ComponentProperties(pipelineName, componentName);

			ComponentProperties nestedProperties = properties.extractNestedProperties("adapter.config");

			assertTrue(nestedProperties.getConfig().isEmpty());
		}

		@Test
		void shouldReturnEmpty_whenKeyIsNullOrEmpty() {
			ComponentProperties properties = new ComponentProperties(pipelineName, componentName, Map.of("a", "b"));

			assertTrue(properties.extractNestedProperties(null).getConfig().isEmpty());
			assertTrue(properties.extractNestedProperties("").getConfig().isEmpty());
		}

		@Test
		void shouldReturnEmpty_whenKeyMatchesValueInsteadOfNestedStructure() {
			ComponentProperties properties = new ComponentProperties(pipelineName, componentName, Map.of("a", "b"));
			assertTrue(properties.extractNestedProperties("a").getConfig().isEmpty());
		}
	}

	@Nested
	class GetConfig {

		Map<String, String> config = Map.of("test", "test");

		@Test
		void shouldHaveEmptyConfigWithNoArgumentConstructor() {
			ComponentProperties componentProperties = new ComponentProperties(pipelineName, componentName);

			assertTrue(componentProperties.getConfig().isEmpty());
		}

		@Test
		void shouldHaveConfigWithArgumentConstructor() {
			ComponentProperties componentProperties = new ComponentProperties(pipelineName, componentName, config);

			assertEquals(config, componentProperties.getConfig());
		}
	}

	@Nested
	class GetProperty {
		@Test
		void test() {
			ComponentProperties componentProperties = new ComponentProperties(pipelineName, componentName,
					Map.of("key", "value", "keyTwo", "valueTwo"));

			assertEquals("value", componentProperties.getProperty("key"));
			assertEquals("valueTwo", componentProperties.getProperty("keyTwo"));
			assertThrows(ConfigPropertyMissingException.class,
					() -> componentProperties.getProperty("keyThree"));
		}
	}

	@Nested
	class GetOptionalBoolean {
		ComponentProperties componentProperties = new ComponentProperties(pipelineName, componentName,
				Map.of("string", "string", "trueLowerCase", "true", "trueMixedCase", "TrUe",
						"trueUpperCase", "TRUE"));

		@Test
		void shouldReturnFalseWhenPropertyIsNotABoolean() {
			assertFalse(componentProperties.getOptionalBoolean("string").orElseThrow());
		}

		@Test
		void shouldReturnBooleanWhenPropertyIsBoolean() {
			assertTrue(componentProperties.getOptionalBoolean("trueLowerCase").orElseThrow());
			assertTrue(componentProperties.getOptionalBoolean("trueMixedCase").orElseThrow());
			assertTrue(componentProperties.getOptionalBoolean("trueUpperCase").orElseThrow());
		}

		@Test
		void shouldReturnEmptyWhenPropertyIsMissing() {
			assertTrue(componentProperties.getOptionalBoolean("notPresent").isEmpty());
		}
	}

	@Nested
	class GetOptionalInteger {
		ComponentProperties componentProperties = new ComponentProperties(pipelineName, componentName,
				Map.of("string", "string", "integer", "2"));

		@Test
		void shouldThrowExceptionWhenPropertyIsNotAnInteger() {
			assertThrows(NumberFormatException.class, () -> componentProperties.getOptionalInteger("string"));
		}

		@Test
		void shouldReturnIntegerWhenPropertyIsInteger() {
			assertEquals(2, componentProperties.getOptionalInteger("integer").orElseThrow());
		}

		@Test
		void shouldReturnEmptyWhenPropertyIsMissing() {
			assertTrue(componentProperties.getOptionalInteger("notPresent").isEmpty());
		}
	}

	@Nested
	class GetOptionalPropertyFromFile {

		ComponentProperties componentProperties;

		@Test
		void shouldReturnEmptyIfFileMissing() {
			componentProperties = new ComponentProperties(pipelineName, componentName, Map.of(
					"non-existant", "non-existant-file",
					"query", "src/test/resources/query.rq"));

			assertThat(componentProperties.getOptionalPropertyFromFile("non-existant")).isEmpty();
		}

		@Test
		void shouldThrowExceptionIfUnreadableFile() throws IOException {
			componentProperties = new ComponentProperties(pipelineName, componentName,
					Map.of("non-regular-file", Files.createTempDirectory("queryDir").toFile().getAbsolutePath()));

			assertThatThrownBy(() -> componentProperties.getOptionalPropertyFromFile("non-regular-file"))
					.isInstanceOf(IllegalArgumentException.class);
		}

		@Test
		void shouldReturnFileContentsWhenFileExistsAndIsReadable() {
			componentProperties = new ComponentProperties(pipelineName, componentName, Map.of(
					"non-existant", "non-existant-file",
					"query", "src/test/resources/query.rq"));

			assertThat(componentProperties.getOptionalPropertyFromFile("query")).contains("sparql");
		}

		@Test
		void shouldReturnEmptyIfNotFilePath() {
			componentProperties = new ComponentProperties(pipelineName, componentName,
					Map.of("query", """
							PREFIX schema: <http://schema.org/>

							CONSTRUCT {
							  ?s ?p ?o .
							  ?s schema:hasCar "car" .
							}
							WHERE {
							  ?s ?p ?o .
							}
							"""));

			assertThat(componentProperties.getOptionalPropertyFromFile("query")).isEmpty();
		}
	}

}