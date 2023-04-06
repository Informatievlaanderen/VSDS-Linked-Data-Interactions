package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.auth.KafkaOutAuthStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class KafkaOutAuthStrategyTest {

	@ParameterizedTest
	@EnumSource(KafkaOutAuthStrategy.class)
	void shouldReturnAValueForExistingAuthStrategies(KafkaOutAuthStrategy authStrategy) {
		assertTrue(KafkaOutAuthStrategy.from(authStrategy.name()).isPresent());
	}

	@Test
	void shouldReturnEmptyOptionalForNonExistingAuthStrategies() {
		assertTrue(KafkaOutAuthStrategy.from("nonExisting").isEmpty());
	}

}