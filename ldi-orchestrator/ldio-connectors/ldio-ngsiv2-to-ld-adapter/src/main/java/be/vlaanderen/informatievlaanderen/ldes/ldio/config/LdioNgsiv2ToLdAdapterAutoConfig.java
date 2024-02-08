package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.NgsiV2ToLdAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioNgsiv2ToLdAdapterAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean("Ldio:NgsiV2ToLdAdapter")
	public LdioAdapterConfigurator ldiHttpOutConfigurator() {
		return new LdioSparqlConstructProcessorConfigurator();
	}

	public static class LdioSparqlConstructProcessorConfigurator implements LdioAdapterConfigurator {
		@Override
		public LdiAdapter configure(ComponentProperties config) {
			String dataIdentifier = config.getProperty("data-identifier");
			String coreContext = config.getProperty("core-context");
			String ldContext = config.getOptionalProperty("ld-context").orElse(null);
			return new NgsiV2ToLdAdapter(dataIdentifier, coreContext, ldContext);
		}
	}
}
