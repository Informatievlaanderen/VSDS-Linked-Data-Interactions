package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioAdapterConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioJsonToLdAdapterAutoConfig {
	public static final String NAME = "Ldio:JsonToLdAdapter";

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioAdapterConfigurator ldioJsonToLdAdapterConfigurator() {
		return new LdioJsonToLdConfigurator();
	}

	public static class LdioJsonToLdConfigurator implements LdioAdapterConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties config) {
			String coreContext = config.getProperty("context");
			boolean forceContentType = config.getOptionalBoolean("force-content-type").orElse(false);

			final int maxCacheCapacity = config.getOptionalInteger("max-jsonld-cache-capacity").orElse(100);
			return new JsonToLdAdapter(coreContext, forceContentType, maxCacheCapacity);
		}
	}
}
