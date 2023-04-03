package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class KafkaInAuthStrategyTest {

	@ParameterizedTest
	@EnumSource(KafkaInAuthStrategy.class)
	void shouldReturnAValueForExistingAuthStrategies(KafkaInAuthStrategy authStrategy) {
		assertTrue(KafkaInAuthStrategy.from(authStrategy.name()).isPresent());
	}

	@Test
	void shouldReturnEmptyOptionalForNonExistingAuthStrategies() {
		assertTrue(KafkaInAuthStrategy.from("nonExisting").isEmpty());
	}

}