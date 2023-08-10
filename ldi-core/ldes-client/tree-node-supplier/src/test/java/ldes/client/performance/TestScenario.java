package ldes.client.performance;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;

public enum TestScenario {

    SQLITE10(StatePersistenceStrategy.SQLITE, 10),
    MEMORY10(StatePersistenceStrategy.MEMORY, 10),
    FILE10(StatePersistenceStrategy.POSTGRES, 10);

    private final StatePersistenceStrategy persistenceStrategy;
    private final int fragmentSize;

    TestScenario(StatePersistenceStrategy persistenceStrategy, int fragmentSize) {
        this.persistenceStrategy = persistenceStrategy;
        this.fragmentSize = fragmentSize;
    }

    public StatePersistenceStrategy getPersistenceStrategy() {
        return persistenceStrategy;
    }

    public int getFragmentSize() {
        return fragmentSize;
    }

}
