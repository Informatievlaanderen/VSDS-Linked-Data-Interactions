package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioTransformerConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioModelSplitTransformerAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitTransformer")
	public LdioTransformerConfigurator ldiModelSplitConfigurator() {
		return new LdioModelSplitTransformerTransformerConfigurator();
	}

	public static class LdioModelSplitTransformerTransformerConfigurator implements LdioTransformerConfigurator {

		public static final String SUBJECT_TYPE = "split-subject-type";

		@Override
		public LdioTransformer configure(ComponentProperties config) {
			final String subjectType = config.getProperty(SUBJECT_TYPE);
			return new LdioModelSplitter(subjectType);
		}

	}
}
