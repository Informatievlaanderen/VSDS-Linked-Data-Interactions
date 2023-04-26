package config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;

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
		
		@Test
		void shouldReturnEmptyWhenFileMissing() {
			ComponentProperties componentProperties = new ComponentProperties(
					Map.of("query", "src/test/resources/query.rq"));
			
			assertEquals("sparql", componentProperties.getOptionalPropertyFromFileIfPresent("query").get());
		}
	}

}