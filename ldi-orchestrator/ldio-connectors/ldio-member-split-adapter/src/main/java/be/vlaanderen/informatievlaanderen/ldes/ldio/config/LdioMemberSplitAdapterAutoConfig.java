package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.ModelSplitAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioMemberSplitAdapterAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldi.MemberSplitAdapter")
	public LdioConfigurator ldiHttpOutConfigurator(ConfigurableApplicationContext configContext) {
		return new LdioMemberSplitAdapterConfigurator(configContext);
	}

	public static class LdioMemberSplitAdapterConfigurator implements LdioConfigurator {
		public static final String MEMBER_TYPE = "member-type";
		public static final String BASE_ADAPTER = "base-adapter";

		private final ConfigurableApplicationContext configContext;

		public LdioMemberSplitAdapterConfigurator(ConfigurableApplicationContext configContext) {
			this.configContext = configContext;
		}

		@Override
		public LdiComponent configure(ComponentProperties config) {
			final String memberType = config.getProperty(MEMBER_TYPE);
			final String baseAdapterBeanName = config.getProperty(BASE_ADAPTER);
			final LdioConfigurator ldioConfigurator = (LdioConfigurator) configContext.getBean(baseAdapterBeanName);
			final LdiAdapter adapter = (LdiAdapter) ldioConfigurator.configure(config);
			return new ModelSplitAdapter(memberType, adapter);
		}
	}
}
