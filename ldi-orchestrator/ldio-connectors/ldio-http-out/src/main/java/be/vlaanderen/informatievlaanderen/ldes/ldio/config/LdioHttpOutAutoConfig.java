package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.requestexecutor.LdioRequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties.RDF_WRITER;

@Configuration
public class LdioHttpOutAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpOut")
	public LdioOutputConfigurator ldiHttpOutConfigurator(ObservationRegistry observationRegistry) {
		return new LdioHttpOutConfigurator(observationRegistry);
	}

	public static class LdioHttpOutConfigurator implements LdioOutputConfigurator {
		private final ObservationRegistry observationRegistry;

		public LdioHttpOutConfigurator(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
		}

		@Override
		public LdiComponent configure(ComponentProperties config) {
			final RequestExecutor requestExecutor = new LdioRequestExecutorSupplier().getRequestExecutor(config);

			String targetURL = config.getProperty("endpoint");

			return new LdioHttpOut(requestExecutor, targetURL,
					new LdiRdfWriterProperties(config.extractNestedProperties(RDF_WRITER).getConfig()),
					observationRegistry);
		}
	}
}
