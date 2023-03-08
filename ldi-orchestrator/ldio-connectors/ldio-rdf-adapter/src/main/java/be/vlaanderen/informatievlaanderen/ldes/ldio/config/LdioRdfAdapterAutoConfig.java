package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioRdfAdapterAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.RdfAdapter")
	public LdioConfigurator ldiHttpOutConfigurator() {
		return new LdioSparqlConstructConfigurator();
	}

	public static class LdioSparqlConstructConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties config) {
			return new RdfAdapter();
		}
	}
}
