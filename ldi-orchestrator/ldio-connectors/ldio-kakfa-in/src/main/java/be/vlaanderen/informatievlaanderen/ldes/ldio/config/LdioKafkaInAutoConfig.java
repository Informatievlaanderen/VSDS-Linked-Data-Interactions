package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn;
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

@Configuration
public class LdioKafkaInAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn")
	public LdioKafkaInConfigurator ldioConfigurator() {
		return new LdioKafkaInConfigurator();
	}

	public static class LdioKafkaInConfigurator implements LdioInputConfigurator {

		@Override
		public Object configure(LdiAdapter adapter,
				ComponentExecutor executor,
				ComponentProperties config) {
			String bootstrapServer = config.getProperty(KafkaInConfigKeys.BOOTSTRAP_SERVERS);
			String groupId = config.getOptionalProperty(KafkaInConfigKeys.GROUP_ID)
					.orElse(defineUniqueGroupName(config));
			String autoOffsetReset = config.getOptionalProperty(KafkaInConfigKeys.AUTO_OFFSET_RESET).orElse("earliest");
			String[] topics = config.getProperty(KafkaInConfigKeys.TOPICS).split(",");
			String contentType = getContentType(config);

			LdioKafkaIn ldioKafkaIn = new LdioKafkaIn(executor, adapter, contentType);

			ContainerProperties containerProps = new ContainerProperties(topics);
			containerProps.setMessageListener(ldioKafkaIn);
			var consumerFactory = new DefaultKafkaConsumerFactory<>(
					consumerProps(bootstrapServer, groupId, autoOffsetReset));
			return new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
		}

		private String getContentType(ComponentProperties config) {
			return config
					.getOptionalProperty(KafkaInConfigKeys.CONTENT_TYPE)
					.map(RDFLanguages::contentTypeToLang)
					.map(Lang::getHeaderString)
					.orElse(Lang.NQUADS.getHeaderString());
		}

		private Map<String, Object> consumerProps(String bootstrapServers, String groupId, String offsetReset) {
			Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			return props;
		}

		private String defineUniqueGroupName(ComponentProperties config) {
			return String.format("ldio-%s-%s", config.getProperty("orchestrator.name"),
					config.getProperty("pipeline.name"));
		}
	}
}
