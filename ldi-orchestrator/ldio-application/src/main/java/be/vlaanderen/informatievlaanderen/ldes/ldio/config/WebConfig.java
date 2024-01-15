package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.converters.PipelineStatusConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
	@Bean
	public PipelineStatusConverter modelConverter() {
		return new PipelineStatusConverter();
	}
}
