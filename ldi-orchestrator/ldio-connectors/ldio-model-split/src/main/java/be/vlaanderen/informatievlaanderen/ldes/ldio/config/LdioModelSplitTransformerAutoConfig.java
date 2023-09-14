package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioProcessorConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioProcessor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioModelSplitTransformerAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitTransformer")
	public LdioProcessorConfigurator ldiModelSplitConfigurator() {
		return new LdioModelSplitTransformerProcessorConfigurator();
	}

	public static class LdioModelSplitTransformerProcessorConfigurator implements LdioProcessorConfigurator {

		public static final String SUBJECT_TYPE = "split-subject-type";

		@Override
		public LdioProcessor configure(ComponentProperties config) {
			final String subjectType = config.getProperty(SUBJECT_TYPE);
			return new LdioModelSplitter(subjectType);
		}

	}
}
