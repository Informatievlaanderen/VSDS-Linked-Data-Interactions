package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioModelSplitAdapterAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitAdapter")
	public LdioConfigurator ldiHttpOutConfigurator(ConfigurableApplicationContext configContext) {
		return new LdioModelSplitAdapterConfigurator(configContext);
	}

	public static class LdioModelSplitAdapterConfigurator implements LdioConfigurator {

		public static final String SUBJECT_TYPE = "split-subject-type";
		public static final String BASE_ADAPTER = "split-base-adapter";

		private final ConfigurableApplicationContext configContext;

		public LdioModelSplitAdapterConfigurator(ConfigurableApplicationContext configContext) {
			this.configContext = configContext;
		}

		@Override
		public LdiComponent configure(ComponentProperties config) {
			final String subjectType = config.getProperty(SUBJECT_TYPE);
			final String baseAdapterBeanName = config.getProperty(BASE_ADAPTER);
			final LdioConfigurator ldioConfigurator = (LdioConfigurator) configContext.getBean(baseAdapterBeanName);
			final LdiAdapter adapter = (LdiAdapter) ldioConfigurator.configure(config);
			return new ModelSplitAdapter(subjectType, adapter, new ModelSplitter());
		}
	}
}
