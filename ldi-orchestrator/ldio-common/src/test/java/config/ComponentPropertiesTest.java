package config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

class ComponentPropertiesTest {
	
	@Nested
	class GetConfig {
		
		Map<String, String> config = Map.of("test", "test");
		
		@Test
		void shouldHaveEmptyConfigWithNoArgumentConstructor() {
			ComponentProperties componentProperties = new ComponentProperties();
			
			assertTrue(componentProperties.getConfig().isEmpty());
		}
		
		@Test
		void shouldHaveConfigWithArgumentConstructor() {
			ComponentProperties componentProperties = new ComponentProperties(config);
			
			assertEquals(config, componentProperties.getConfig());
		}
	}

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

	@Nested
	class GetOptionalBoolean {
		ComponentProperties componentProperties = new ComponentProperties(
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
		ComponentProperties componentProperties = new ComponentProperties(
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

		ComponentProperties componentProperties = new ComponentProperties(
				Map.of(
						"non-existant", "non-existant-file",
						"query", "src/test/resources/query.rq"));

		@Test
		void shouldReturnEmptyIfFileMissing() {
			assertTrue(componentProperties.getOptionalPropertyFromFile("non-existant").isEmpty());
		}

		@Test
		void shouldThrowExceptionIfUnreadableFile() throws IOException {
			ComponentProperties componentProperties = new ComponentProperties(
					Map.of("non-regular-file", Files.createTempDirectory("queryDir").toFile().getAbsolutePath()));

			assertThrows(IllegalArgumentException.class,
					() -> componentProperties.getOptionalPropertyFromFile("non-regular-file"));
		}

		@Test
		void shouldReturnFileContentsWhenFileExistsAndIsReadable() {
			assertEquals("sparql", componentProperties.getOptionalPropertyFromFile("query").get());
		}
	}
}
