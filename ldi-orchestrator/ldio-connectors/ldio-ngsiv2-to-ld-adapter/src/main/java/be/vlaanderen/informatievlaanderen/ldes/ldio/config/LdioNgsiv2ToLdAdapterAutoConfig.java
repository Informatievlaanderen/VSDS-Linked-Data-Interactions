package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import be.vlaanderen.informatievlaanderen.ldes.ldi.NgsiV2ToLdAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioNgsiv2ToLdAdapterAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.NgsiV2ToLdAdapter")
	public LdioConfigurator ldiHttpOutConfigurator() {
		return new LdioSparqlConstructConfigurator();
	}

	public static class LdioSparqlConstructConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties config) {
			String dataIdentifier = config.getProperty("dataIdentifier");
			String coreContext = config.getProperty("coreContext");
			String ldContext = config.getOptionalProperty("ldContext").orElse(null);
			return new NgsiV2ToLdAdapter(dataIdentifier, coreContext, ldContext);
		}
	}
}
