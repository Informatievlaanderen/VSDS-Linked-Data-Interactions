package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioKafkaOutAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut")
	public LdioConfigurator ldiKafkaOutConfigurator() {
		return new LdioKafkaOutConfigurator();
	}

}
