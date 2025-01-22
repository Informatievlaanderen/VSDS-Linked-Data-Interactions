package be.vlaanderen.informatievlaanderen.ldes.ldi.processors;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateUtil;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.services.NiFiDBCPDataSource;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.HashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.inmemory.InMemoryHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repositories.sql.SqlHashedStateMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import org.apache.nifi.dbcp.DBCPService;
import org.apache.nifi.processor.ProcessContext;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties.DBCP_SERVICE;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.PersistenceProperties.getStatePersistenceStrategy;

public class HashedMemberRepositoryFactory {
	private HashedMemberRepositoryFactory() {
	}

	public static HashedStateMemberRepository getRepository(ProcessContext context) {
		StatePersistenceStrategy state = getStatePersistenceStrategy(context);

		return switch (state) {
			case POSTGRES, SQLITE -> {
				final DBCPService dbcpService = context.getProperty(DBCP_SERVICE).asControllerService(DBCPService.class);
				final NiFiDBCPDataSource dataSource = new NiFiDBCPDataSource(dbcpService);
				final boolean keepState = PersistenceProperties.stateKept(context);

				var entityManager = HibernateUtil.createEntityManagerFromDatasource(dataSource, keepState);

				yield new SqlHashedStateMemberRepository(entityManager);
			}
			case MEMORY -> new InMemoryHashedStateMemberRepository();
		};
	}
}
