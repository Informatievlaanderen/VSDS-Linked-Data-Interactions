package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoTypeTest {

	@ParameterizedTest
	@EnumSource(GeoType.class)
	void from_GeoJsonUri_ShouldReturnAValueForExistingAuthStrategies(GeoType authStrategy) {
		assertTrue(GeoType.fromUri(authStrategy.geoJsonUri).isPresent());
	}

	@ParameterizedTest
	@EnumSource(GeoType.class)
	void from_SimpleFeaturesUri_ShouldReturnAValueForExistingAuthStrategies(GeoType authStrategy) {
		assertTrue(GeoType.fromUri(authStrategy.simpleFeaturesUri).isPresent());
	}

	@Test
	void from_ShouldReturnEmptyOptionalForNonExistingAuthStrategies() {
		assertTrue(GeoType.fromUri("nonExisting").isEmpty());
	}

}