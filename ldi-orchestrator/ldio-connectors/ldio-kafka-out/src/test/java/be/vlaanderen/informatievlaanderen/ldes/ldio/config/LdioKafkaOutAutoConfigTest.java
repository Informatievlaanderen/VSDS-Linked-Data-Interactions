package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig.ORCHESTRATOR_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LdioKafkaOutAutoConfigTest {

	@Test
	void shouldThrowExceptionWhenInvalidAuthConfig() {
		var configurator = new LdioKafkaOutAutoConfig().ldiKafkaOutConfigurator();

		Map<String, String> config = getBasicConfig();
		config.put(KafkaOutConfigKeys.SECURITY_PROTOCOL, "Fantasy protocol");
		ComponentProperties componentProperties = new ComponentProperties(config);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> configurator.configure(componentProperties));

		assertEquals("java.lang.IllegalArgumentException: Invalid 'security-protocol', " +
						"the supported protocols are: [NO_AUTH, SASL_SSL_PLAIN]", exception.getMessage());
	}

	@Test
	void shouldNotThrowExceptionWhenNoAuthConfig() {
		var configurator = new LdioKafkaOutAutoConfig().ldiKafkaOutConfigurator();

		Map<String, String> config = getBasicConfig();

		assertDoesNotThrow(() -> configurator.configure(new ComponentProperties(config)));
	}

	private Map<String, String> getBasicConfig() {
		Map<String, String> config = new HashMap<>();
		config.put(KafkaOutConfigKeys.BOOTSTRAP_SERVERS, "servers");
		config.put(KafkaOutConfigKeys.TOPIC, "topic1");
		config.put(ORCHESTRATOR_NAME, "orchestrator.name");
		config.put(PIPELINE_NAME, "pipeline.name");
		return config;
	}

	@Test
	void shouldNotThrowExceptionWhenSaslSslPlain() {
		var configurator = new LdioKafkaOutAutoConfig().ldiKafkaOutConfigurator();

		Map<String, String> config = getBasicConfig();
		config.put(KafkaOutConfigKeys.SECURITY_PROTOCOL, KafkaAuthStrategy.SASL_SSL_PLAIN.name());
		config.put(KafkaOutConfigKeys.SASL_JAAS_USER, "user");
		config.put(KafkaOutConfigKeys.SASL_JAAS_PASSWORD, "secret");

		assertDoesNotThrow(() -> configurator.configure(new ComponentProperties(config)));
	}

}