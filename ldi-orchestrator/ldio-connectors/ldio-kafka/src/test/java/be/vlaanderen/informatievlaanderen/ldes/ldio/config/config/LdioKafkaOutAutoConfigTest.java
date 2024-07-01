package be.vlaanderen.informatievlaanderen.ldes.ldio.config.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioKafkaOutAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.configurator.ComponentProperties;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.OrchestratorConfig.ORCHESTRATOR_NAME;
import static org.junit.jupiter.api.Assertions.*;

class LdioKafkaOutAutoConfigTest {

	@Test
	void shouldThrowExceptionWhenInvalidAuthConfig() {
		var configurator = new LdioKafkaOutAutoConfig().ldiKafkaOutConfigurator();

		Map<String, String> config = getBasicConfig();
		config.put(KafkaOutConfigKeys.SECURITY_PROTOCOL, "Fantasy protocol");
		ComponentProperties componentProperties = new ComponentProperties("pipelineName", NAME, config);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> configurator.configure(componentProperties));

		assertEquals("java.lang.IllegalArgumentException: Invalid 'security-protocol', " +
				"the supported protocols are: [NO_AUTH, SASL_SSL_PLAIN]", exception.getMessage());
	}

	@Test
	void shouldNotThrowExceptionWhenNoAuthConfig() {
		var configurator = new LdioKafkaOutAutoConfig().ldiKafkaOutConfigurator();

		Map<String, String> config = getBasicConfig();

		assertDoesNotThrow(() -> configurator.configure(new ComponentProperties("pipelineName", NAME, config)));
	}

	private Map<String, String> getBasicConfig() {
		Map<String, String> config = new HashMap<>();
		config.put(KafkaOutConfigKeys.BOOTSTRAP_SERVERS, "servers");
		config.put(KafkaOutConfigKeys.TOPIC, "topic1");
		config.put(ORCHESTRATOR_NAME, "orchestrator.name");
		return config;
	}

	@Test
	void shouldNotThrowExceptionWhenSaslSslPlain() {
		var configurator = new LdioKafkaOutAutoConfig().ldiKafkaOutConfigurator();

		Map<String, String> config = getBasicConfig();
		config.put(KafkaOutConfigKeys.SECURITY_PROTOCOL, KafkaAuthStrategy.SASL_SSL_PLAIN.name());
		config.put(KafkaOutConfigKeys.SASL_JAAS_USER, "user");
		config.put(KafkaOutConfigKeys.SASL_JAAS_PASSWORD, "secret");

		assertDoesNotThrow(() -> configurator.configure(new ComponentProperties("pipelineName", NAME, config)));
	}

}