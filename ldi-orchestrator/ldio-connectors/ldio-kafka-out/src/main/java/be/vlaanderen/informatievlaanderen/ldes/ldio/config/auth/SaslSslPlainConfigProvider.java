package be.vlaanderen.informatievlaanderen.ldes.ldio.config.auth;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.SASL_JAAS_PASSWORD;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.SASL_JAAS_USER;

public class SaslSslPlainConfigProvider {

	public Map<String, ?> createSaslSslPlainConfig(ComponentProperties config) {
		final Map<String, Object> properties = new HashMap<>();
		properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		String plainLoginString = ("org.apache.kafka.common.security.plain.PlainLoginModule" +
				" required username='%s' password='%s';")
				.formatted(config.getProperty(SASL_JAAS_USER), config.getProperty(SASL_JAAS_PASSWORD));
		properties.put(SaslConfigs.SASL_JAAS_CONFIG, plainLoginString);
		return properties;
	}

}
