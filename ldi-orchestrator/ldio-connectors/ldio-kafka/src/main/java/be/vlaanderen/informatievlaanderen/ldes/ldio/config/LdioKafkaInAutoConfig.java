package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.SaslSslPlainConfigProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.SecurityProtocolNotSupportedException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig.ORCHESTRATOR_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException.verifyAdapterPresent;

@Configuration
public class LdioKafkaInAutoConfig {

	@SuppressWarnings("java:S6830")
	@Bean(NAME)
	public LdioKafkaInConfigurator ldioConfigurator(ObservationRegistry observationRegistry) {
		return new LdioKafkaInConfigurator(observationRegistry);
	}

	public static class LdioKafkaInConfigurator implements LdioInputConfigurator {
		private final ObservationRegistry observationRegistry;

		public LdioKafkaInConfigurator(ObservationRegistry observationRegistry) {
			this.observationRegistry = observationRegistry;
		}

		@Override
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties config) {
			String pipelineName = config.getPipelineName();
			verifyAdapterPresent(pipelineName, adapter);

			LdioKafkaIn ldioKafkaIn = new LdioKafkaIn(NAME, pipelineName, executor, adapter, observationRegistry, getContentType(config));
			var consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerConfig(config));
			ContainerProperties containerProps = new ContainerProperties(config.getProperty(TOPICS).split(","));
			containerProps.setMessageListener(ldioKafkaIn);
			return new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
		}

		private String getContentType(ComponentProperties config) {
			return config
					.getOptionalProperty(CONTENT_TYPE)
					.map(RDFLanguages::contentTypeToLang)
					.map(Lang::getHeaderString)
					.orElse(Lang.NQUADS.getHeaderString());
		}

		private Map<String, Object> getConsumerConfig(ComponentProperties config) {
			final Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getProperty(BOOTSTRAP_SERVERS));
			props.put(ConsumerConfig.GROUP_ID_CONFIG,
					config.getOptionalProperty(GROUP_ID).orElse(defineUniqueGroupName(config)));
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
					config.getOptionalProperty(AUTO_OFFSET_RESET).orElse("earliest"));
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

			var authStrategy = KafkaAuthStrategy.from(config.getOptionalProperty(SECURITY_PROTOCOL)
							.orElse(KafkaAuthStrategy.NO_AUTH.name()))
					.orElseThrow(() -> new SecurityProtocolNotSupportedException(SECURITY_PROTOCOL));

			if (KafkaAuthStrategy.SASL_SSL_PLAIN.equals(authStrategy)) {
				final String user = config.getProperty(SASL_JAAS_USER);
				final String password = config.getProperty(SASL_JAAS_PASSWORD);
				props.putAll(new SaslSslPlainConfigProvider().createSaslSslPlainConfig(user, password));
			}

			return props;
		}

		private String defineUniqueGroupName(ComponentProperties config) {
			return String.format("ldio-%s-%s", config.getProperty(ORCHESTRATOR_NAME), config.getPipelineName());
		}
	}
}
