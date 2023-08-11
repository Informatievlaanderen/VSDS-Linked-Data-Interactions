package ldes.client.performance;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;

public enum TestScenario {

	// @formatter:off
	MEMORY10(StatePersistenceStrategy.MEMORY, FragmentSize.TEN),
	POSTGRES10(StatePersistenceStrategy.POSTGRES, FragmentSize.TEN),
	SQLITE10(StatePersistenceStrategy.SQLITE, FragmentSize.TEN),
	FILE10(StatePersistenceStrategy.FILE, FragmentSize.TEN),
	MEMORY250(StatePersistenceStrategy.MEMORY, FragmentSize.TWOFIFTY),
	POSTGRES250(StatePersistenceStrategy.POSTGRES, FragmentSize.TWOFIFTY),
	SQLITE250(StatePersistenceStrategy.SQLITE, FragmentSize.TWOFIFTY),
	FILE250(StatePersistenceStrategy.FILE, FragmentSize.TWOFIFTY);
	// @formatter:on

	private final StatePersistenceStrategy persistenceStrategy;
	private final FragmentSize fragmentSize;

	TestScenario(StatePersistenceStrategy persistenceStrategy, FragmentSize fragmentSize) {
		this.persistenceStrategy = persistenceStrategy;
		this.fragmentSize = fragmentSize;
	}

	public StatePersistenceStrategy getPersistenceStrategy() {
		return persistenceStrategy;
	}

	public String getStartingEndpoint() {
		return fragmentSize.getStartingEndpoint();
	}

}
