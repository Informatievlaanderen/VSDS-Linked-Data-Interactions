package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.OrchestratorConfig.ORCHESTRATOR_NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.PipelineConfig.PIPELINE_NAME;
import static org.junit.jupiter.api.Assertions.*;

class LdioKafkaInAutoConfigTest {

	@Test
	void shouldThrowExceptionWhenInvalidAuthConfig() {
		var configurator = new LdioKafkaInAutoConfig.LdioKafkaInConfigurator();

		Map<String, String> config = getBasicConfig();
		config.put(KafkaInConfigKeys.SECURITY_PROTOCOL, "Fantasy protocol");
		ComponentProperties componentProperties = new ComponentProperties(config);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> configurator.configure((content) -> Stream.of(), null, componentProperties));

		assertEquals("java.lang.IllegalArgumentException: Invalid 'security-protocol', " +
						"the supported protocols are: [NO_AUTH, SASL_SSL_PLAIN]", exception.getMessage());
	}

	@Test
	void shouldNotThrowExceptionWhenNoAuthConfig() {
		var configurator = new LdioKafkaInAutoConfig.LdioKafkaInConfigurator();

		Map<String, String> config = getBasicConfig();

		assertDoesNotThrow(
				() -> configurator.configure((content) -> Stream.of(), null, new ComponentProperties(config)));
	}

	@Test
	void shouldNotThrowExceptionWhenSaslSslPlain() {
		var configurator = new LdioKafkaInAutoConfig.LdioKafkaInConfigurator();

		Map<String, String> config = getBasicConfig();
		config.put(KafkaInConfigKeys.SECURITY_PROTOCOL, KafkaAuthStrategy.SASL_SSL_PLAIN.name());
		config.put(KafkaInConfigKeys.SASL_JAAS_USER, "user");
		config.put(KafkaInConfigKeys.SASL_JAAS_PASSWORD, "secret");

		assertDoesNotThrow(
				() -> configurator.configure((content) -> Stream.of(), null, new ComponentProperties(config)));
	}

	private Map<String, String> getBasicConfig() {
		Map<String, String> config = new HashMap<>();
		config.put(KafkaInConfigKeys.BOOTSTRAP_SERVERS, "servers");
		config.put(KafkaInConfigKeys.TOPICS, "topic1");
		config.put(ORCHESTRATOR_NAME, "orchestrator.name");
		config.put(PIPELINE_NAME, "pipeline.name");
		return config;
	}

}