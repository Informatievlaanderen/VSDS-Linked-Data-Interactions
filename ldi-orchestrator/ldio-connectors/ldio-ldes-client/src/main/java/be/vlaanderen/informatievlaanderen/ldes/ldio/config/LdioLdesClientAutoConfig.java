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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

@Configuration
public class LdioLdesClientAutoConfig {
	private static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldi.client.LdioLdesClient";
	@Bean(NAME)
	public LdioInputConfigurator ldioConfigurator() {
		return new LdioLdesClientConfigurator();
	}

	public static class LdioLdesClientConfigurator implements LdioInputConfigurator {

		private final LdioRequestExecutorSupplier ldioRequestExecutorSupplier = new LdioRequestExecutorSupplier();
		private final StatePersistenceFactory statePersistenceFactory = new StatePersistenceFactory();

		@Override
		public LdiComponent configure(LdiAdapter adapter, ComponentExecutor componentExecutor,
		                              ComponentProperties properties) {
			String pipelineName = properties.getProperty(PIPELINE_NAME);
			RequestExecutor requestExecutor = ldioRequestExecutorSupplier.getRequestExecutor(properties);
			StatePersistence statePersistence = statePersistenceFactory.getStatePersistence(properties);
			LdesClientRunner ldesClientRunner = new LdesClientRunner(NAME, pipelineName, componentExecutor, requestExecutor, properties,
					statePersistence);
			return new LdioLdesClient(NAME, pipelineName, componentExecutor, ldesClientRunner);
		}

	}
}
