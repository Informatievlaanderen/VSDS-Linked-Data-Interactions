package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.HibernateProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.sqlite.SqliteEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberVersionRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberVersionRepository;

public class MemberVersionRepositoryFactory {

    private MemberVersionRepositoryFactory() {
    }

    public static MemberVersionRepository getMemberVersionRepositoryFactory(StatePersistenceStrategy statePersistenceStrategy,
                                                                            HibernateProperties properties, String instanceName) {
        return switch (statePersistenceStrategy) {
            case SQLITE -> new SqlMemberVersionRepository(SqliteEntityManagerFactory.getInstance(properties), instanceName);
            case MEMORY -> new InMemoryMemberVersionRepository();
            case POSTGRES -> new SqlMemberVersionRepository(PostgresEntityManagerFactory.getInstance(instanceName, properties.getProperties()), instanceName);
        };
    }
}
