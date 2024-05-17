package be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StatePersistenceStrategyTest {
	@ParameterizedTest
	@ValueSource(strings = {"memory", "MEMORY", "Memory", "meMOrY"})
	void test_convertStringToMEMORY(String input) {
		final Optional<StatePersistenceStrategy> result = StatePersistenceStrategy.from(input);

		assertThat(result).contains(StatePersistenceStrategy.MEMORY);
	}

	@ParameterizedTest
	@ValueSource(strings = {"sqlite", "SQLITE", "Sqlite", "SQLite", "sQlITe"})
	void test_convertStringToSQLITE(String input) {
		final Optional<StatePersistenceStrategy> result = StatePersistenceStrategy.from(input);

		assertThat(result).contains(StatePersistenceStrategy.SQLITE);
	}

	@ParameterizedTest
	@ValueSource(strings = {"postgres", "POSTGRES", "Postgres", "PostGres", "PoSTgRes"})
	void test_convertStringToPOSTGRES(String input) {
		final Optional<StatePersistenceStrategy> result = StatePersistenceStrategy.from(input);

		assertThat(result).contains(StatePersistenceStrategy.POSTGRES);
	}

	@ParameterizedTest
	@ValueSource(strings = {"PostgreSQL", "sqlight", "In-Memory"})
	void test_invalidConversion(String input) {
		final Optional<StatePersistenceStrategy> result = StatePersistenceStrategy.from(input);

		assertThat(result).isEmpty();
	}

}