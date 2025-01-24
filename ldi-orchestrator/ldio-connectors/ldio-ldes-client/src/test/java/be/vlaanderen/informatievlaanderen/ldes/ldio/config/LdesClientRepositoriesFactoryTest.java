package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.exception.ConfigPropertyMissingException;
import ldes.client.treenodesupplier.domain.valueobject.LdesClientRepositories;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryTreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberRepository;
import ldes.client.treenodesupplier.repository.sql.SqlTreeNodeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.persistence.PersistenceProperties.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LdesClientRepositoriesFactoryTest {
	private static PostgreSQLContainer postgreSQLContainer;

	@BeforeAll
	static void beforeAll() {
		postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
				.withDatabaseName("integration-test-client-persistence")
				.withUsername("sa")
				.withPassword("sa");
		postgreSQLContainer.start();
	}

	@AfterAll
	static void afterAll() {
		postgreSQLContainer.stop();
	}

	@ParameterizedTest
	@ArgumentsSource(ComponentPropertiesArgumentsProvider.class)
	void when_stateIsAllowedValue_then_StatePersistenceIsCreated(ComponentProperties componentProperties,
	                                                             Class<MemberRepository> expectedMemberRepositoryClass,
	                                                             Class<TreeNodeRecordRepository> expectedTreeNodeRecordRepositoryClass) {
		LdesClientRepositories ldesClientRepositories = LdesClientRepositoriesFactory.getLdesClientRepositories(componentProperties);

		MemberRepository memberRepository = ldesClientRepositories.memberRepository();
		assertEquals(expectedMemberRepositoryClass, memberRepository.getClass());
		TreeNodeRecordRepository treeNodeRecordRepository = ldesClientRepositories.treeNodeRecordRepository();
		assertEquals(expectedTreeNodeRecordRepositoryClass, treeNodeRecordRepository.getClass());

		memberRepository.destroyState();
		treeNodeRecordRepository.destroyState();
	}

	@Test
	void when_stateIsPostgres_and_additionalPropertiesAreMissing_then_throwException() {
		ComponentProperties props = new ComponentProperties("pipelineName", "", Map.of(STATE, "postgres"));

		assertThrows(ConfigPropertyMissingException.class, () -> LdesClientRepositoriesFactory.getLdesClientRepositories(props));
	}

	private static class ComponentPropertiesArgumentsProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(new ComponentProperties("pipelineName", "", Map.of(STATE, "memory")),
							InMemoryMemberRepository.class,
							InMemoryTreeNodeRecordRepository.class),
					Arguments.of(new ComponentProperties("pipelineName", "", Map.of(STATE, "sqlite",
									SQLITE_DIRECTORY, "target")),
							SqlMemberRepository.class,
							SqlTreeNodeRepository.class),
					Arguments.of(
							new ComponentProperties("pipelineName", ""
									, Map.of(STATE, "postgres",
									POSTGRES_URL, postgreSQLContainer.getJdbcUrl(),
									POSTGRES_USERNAME, postgreSQLContainer.getUsername(), POSTGRES_PASSWORD,
									postgreSQLContainer.getPassword(), KEEP_STATE, "false")),
							SqlMemberRepository.class, SqlTreeNodeRepository.class));
		}
	}

}