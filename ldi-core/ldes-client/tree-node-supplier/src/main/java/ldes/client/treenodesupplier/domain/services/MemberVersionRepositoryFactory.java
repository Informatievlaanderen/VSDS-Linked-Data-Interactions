package ldes.client.treenodesupplier.domain.services;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import ldes.client.treenodesupplier.repository.MemberVersionRepository;
import ldes.client.treenodesupplier.repository.inmemory.InMemoryMemberVersionRepository;
import ldes.client.treenodesupplier.repository.sql.SqlMemberVersionRepository;
import ldes.client.treenodesupplier.repository.sql.postgres.PostgresEntityManagerFactory;
import ldes.client.treenodesupplier.repository.sql.sqlite.SqliteEntityManagerFactory;

import java.util.Map;

public class MemberVersionRepositoryFactory {

    private MemberVersionRepositoryFactory() {
    }

    public static MemberVersionRepository getMemberVersionRepositoryFactory(StatePersistenceStrategy statePersistenceStrategy,
                                                                            Map<String, String> properties, String instanceName) {
        return switch (statePersistenceStrategy) {
            case SQLITE -> new SqlMemberVersionRepository(SqliteEntityManagerFactory.getInstance(instanceName), instanceName);
            case MEMORY -> new InMemoryMemberVersionRepository();
            case POSTGRES -> new SqlMemberVersionRepository(PostgresEntityManagerFactory.getInstance(instanceName, properties), instanceName);
        };
    }
}
