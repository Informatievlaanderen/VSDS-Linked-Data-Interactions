package ldes.client.treenodesupplier.domain.valueobject;

import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StatePersistenceStrategyTest {

	@ParameterizedTest
	@EnumSource(StatePersistenceStrategy.class)
	void from_ShouldReturnAValueForExistingAuthStrategies(StatePersistenceStrategy authStrategy) {
		assertTrue(StatePersistenceStrategy.from(authStrategy.name()).isPresent());
	}

	@Test
	void from_ShouldReturnEmptyOptionalForNonExistingAuthStrategies() {
		assertTrue(StatePersistenceStrategy.from("nonExisting").isEmpty());
	}

}
