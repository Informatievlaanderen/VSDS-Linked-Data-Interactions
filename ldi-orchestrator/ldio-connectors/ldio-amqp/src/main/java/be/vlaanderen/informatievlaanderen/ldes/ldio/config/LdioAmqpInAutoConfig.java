package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.PipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.statusmanagement.pipelinestatus.StartedPipelineStatus;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioObserver;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.LdioAmpqInProperties;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig.*;

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
		public LdioInput configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties config) {
			final String pipelineName = config.getPipelineName();
			final String remoteUrl = new RemoteUrlExtractor(config).getRemoteUrl();
			final JmsConfig jmsConfig = new JmsConfig(config.getProperty(USERNAME), config.getProperty(PASSWORD),
					remoteUrl, config.getProperty(QUEUE));
			final LdioAmpqInProperties properties = new LdioAmpqInProperties(pipelineName, getContentType(config), jmsConfig);
			final LdioObserver ldioObserver = LdioObserver.register(LdioAmqpIn.NAME, pipelineName, observationRegistry);
			return new LdioAmqpIn(executor, adapter, ldioObserver, ldioAmqpInRegistrator, properties);
		}

		@Override
		public boolean isAdapterRequired() {
			return true;
		}

		@Override
		public PipelineStatus getInitialPipelineStatus() {
			return new StartedPipelineStatus();
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
