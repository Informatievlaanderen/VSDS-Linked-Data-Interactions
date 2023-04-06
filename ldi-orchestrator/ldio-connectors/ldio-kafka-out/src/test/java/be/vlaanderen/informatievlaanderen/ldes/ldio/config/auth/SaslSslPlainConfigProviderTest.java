package be.vlaanderen.informatievlaanderen.ldes.ldio.config.auth;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.SASL_JAAS_PASSWORD;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.SASL_JAAS_USER;
import static org.junit.jupiter.api.Assertions.*;

class SaslSslPlainConfigProviderTest {

	private SaslSslPlainConfigProvider provider;

	@BeforeEach
	void setUp() {
		provider = new SaslSslPlainConfigProvider();
	}

	@Test
	void createSaslSslPlainConfig() {
		var properties = new ComponentProperties(Map.of(SASL_JAAS_USER, "user", SASL_JAAS_PASSWORD, "secret"));
		Map<String, ?> saslSslPlainConfig = provider.createSaslSslPlainConfig(properties);

		assertEquals("SASL_SSL", saslSslPlainConfig.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
		assertEquals("PLAIN", saslSslPlainConfig.get(SaslConfigs.SASL_MECHANISM));
		assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule" +
				" required username='user' password='secret';",
				saslSslPlainConfig.get(SaslConfigs.SASL_JAAS_CONFIG));
	}

}