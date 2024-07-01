package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RmlAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioAdapterConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioRmlAdapterAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean("Ldio:RmlAdapter")
	public LdioAdapterConfigurator ldioAdapterConfigurator() {
		return new LdioRmlAdapterProcessorConfigurator();
	}

	public static class LdioRmlAdapterProcessorConfigurator implements LdioAdapterConfigurator {
		public static final String MAPPING = "mapping";

		@Override
		public LdiAdapter configure(ComponentProperties config) {
			String rmlMapping = config.getOptionalPropertyFromFile(MAPPING).orElse(config.getProperty(MAPPING));

			return new RmlAdapter(rmlMapping);
		}
	}
}
