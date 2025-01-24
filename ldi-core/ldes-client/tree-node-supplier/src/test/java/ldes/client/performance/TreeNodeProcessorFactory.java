package ldes.client.performance;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateUtil;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.valueobject.LdesClientRepositories;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import org.apache.jena.riot.Lang;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.function.Consumer;

class TreeNodeProcessorFactory {

	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory(false);

	TreeNodeProcessor createTreeNodeProcessor(StatePersistenceStrategy statePersistenceStrategy, List<String> url, Lang sourceFormat) {
		final LdesMetaData ldesMetaData = new LdesMetaData(url, sourceFormat);
		final LdesClientRepositories ldesClientRepositories = switch (statePersistenceStrategy) {
			case MEMORY -> createInMemoryStatePersistence();
			case SQLITE -> createSqliteStatePersistence();
			case POSTGRES -> createPostgresPersistence();
		};
		final RequestExecutor requestExecutor = requestExecutorFactory.createNoAuthExecutor();
		final TimestampExtractor timestampExtractor = new TimestampFromCurrentTimeExtractor();
		return new TreeNodeProcessor(ldesMetaData, ldesClientRepositories, requestExecutor, timestampExtractor, Mockito.mock(Consumer.class));
	}

	private PostgreSQLContainer startPostgresContainer() {
		PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
				.withDatabaseName("integration-test-client-persistence")
				.withUsername("sa")
				.withPassword("sa");
		postgreSQLContainer.start();
		return postgreSQLContainer;
	}

	private LdesClientRepositories createSqliteStatePersistence() {
		final SqliteProperties sqliteProperties = new SqliteProperties("instanceName", false);
		LdesClientRepositories clientRepositories = LdesClientRepositories.sqlBased(
				HibernateUtil.createEntityManagerFromProperties(sqliteProperties.getProperties()));

		return new LdesClientRepositories(clientRepositories.memberRepository(), clientRepositories.memberIdRepository(),
				clientRepositories.treeNodeRecordRepository(), clientRepositories.memberVersionRepository());
	}

	private LdesClientRepositories createPostgresPersistence() {
		final PostgreSQLContainer postgreSQLContainer = startPostgresContainer();

		PostgresProperties postgresProperties = new PostgresProperties(postgreSQLContainer.getJdbcUrl(),
				postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword(), false);

		LdesClientRepositories clientRepositories = LdesClientRepositories.sqlBased(
				HibernateUtil.createEntityManagerFromProperties(postgresProperties.getProperties()));

		return new LdesClientRepositories(clientRepositories.memberRepository(), clientRepositories.memberIdRepository(),
				clientRepositories.treeNodeRecordRepository(), clientRepositories.memberVersionRepository());
	}

	private LdesClientRepositories createInMemoryStatePersistence() {
		LdesClientRepositories clientRepositories = LdesClientRepositories.memoryBased();

		return new LdesClientRepositories(clientRepositories.memberRepository(), clientRepositories.memberIdRepository(),
				clientRepositories.treeNodeRecordRepository(), clientRepositories.memberVersionRepository());
	}

}
