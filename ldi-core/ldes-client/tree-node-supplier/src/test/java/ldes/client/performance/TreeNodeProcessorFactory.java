package ldes.client.performance;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.services.MemberIdRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresProperties;
import org.apache.jena.riot.Lang;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Map;

class TreeNodeProcessorFactory {

	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory();

	TreeNodeProcessor createTreeNodeProcessor(StatePersistenceStrategy statePersistenceStrategy, List<String> url) {
		final LdesMetaData ldesMetaData = new LdesMetaData(url, Lang.TURTLE);
		final StatePersistence statePersistence = switch (statePersistenceStrategy) {
			case MEMORY -> createInMemoryStatePersistence();
			case SQLITE -> createSqliteStatePersistence();
			case POSTGRES -> createPostgresPersistence();
		};
		final RequestExecutor requestExecutor = requestExecutorFactory.createNoAuthExecutor();
		final TimestampExtractor timestampExtractor = new TimestampFromCurrentTimeExtractor();
		return new TreeNodeProcessor(ldesMetaData, statePersistence, requestExecutor, timestampExtractor);
	}

	private PostgreSQLContainer startPostgresContainer() {
		PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
				.withDatabaseName("integration-test-client-persistence")
				.withUsername("sa")
				.withPassword("sa");
		postgreSQLContainer.start();
		return postgreSQLContainer;
	}

	private StatePersistence createSqliteStatePersistence() {
		MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.SQLITE,
				Map.of(), "instanceName");
		TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.SQLITE, Map.of(), "instanceName");
		MemberIdRepository memberIdRepository = MemberIdRepositoryFactory.getMemberIdRepository(StatePersistenceStrategy.SQLITE,
				Map.of(), "instanceName");
		return new StatePersistence(memberRepository, memberIdRepository, treeNodeRecordRepository);
	}

	private StatePersistence createPostgresPersistence() {
		final PostgreSQLContainer postgreSQLContainer = startPostgresContainer();

		PostgresProperties postgresProperties = new PostgresProperties(postgreSQLContainer.getJdbcUrl(),
				postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword(), false);
		MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(
				StatePersistenceStrategy.POSTGRES,
				postgresProperties.getProperties(), "instanceName");
		TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.POSTGRES, postgresProperties.getProperties(),
						"instanceName");
		MemberIdRepository memberIdRepository = MemberIdRepositoryFactory.getMemberIdRepository(
				StatePersistenceStrategy.POSTGRES,
				postgresProperties.getProperties(), "instanceName");

		return new StatePersistence(memberRepository, memberIdRepository, treeNodeRecordRepository);
	}

	private StatePersistence createInMemoryStatePersistence() {
		MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.MEMORY,
				Map.of(), "instanceName");
		TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.MEMORY, Map.of(), "instanceName");
		MemberIdRepository memberIdRepository = MemberIdRepositoryFactory.getMemberIdRepository(StatePersistenceStrategy.MEMORY,
				Map.of(), "instanceName");
		return new StatePersistence(memberRepository, memberIdRepository, treeNodeRecordRepository);
	}

}
