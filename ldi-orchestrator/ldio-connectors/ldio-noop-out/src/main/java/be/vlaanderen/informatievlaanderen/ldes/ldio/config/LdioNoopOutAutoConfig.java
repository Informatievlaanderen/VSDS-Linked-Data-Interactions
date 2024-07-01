package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioNoopOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.LdioOutputConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioNoopOut.NAME;

@Configuration
public class LdioNoopOutAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioOutputConfigurator ldiHttpOutConfigurator() {
		return new LdioHttpOutConfigurator();
	}

	public static class LdioHttpOutConfigurator implements LdioOutputConfigurator {

		@Override
		public LdiComponent configure(ComponentProperties config) {
			return new LdioNoopOut();
		}
	}
}
