package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthStrategyTest {

	@ParameterizedTest
	@EnumSource(AuthStrategy.class)
	void from_ShouldReturnAValueForExistingAuthStrategies(AuthStrategy authStrategy) {
		assertTrue(AuthStrategy.from(authStrategy.name()).isPresent());
	}

	@Test
	void from_ShouldReturnEmptyOptionalForNonExistingAuthStrategies() {
		assertTrue(AuthStrategy.from("nonExisting").isEmpty());
	}

}