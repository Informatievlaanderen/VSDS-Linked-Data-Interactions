package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioRdfAdapterAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter")
	public LdioAdapterConfigurator ldiHttpOutConfigurator() {
		return new LdioRdfConfigurator();
	}

	public static class LdioRdfConfigurator implements LdioAdapterConfigurator {
		@Override
		public LdiAdapter configure(ComponentProperties config) {
			return new RdfAdapter();
		}
	}
}
