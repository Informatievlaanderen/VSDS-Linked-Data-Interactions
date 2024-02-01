package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpInConfigKeys.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException.verifyAdapterPresent;

@Configuration
public class LdioAmqpInAutoConfig {
	private static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpIn";

	@Bean(NAME)
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
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties config) {
			String pipelineName = config.getProperty(PIPELINE_NAME);
			verifyAdapterPresent(pipelineName, adapter);

			LdioAmqpIn ldioAmqpIn = new LdioAmqpIn(NAME, pipelineName, executor, adapter, observationRegistry,
					getContentType(config), ldioAmqpInRegistrator);

			Optional<String> username = config.getOptionalProperty(USERNAME);

			if (username.isPresent()) {
				return ldioAmqpIn.registerListener(username.get(), config.getProperty(PASSWORD),
						getRemoteUrl(config), config.getProperty(TOPIC));
			} else {
				return ldioAmqpIn.registerListener(getRemoteUrl(config), config.getProperty(TOPIC));
			}
		}

		private String getContentType(ComponentProperties config) {
			return config
					.getOptionalProperty(CONTENT_TYPE)
					.map(RDFLanguages::contentTypeToLang)
					.map(Lang::getHeaderString)
					.orElse(Lang.NQUADS.getHeaderString());
		}

		private String getRemoteUrl(ComponentProperties config) {
			String remoteUrl = config.getProperty(REMOTE_URL);

			Pattern pattern = Pattern.compile(REMOTE_URL_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(remoteUrl);
			boolean matchFound = matcher.find();
			if (matchFound) {
				return remoteUrl;
			} else {
				throw new IllegalArgumentException(REMOTE_URL_REGEX_ERROR);
			}
		}
	}
}
