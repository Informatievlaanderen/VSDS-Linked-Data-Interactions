package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioJsonToLdAdapterAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.JsonToLdAdapter")
	public LdioConfigurator ldioJsonToLdAdapterConfigurator() {
		return new LdioJsonToLdAdapterConfigurator();
	}

	public static class LdioJsonToLdAdapterConfigurator implements LdioConfigurator {
		@Override
		public LdiComponent configure(ComponentProperties config) {
			String coreContext = config.getProperty("core-context");
			String ldContext = config.getOptionalProperty("ld-context").orElse(null);
			return new JsonToLdAdapter(coreContext, ldContext);
		}
	}
}
