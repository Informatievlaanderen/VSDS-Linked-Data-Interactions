package be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.HashMap;
import java.util.Map;

public class SaslSslPlainConfigProvider {

	private static final String PLAIN_LOGIN_BASE_STRING = "org.apache.kafka.common.security.plain.PlainLoginModule" +
			" required username='%s' password='%s';";
	private static final String SASL_SSL = "SASL_SSL";
	private static final String PLAIN = "PLAIN";

	public Map<String, Object> createSaslSslPlainConfig(String user, String password) {
		final Map<String, Object> properties = new HashMap<>();
		properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SASL_SSL);
		properties.put(SaslConfigs.SASL_MECHANISM, PLAIN);
		final String plainLoginString = PLAIN_LOGIN_BASE_STRING.formatted(user, password);
		properties.put(SaslConfigs.SASL_JAAS_CONFIG, plainLoginString);
		return properties;
	}

}
