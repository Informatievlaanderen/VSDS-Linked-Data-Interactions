package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientRunner;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioLdesClient;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;

public class LdioLdesClientConfigurator implements LdioInputConfigurator {

	private final LdioRequestExecutorSupplier ldioRequestExecutorSupplier = new LdioRequestExecutorSupplier();
	private final StatePersistenceFactory statePersistenceFactory = new StatePersistenceFactory();

	@Override
	public LdiComponent configure(LdiAdapter adapter, ComponentExecutor componentExecutor,
			ComponentProperties properties) {
		RequestExecutor requestExecutor = ldioRequestExecutorSupplier.getRequestExecutor(properties);
		StatePersistence statePersistence = statePersistenceFactory.getStatePersistence(properties);
		LdesClientRunner ldesClientRunner = new LdesClientRunner(requestExecutor, properties, componentExecutor,
				statePersistence);
		return new LdioLdesClient(componentExecutor, ldesClientRunner);
	}

}
