package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitTransformer;
import be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioModelSplitTransformerAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitTransformer")
	public LdioConfigurator ldiModelSplitConfigurator() {
		return new LdioModelSplitTransformerConfigurator();
	}

	public static class LdioModelSplitTransformerConfigurator implements LdioConfigurator {

		public static final String SUBJECT_TYPE = "split-subject-type";

		@Override
		public LdiComponent configure(ComponentProperties config) {
			final String subjectType = config.getProperty(SUBJECT_TYPE);
			return new ModelSplitTransformer(subjectType, new ModelSplitter());
		}
	}
}
