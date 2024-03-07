package ldes.client.performance;

import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import org.apache.jena.riot.Lang;

public enum TestScenario {

	// @formatter:off
	MEMORY10(StatePersistenceStrategy.MEMORY, FragmentSize.TEN),
	POSTGRES10(StatePersistenceStrategy.POSTGRES, FragmentSize.TEN),
	SQLITE10(StatePersistenceStrategy.SQLITE, FragmentSize.TEN),
	MEMORY250(StatePersistenceStrategy.MEMORY, FragmentSize.TWOFIFTY),
	POSTGRES250(StatePersistenceStrategy.POSTGRES, FragmentSize.TWOFIFTY),
	SQLITE250(StatePersistenceStrategy.SQLITE, FragmentSize.TWOFIFTY),
	MEMORY_EXTERNAL(StatePersistenceStrategy.MEMORY, FragmentSize.EXT),
	MEMORY_EXTERNAL_250_TURTLE(StatePersistenceStrategy.MEMORY, FragmentSize.EXT_TWOFIFTY, Lang.TURTLE),
	MEMORY_EXTERNAL_1000_TURTLE(StatePersistenceStrategy.MEMORY, FragmentSize.EXT_THOUSAND, Lang.TURTLE),
	MEMORY_EXTERNAL_500_TURTLE(StatePersistenceStrategy.MEMORY, FragmentSize.EXT_TWOFIFTY, Lang.TURTLE),
	MEMORY_EXTERNAL_250_PROTOBUF(StatePersistenceStrategy.MEMORY, FragmentSize.EXT_TWOFIFTY, Lang.RDFPROTO),
	MEMORY_EXTERNAL_500_PROTOBUF(StatePersistenceStrategy.MEMORY, FragmentSize.EXT_TWOFIFTY, Lang.RDFPROTO),
	MEMORY_EXTERNAL_1000_PROTOBUF(StatePersistenceStrategy.MEMORY, FragmentSize.EXT_THOUSAND, Lang.RDFPROTO);
	// @formatter:on

	private final StatePersistenceStrategy persistenceStrategy;
	private final FragmentSize fragmentSize;
	private final Lang sourceFormat;

	TestScenario(StatePersistenceStrategy persistenceStrategy, FragmentSize fragmentSize) {
		this(persistenceStrategy, fragmentSize, Lang.TURTLE);
	}

	TestScenario(StatePersistenceStrategy persistenceStrategy, FragmentSize fragmentSize, Lang sourceFormat) {
		this.persistenceStrategy = persistenceStrategy;
		this.fragmentSize = fragmentSize;
		this.sourceFormat = sourceFormat;
	}

	public StatePersistenceStrategy getPersistenceStrategy() {
		return persistenceStrategy;
	}

	public String getStartingEndpoint() {
		return fragmentSize.getStartingEndpoint();
	}

	public Lang getSourceFormat() {
		return sourceFormat;
	}
}
