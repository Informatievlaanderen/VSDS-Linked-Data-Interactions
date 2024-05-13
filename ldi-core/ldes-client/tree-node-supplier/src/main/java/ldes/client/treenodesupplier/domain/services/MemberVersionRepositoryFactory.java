package ldes.client.treenodesupplier.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberVersionRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberVersionRepository;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.postgres.PostgresEntityManagerFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.sqlite.SqliteEntityManagerFactory;

import java.util.Map;

public class MemberVersionRepositoryFactory {

    private MemberVersionRepositoryFactory() {
    }

    public static MemberVersionRepository getMemberVersionRepositoryFactory(StatePersistenceStrategy statePersistenceStrategy,
                                                                            Map<String, String> properties, String instanceName) {
        return switch (statePersistenceStrategy) {
            case SQLITE -> new SqlMemberVersionRepository(SqliteEntityManagerFactory.getInstance(instanceName, properties), instanceName);
            case MEMORY -> new InMemoryMemberVersionRepository();
            case POSTGRES -> new SqlMemberVersionRepository(PostgresEntityManagerFactory.getInstance(instanceName, properties), instanceName);
        };
    }
}
