package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioInputConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn;
import org.apache.jena.riot.Lang;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties()
@ComponentScan(value = "be.vlaanderen.informatievlaanderen.ldes")
public class LdioKafkaInAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn")
	public LdioKafkaInConfigurator ldioConfigurator() {
		return new LdioKafkaInConfigurator();
	}

	public static class LdioKafkaInConfigurator implements LdioInputConfigurator {

		@Autowired
		ConfigurableApplicationContext configContext;

		@Override
		public Object configure(LdiAdapter adapter,
				ComponentExecutor executor,
				ComponentProperties config) {
			String bootstrapServer = config.getProperty("bootstrap-servers");
			String groupId = config.getOptionalProperty("group-id").orElse(defineUniqueGroupName(config));
			String autoOffsetReset = config.getOptionalProperty("auto-offset-reset").orElse("earliest");
			String[] topics = config.getProperty("topics").split(",");

			LdioKafkaIn ldioKafkaIn = new LdioKafkaIn(executor, adapter, Lang.NQUADS.getHeaderString());

			ContainerProperties containerProps = new ContainerProperties(topics);
			containerProps.setMessageListener(ldioKafkaIn);
			DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<>(
					consumerProps(bootstrapServer, groupId, autoOffsetReset));
			return new KafkaMessageListenerContainer<>(cf, containerProps);
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
