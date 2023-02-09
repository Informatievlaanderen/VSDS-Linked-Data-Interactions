package be.vlaanderen.informatievlaanderen.ldes.ldto.config;

import be.vlaanderen.informatievlaanderen.ldes.ldto.input.LdtoHttpIn;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldto.services.ModelHttpConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan
public class StarterPackAutoConfiguration {
	@Bean
	public ModelHttpConverter memberConverter() {
		return new ModelHttpConverter();
	}

	@Bean
	@Qualifier("be.vlaanderen.informatievlaanderen.ldes.ldto.input.LdtoHttpIn")
	public LdtoHttpIn ldtoHttpIn(ComponentExecutor componentExecutor) {
		// Workaround to lazy load the LdtoHttpIn RestController only when configured as an LdtoInput
		@RestController
		class LdtoHttpInBean extends LdtoHttpIn {
			public LdtoHttpInBean(ComponentExecutor componentExecutor) {
				super(componentExecutor);
			}
		}
		return new LdtoHttpInBean(componentExecutor);
	}
}
