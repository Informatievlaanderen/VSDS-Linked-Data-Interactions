package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioJsonToLdAdapterAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter")
	public LdioAdapterConfigurator ldioJsonToLdAdapterConfigurator() {
		return new LdioJsonToLdConfigurator();
	}

	public static class LdioJsonToLdConfigurator implements LdioAdapterConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties config) {
			String coreContext = config.getProperty("core-context");
			boolean forceContentType = config.getOptionalBoolean("force-content-type").orElse(false);
			return new JsonToLdAdapter(coreContext, forceContentType);
		}
	}
}
