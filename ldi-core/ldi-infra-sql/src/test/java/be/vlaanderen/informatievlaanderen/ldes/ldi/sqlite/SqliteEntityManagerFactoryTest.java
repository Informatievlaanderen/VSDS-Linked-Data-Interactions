package be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqliteEntityManagerFactoryTest {
	private static final SqliteProperties sqliteProperties = new SqliteProperties("instanceName", false);

	@Test
	void given_InvalidProperties_when_GetInstance_then_ThrowException() {
		assertThatThrownBy(() -> SqliteEntityManagerFactory.getInstance(Map::of))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Invalid properties for SqliteEntityManagerFactory provided");
	}

	@Test
	void when_GetInstance_then_SameSingletonInstanceIsReturned() {
		final SqliteEntityManagerFactory firstInstance = SqliteEntityManagerFactory.getInstance(sqliteProperties);
		final SqliteEntityManagerFactory secondInstance = SqliteEntityManagerFactory.getInstance(sqliteProperties);

		assertThat(firstInstance).isSameAs(secondInstance);
	}

	@Test
	void test_DestroyState() {
		final SqliteEntityManagerFactory firstInstance = SqliteEntityManagerFactory.getInstance(sqliteProperties);
		firstInstance.destroyState(sqliteProperties.getInstanceName());

		assertThat(firstInstance.getEntityManager().isOpen()).isFalse();
		assertThat(new File(sqliteProperties.getDatabaseDirectory(), sqliteProperties.getDatabaseName())).doesNotExist();

		final SqliteEntityManagerFactory secondInstance = SqliteEntityManagerFactory.getInstance(sqliteProperties);

		assertThat(firstInstance).isNotSameAs(secondInstance);
	}
}