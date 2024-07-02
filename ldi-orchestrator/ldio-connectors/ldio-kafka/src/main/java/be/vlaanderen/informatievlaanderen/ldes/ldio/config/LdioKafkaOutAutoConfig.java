package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.LdioOutputConfigurator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut.NAME;

@Configuration
public class LdioKafkaOutAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioOutputConfigurator ldiKafkaOutConfigurator() {
		return new LdioKafkaOutProcessorConfigurator();
	}

}
