package be.vlaanderen.informatievlaanderen.ldes.ldi.postgres;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresEntityManagerFactoryTest {
	public static final String INSTANCE_NAME = "instance";
	private static PostgreSQLContainer<?> postgreSQLContainer;
	private static PostgresProperties postgresProperties;

	@BeforeAll
	static void beforeAll() {
		postgreSQLContainer = new PostgreSQLContainer<>("postgres:11.1")
				.withDatabaseName("integration-change-detection-filter-persistence")
				.withUsername("sa")
				.withPassword("sa");
		postgreSQLContainer.start();

		postgresProperties = new PostgresProperties(postgreSQLContainer.getJdbcUrl(), postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword(), false);
	}

	@AfterAll
	static void afterAll() {
		postgreSQLContainer.stop();
	}

	@AfterEach
	void tearDown() {
		PostgresEntityManagerFactory.getInstance(INSTANCE_NAME, postgresProperties.getProperties()).destroyState(INSTANCE_NAME);
	}

	@Test
	void when_GetInstance_then_SameSingletonInstanceIsReturned() {
		final PostgresEntityManagerFactory firstInstance = PostgresEntityManagerFactory.getInstance(INSTANCE_NAME, postgresProperties.getProperties());
		final PostgresEntityManagerFactory secondInstance = PostgresEntityManagerFactory.getInstance(INSTANCE_NAME, postgresProperties.getProperties());

		assertThat(firstInstance).isSameAs(secondInstance);
	}

	@Test
	void test_DestroyState() {
		final PostgresEntityManagerFactory firstInstance = PostgresEntityManagerFactory.getInstance(INSTANCE_NAME, postgresProperties.getProperties());
		firstInstance.destroyState(INSTANCE_NAME);

		assertThat(firstInstance.getEntityManager().isOpen()).isFalse();

		final PostgresEntityManagerFactory secondInstance = PostgresEntityManagerFactory.getInstance(INSTANCE_NAME, postgresProperties.getProperties());

		assertThat(firstInstance).isNotSameAs(secondInstance);
	}
}