package be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.HashMap;
import java.util.Map;

public class SaslSslPlainConfigProvider {

	public Map<String, Object> createSaslSslPlainConfig(String user, String password) {
		final Map<String, Object> properties = new HashMap<>();
		properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
		properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
		String plainLoginString = ("org.apache.kafka.common.security.plain.PlainLoginModule" +
				" required username='%s' password='%s';").formatted(user, password);
		properties.put(SaslConfigs.SASL_JAAS_CONFIG, plainLoginString);
		return properties;
	}

}
