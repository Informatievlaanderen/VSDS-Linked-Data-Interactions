package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class LdioHttpInAutoConfig {
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn")
	public LdioHttpInConfigurator ldioConfigurator() {
		return new LdioHttpInConfigurator();
	}

	public static class LdioHttpInConfigurator implements LdioInputConfigurator {

		@Override
		public LdiComponent configure(LdiAdapter adapter,
				ComponentExecutor executor,
				ComponentProperties config) {
			@RestController
			class LdioHttpInBean extends LdioHttpIn {

				public LdioHttpInBean() {
					// Workaround to lazy load the LdtoHttpIn RestController only when configured as
					// an LdtoInput
					super(executor, adapter, config.getProperty("pipeline.name"));
				}
			}
			return new LdioHttpInBean();
		}
	}
}
