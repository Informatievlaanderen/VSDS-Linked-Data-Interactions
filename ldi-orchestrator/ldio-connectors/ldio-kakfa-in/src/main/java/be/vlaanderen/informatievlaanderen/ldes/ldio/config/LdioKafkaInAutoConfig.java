package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.SaslSslPlainConfigProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.exceptions.SecurityProtocolNotSupportedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
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

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.AUTO_OFFSET_RESET;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.BOOTSTRAP_SERVERS;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.CONTENT_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.GROUP_ID;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.SASL_JAAS_PASSWORD;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.SASL_JAAS_USER;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.SECURITY_PROTOCOL;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.TOPICS;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig.ORCHESTRATOR_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.exception.LdiAdapterMissingException.verifyAdapterPresent;

@Configuration
public class LdioKafkaInAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn")
	public LdioKafkaInConfigurator ldioConfigurator() {
		return new LdioKafkaInConfigurator();
	}

	public static class LdioKafkaInConfigurator implements LdioInputConfigurator {

		@Override
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties config) {
			verifyAdapterPresent(config.getProperty(PIPELINE_NAME), adapter);

			LdioKafkaIn ldioKafkaIn = new LdioKafkaIn(executor, adapter, getContentType(config));
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
			return String.format("ldio-%s-%s", config.getProperty(ORCHESTRATOR_NAME),
					config.getProperty(PIPELINE_NAME));
		}
	}
}
