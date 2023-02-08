package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ModelHttpConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class StarterPackAutoConfiguration {
	@Bean
	public ModelHttpConverter memberConverter() {
		return new ModelHttpConverter();
	}
}
