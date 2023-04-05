package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys.*;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig.ORCHESTRATOR_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;

@Configuration
public class LdioKafkaInAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn")
	public LdioKafkaInConfigurator ldioConfigurator() {
		return new LdioKafkaInConfigurator();
	}

	public static class LdioKafkaInConfigurator implements LdioInputConfigurator {

		@Override
		public Object configure(LdiAdapter adapter, ComponentExecutor executor, ComponentProperties config) {
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

			var authStrategy = KafkaInAuthStrategy.from(config.getOptionalProperty(SECURITY_PROTOCOL)
					.orElse(KafkaInAuthStrategy.NO_AUTH.name())).orElseThrow(this::securityProtocolNotSupported);
			if (KafkaInAuthStrategy.SASL_SSL_PLAIN.equals(authStrategy)) {
				props.putAll(getSaslSslPlainConfig(config));
			}
			return props;
		}

		private Map<String, Object> getSaslSslPlainConfig(ComponentProperties config) {
			final Map<String, Object> props = new HashMap<>();
			props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
			props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
			String plainLoginString = ("org.apache.kafka.common.security.plain.PlainLoginModule" +
					" required username='%s' password='%s';")
					.formatted(config.getProperty(SASL_JAAS_USER), config.getProperty(SASL_JAAS_PASSWORD));
			props.put(SaslConfigs.SASL_JAAS_CONFIG, plainLoginString);
			return props;
		}

		private IllegalArgumentException securityProtocolNotSupported() {
			return new IllegalArgumentException("Invalid '%s', the supported protocols are: %s".formatted(
					SECURITY_PROTOCOL,
					Arrays.stream(KafkaInAuthStrategy.values()).map(Enum::name).toList()));
		}

		private String defineUniqueGroupName(ComponentProperties config) {
			return String.format("ldio-%s-%s", config.getProperty(ORCHESTRATOR_NAME),
					config.getProperty(PIPELINE_NAME));
		}
	}
}
