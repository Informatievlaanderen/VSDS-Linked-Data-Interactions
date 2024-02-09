package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioOutputConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LdioKafkaOutAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut")
	public LdioOutputConfigurator ldiKafkaOutConfigurator() {
		return new LdioKafkaOutProcessorConfigurator();
	}

}
