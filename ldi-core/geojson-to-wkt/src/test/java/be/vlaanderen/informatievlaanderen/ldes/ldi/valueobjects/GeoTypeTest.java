package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoTypeTest {

	@ParameterizedTest
	@EnumSource(GeoType.class)
	void from_ShouldReturnAValueForExistingAuthStrategies(GeoType authStrategy) {
		assertTrue(GeoType.fromUri(authStrategy.uri).isPresent());
	}

	@Test
	void from_ShouldReturnEmptyOptionalForNonExistingAuthStrategies() {
		assertTrue(GeoType.fromUri("nonExisting").isEmpty());
	}

}