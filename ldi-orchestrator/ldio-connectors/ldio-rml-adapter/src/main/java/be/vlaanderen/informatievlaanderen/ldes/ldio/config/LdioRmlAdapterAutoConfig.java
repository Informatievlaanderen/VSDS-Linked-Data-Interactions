package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.RmlAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioRmlAdapterAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.RmlAdapter")
	public LdioConfigurator ldiHttpOutConfigurator() {
		return new LdioRmlAdapterConfigurator();
	}

	public static class LdioRmlAdapterConfigurator implements LdioConfigurator {
		public static final String MAPPING = "mapping";
		@Override
		public LdiComponent configure(ComponentProperties config) {
			String rmlMapping = config.getOptionalPropertyFromFile(MAPPING).orElse(config.getProperty(MAPPING));

			return new RmlAdapter(rmlMapping);
		}
	}
}
