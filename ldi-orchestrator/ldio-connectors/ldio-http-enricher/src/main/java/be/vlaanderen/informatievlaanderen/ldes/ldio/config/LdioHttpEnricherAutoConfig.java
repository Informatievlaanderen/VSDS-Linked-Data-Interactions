package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.EmptyPropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
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

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioHttpEnricherProperties.*;

@Configuration
public class LdioHttpEnricherAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpEnricher")
	public LdioConfigurator ldioConfigurator(ConfigurableApplicationContext configContext) {
		return config -> {
			final LdiAdapter adapter = createAdapter(configContext, config);
			final RequestPropertyPathExtractors requestPropertyPaths = createRequestPropertyPathExtractors(config);
			final RequestExecutor requestExecutor = new LdioRequestExecutorSupplier().getRequestExecutor(config);
			return new LdioHttpEnricher(adapter, requestExecutor, requestPropertyPaths);
		};
	}

	// TODO TVB: 17/10/23 test correct mapping props to object..
	private RequestPropertyPathExtractors createRequestPropertyPathExtractors(ComponentProperties config) {
		final var urlPropertyPathExtractor = PropertyPathExtractor.from(config.getProperty(URL_PROPERTY_PATH));
		final var bodyPropertyPathExtractor = createPropertyPathExtractor(config, BODY_PROPERTY_PATH);
		final var headerPropertyPathExtractor = createPropertyPathExtractor(config, HEADER_PROPERTY_PATH);
		final var httpMethodPropertyPathExtractor = createPropertyPathExtractor(config, HTTP_METHOD_PROPERTY_PATH);
		return new RequestPropertyPathExtractors(
				urlPropertyPathExtractor,
				bodyPropertyPathExtractor,
				headerPropertyPathExtractor,
				httpMethodPropertyPathExtractor);
	}

	private PropertyExtractor createPropertyPathExtractor(ComponentProperties config, String property) {
		return config
				.getOptionalProperty(property)
				.map(PropertyPathExtractor::from)
				.map(PropertyExtractor.class::cast)
				.orElse(new EmptyPropertyExtractor());
	}

	private LdiAdapter createAdapter(ConfigurableApplicationContext configContext, ComponentProperties config) {
		final String adapterBeanName = config.getProperty(HTTP_REQUESTER_ADAPTER);
		final LdioConfigurator ldioConfigurator = (LdioConfigurator) configContext.getBean(adapterBeanName);
		return (LdiAdapter) ldioConfigurator.configure(config);
	}

}
