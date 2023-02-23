package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.services.ModelHttpConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioHttpInAutoConfig {
	@Bean
	public ModelHttpConverter memberConverter() {
		return new ModelHttpConverter();
	}

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn")
	public LdioConfigurator ldioConfigurator(ComponentExecutor componentExecutor) {
		return new LdioHttpInConfigurator(componentExecutor);
	}

	public static class LdioHttpInConfigurator implements LdioConfigurator {
		private final ComponentExecutor componentExecutor;

		public LdioHttpInConfigurator(ComponentExecutor componentExecutor) {
			this.componentExecutor = componentExecutor;
		}

		@Override
		public LdiComponent configure(ComponentProperties config) {
			// Workaround to lazy load the LdtoHttpIn RestController only when configured as
			// an LdtoInput
			@RestController
			class LdioHttpInBean extends LdioHttpIn {
				public LdioHttpInBean(ComponentExecutor componentExecutor) {
					super(componentExecutor);
				}
			}
			return new LdioHttpInBean(componentExecutor);
		}
	}
}
