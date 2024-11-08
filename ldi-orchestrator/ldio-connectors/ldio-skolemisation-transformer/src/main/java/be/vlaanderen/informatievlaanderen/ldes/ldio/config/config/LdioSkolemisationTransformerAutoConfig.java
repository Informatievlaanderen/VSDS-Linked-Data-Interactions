package be.vlaanderen.informatievlaanderen.ldes.ldio.config.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.SkolemisationTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioSkolemisationTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioSkolemisationTransformerAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(LdioSkolemisationTransformer.NAME)
	public LdioTransformerConfigurator ldioConfigurator() {
		return new LdioSkolemisationTransformerConfigurator();
	}

	public static class LdioSkolemisationTransformerConfigurator implements LdioTransformerConfigurator {
		@Override
		public LdioTransformer configure(ComponentProperties config) {
			String skolemDomain = config.getProperty("skolem-domain");
			return new LdioSkolemisationTransformer(new SkolemisationTransformer(skolemDomain));
		}
	}
}
