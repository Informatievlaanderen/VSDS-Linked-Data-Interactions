package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpEnricher;
import be.vlaanderen.informatievlaanderen.ldes.ldio.RequestPropertyPathExtractors;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpEnricherProperties.HTTP_REQUESTER_ADAPTER;

@Configuration
public class LdioHttpEnricherAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpEnricher")
	public LdioConfigurator ldioConfigurator(ConfigurableApplicationContext configContext) {
		return config -> {
			final LdiAdapter adapter = createAdapter(configContext, config);
			final RequestPropertyPathExtractors requestPropertyPaths =
					new PropertyPathExtractorConverter(config).mapToPropertyPathExtractors();
			final RequestExecutor requestExecutor = new LdioRequestExecutorSupplier().getRequestExecutor(config);
			return new LdioHttpEnricher(adapter, requestExecutor, requestPropertyPaths);
		};
	}

	private LdiAdapter createAdapter(ConfigurableApplicationContext configContext, ComponentProperties config) {
		final String adapterBeanName = config.getProperty(HTTP_REQUESTER_ADAPTER);
		final LdioConfigurator ldioConfigurator = (LdioConfigurator) configContext.getBean(adapterBeanName);
		return (LdiAdapter) ldioConfigurator.configure(config);
	}

}
