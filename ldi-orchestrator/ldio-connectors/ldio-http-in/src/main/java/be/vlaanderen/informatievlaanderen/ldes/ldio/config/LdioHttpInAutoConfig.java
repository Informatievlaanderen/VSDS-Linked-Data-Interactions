package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunctions;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException.verifyAdapterPresent;

@Configuration
public class LdioHttpInAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioHttpIn")
	public LdioHttpInConfigurator ldioConfigurator() {
		return new LdioHttpInConfigurator();
	}

	public static class LdioHttpInConfigurator implements LdioInputConfigurator {

		@Autowired
		ConfigurableApplicationContext configContext;

		@Override
		public Object configure(LdiAdapter adapter,
				ComponentExecutor executor,
				ComponentProperties config) {
			String pipelineName = config.getProperty(PIPELINE_NAME);
			verifyAdapterPresent(pipelineName, adapter);

			LdioHttpIn ldioHttpIn = new LdioHttpIn(executor, adapter, pipelineName);

			return ldioHttpIn.mapping();
		}
	}
}
