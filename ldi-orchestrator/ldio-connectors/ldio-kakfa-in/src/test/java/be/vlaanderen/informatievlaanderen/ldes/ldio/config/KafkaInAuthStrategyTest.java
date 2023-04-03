package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class KafkaInAuthStrategyTest {

    @ParameterizedTest
    @EnumSource(KafkaInAuthStrategy.class)
    void from_ShouldReturnAValueForExistingAuthStrategies(KafkaInAuthStrategy authStrategy) {
        assertTrue(KafkaInAuthStrategy.from(authStrategy.name()).isPresent());
    }

    @Test
    void from_ShouldReturnEmptyOptionalForNonExistingAuthStrategies() {
        assertTrue(KafkaInAuthStrategy.from("nonExisting").isEmpty());
    }

}