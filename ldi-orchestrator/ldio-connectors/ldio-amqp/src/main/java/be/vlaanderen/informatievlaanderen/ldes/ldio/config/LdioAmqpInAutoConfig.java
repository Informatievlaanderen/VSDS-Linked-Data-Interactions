package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException.verifyAdapterPresent;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.STARTING;

@Configuration
public class LdioAmqpInAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(LdioAmqpIn.NAME)
	public LdioJmsInConfigurator ldioConfigurator(LdioAmqpInRegistrator ldioAmqpInRegistrator,
	                                              ObservationRegistry observationRegistry) {
		return new LdioJmsInConfigurator(ldioAmqpInRegistrator, observationRegistry);
	}

	public static class LdioJmsInConfigurator implements LdioInputConfigurator {

		private final LdioAmqpInRegistrator ldioAmqpInRegistrator;

		private final ObservationRegistry observationRegistry;

		public LdioJmsInConfigurator(LdioAmqpInRegistrator ldioAmqpInRegistrator, ObservationRegistry observationRegistry) {
			this.ldioAmqpInRegistrator = ldioAmqpInRegistrator;
			this.observationRegistry = observationRegistry;
		}

		@Override
		public LdioInput configure(LdiAdapter adapter, ComponentExecutor executor, ApplicationEventPublisher applicationEventPublisher, ComponentProperties config) {
			String pipelineName = config.getPipelineName();
			verifyAdapterPresent(pipelineName, adapter);

			String remoteUrl = new RemoteUrlExtractor(config).getRemoteUrl();
			JmsConfig jmsConfig = new JmsConfig(config.getProperty(USERNAME), config.getProperty(PASSWORD),
					remoteUrl, config.getProperty(QUEUE));

			LdioAmqpIn ldioIn = new LdioAmqpIn(pipelineName, executor, adapter, getContentType(config), jmsConfig, ldioAmqpInRegistrator,
					observationRegistry, applicationEventPublisher);
			ldioIn.updateStatus(STARTING);
			return ldioIn;
		}

		private String getContentType(ComponentProperties config) {
			return config
					.getOptionalProperty(CONTENT_TYPE)
					.map(RDFLanguages::contentTypeToLang)
					.map(Lang::getHeaderString)
					.orElse(Lang.NQUADS.getHeaderString());
		}

	}
}
