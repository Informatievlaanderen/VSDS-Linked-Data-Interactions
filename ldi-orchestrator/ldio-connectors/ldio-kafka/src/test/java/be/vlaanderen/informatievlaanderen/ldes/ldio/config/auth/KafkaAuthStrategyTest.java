package be.vlaanderen.informatievlaanderen.ldes.ldio.config.auth;

import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.KafkaAuthStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KafkaAuthStrategyTest {

	@ParameterizedTest
	@EnumSource(KafkaAuthStrategy.class)
	void shouldReturnAValueForExistingAuthStrategies(KafkaAuthStrategy authStrategy) {
		assertTrue(KafkaAuthStrategy.from(authStrategy.name()).isPresent());
	}

	@Test
	void shouldReturnEmptyOptionalForNonExistingAuthStrategies() {
		assertTrue(KafkaAuthStrategy.from("nonExisting").isEmpty());
	}

}