package ldes.client.performance;

import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.timestampextractor.TimestampFromCurrentTimeExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.TreeNodeProcessor;
import ldes.client.treenodesupplier.domain.services.MemberIdRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.MemberRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.MemberVersionRepositoryFactory;
import ldes.client.treenodesupplier.domain.services.TreeNodeRecordRepositoryFactory;
import ldes.client.treenodesupplier.domain.valueobject.LdesMetaData;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;
import ldes.client.treenodesupplier.repository.MemberIdRepository;
import ldes.client.treenodesupplier.repository.MemberRepository;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.TreeNodeRecordRepository;
import org.apache.jena.riot.Lang;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

class TreeNodeProcessorFactory {

	private final RequestExecutorFactory requestExecutorFactory = new RequestExecutorFactory(false);

	TreeNodeProcessor createTreeNodeProcessor(StatePersistenceStrategy statePersistenceStrategy, List<String> url, Lang sourceFormat) {
		final LdesMetaData ldesMetaData = new LdesMetaData(url, sourceFormat);
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
		final SqliteProperties sqliteProperties = new SqliteProperties("instanceName", false);
		MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.SQLITE,
				sqliteProperties, "instanceName");
		TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.SQLITE, sqliteProperties, "instanceName");
		MemberIdRepository memberIdRepository = MemberIdRepositoryFactory.getMemberIdRepository(StatePersistenceStrategy.SQLITE,
				sqliteProperties, "instanceName");
		MemberVersionRepository memberVersionRepository = MemberVersionRepositoryFactory.getMemberVersionRepositoryFactory(StatePersistenceStrategy.SQLITE,
				sqliteProperties, "instanceName");

		return new StatePersistence(memberRepository, memberIdRepository, treeNodeRecordRepository, memberVersionRepository);
	}

	private StatePersistence createPostgresPersistence() {
		final PostgreSQLContainer postgreSQLContainer = startPostgresContainer();

		PostgresProperties postgresProperties = new PostgresProperties(postgreSQLContainer.getJdbcUrl(),
				postgreSQLContainer.getUsername(), postgreSQLContainer.getPassword(), false);
		MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(
				StatePersistenceStrategy.POSTGRES,
				postgresProperties, "instanceName");
		TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.POSTGRES, postgresProperties,
						"instanceName");
		MemberIdRepository memberIdRepository = MemberIdRepositoryFactory.getMemberIdRepository(
				StatePersistenceStrategy.POSTGRES,
				postgresProperties, "instanceName");
		MemberVersionRepository memberVersionRepository = MemberVersionRepositoryFactory.getMemberVersionRepositoryFactory(
				StatePersistenceStrategy.POSTGRES,
				postgresProperties, "instanceName");

		return new StatePersistence(memberRepository, memberIdRepository, treeNodeRecordRepository, memberVersionRepository);
	}

	private StatePersistence createInMemoryStatePersistence() {
		final SqliteProperties sqliteProperties = new SqliteProperties("instanceName", false);
		MemberRepository memberRepository = MemberRepositoryFactory.getMemberRepository(StatePersistenceStrategy.MEMORY,
				sqliteProperties, "instanceName");
		TreeNodeRecordRepository treeNodeRecordRepository = TreeNodeRecordRepositoryFactory
				.getTreeNodeRecordRepository(StatePersistenceStrategy.MEMORY, sqliteProperties, "instanceName");
		MemberIdRepository memberIdRepository = MemberIdRepositoryFactory.getMemberIdRepository(StatePersistenceStrategy.MEMORY,
				sqliteProperties, "instanceName");
		MemberVersionRepository memberVersionRepository = MemberVersionRepositoryFactory.getMemberVersionRepositoryFactory(StatePersistenceStrategy.MEMORY,
				sqliteProperties, "instanceName");
		return new StatePersistence(memberRepository, memberIdRepository, treeNodeRecordRepository, memberVersionRepository);
	}

}
