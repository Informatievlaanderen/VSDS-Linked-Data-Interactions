package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpEnricher;
import be.vlaanderen.informatievlaanderen.ldes.ldio.RequestPropertyPathExtractors;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpEnricher.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpEnricherProperties.ADAPTER_CONFIG;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpEnricherProperties.ADAPTER_NAME;

@Configuration
public class LdioHttpEnricherAutoConfig {

	@Bean(NAME)
	public LdioTransformerConfigurator ldioConfigurator(ConfigurableApplicationContext configContext) {
		return config -> {
			final LdiAdapter adapter = createAdapter(configContext, config);
			final RequestPropertyPathExtractors requestPropertyPaths = new PropertyPathExtractorConverter(config)
					.mapToPropertyPathExtractors();
			final RequestExecutor requestExecutor = new LdioRequestExecutorSupplier().getRequestExecutor(config);
			return new LdioHttpEnricher(adapter, requestExecutor, requestPropertyPaths);
		};
	}

	private LdiAdapter createAdapter(ConfigurableApplicationContext configContext, ComponentProperties config) {
		final String adapterBeanName = config.getProperty(ADAPTER_NAME);
		final LdioAdapterConfigurator ldioConfigurator = (LdioAdapterConfigurator) configContext
				.getBean(adapterBeanName);
		return (LdiAdapter) ldioConfigurator.configure(config.extractNestedProperties(ADAPTER_CONFIG));
	}

}
