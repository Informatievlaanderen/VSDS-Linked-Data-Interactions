package be.vlaanderen.informatievlaanderen.ldes.ldio.config.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaInConfigKeys;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioKafkaInAutoConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaIn.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.OrchestratorConfig.ORCHESTRATOR_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LdioKafkaInAutoConfigTest {

	private ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
	private static final String TOPIC = "TopicName";
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, TOPIC);

	@Test
	void shouldThrowExceptionWhenInvalidAuthConfig() {
		var configurator = new LdioKafkaInAutoConfig.LdioKafkaInConfigurator(ObservationRegistry.create());

		Map<String, String> config = getBasicConfig();
		config.put(KafkaInConfigKeys.SECURITY_PROTOCOL, "Fantasy protocol");
		ComponentProperties componentProperties = new ComponentProperties("pipelineName", NAME, config);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> configurator.configure(content -> Stream.of(), null, applicationEventPublisher, componentProperties));

		assertEquals("java.lang.IllegalArgumentException: Invalid 'security-protocol', " +
				"the supported protocols are: [NO_AUTH, SASL_SSL_PLAIN]", exception.getMessage());
	}

	@Test
	void shouldNotThrowExceptionWhenNoAuthConfig() {
		var configurator = new LdioKafkaInAutoConfig.LdioKafkaInConfigurator(ObservationRegistry.create());

		Map<String, String> config = getBasicConfig();

		assertDoesNotThrow(
				() -> configurator.configure(content -> Stream.of(), null, applicationEventPublisher, new ComponentProperties("pipelineName", NAME, config)));
	}

	@Test
	void shouldNotThrowExceptionWhenSaslSslPlain() {
		var configurator = new LdioKafkaInAutoConfig.LdioKafkaInConfigurator(ObservationRegistry.create());

		Map<String, String> config = getBasicConfig();
		config.put(KafkaInConfigKeys.SECURITY_PROTOCOL, KafkaAuthStrategy.SASL_SSL_PLAIN.name());
		config.put(KafkaInConfigKeys.SASL_JAAS_USER, "user");
		config.put(KafkaInConfigKeys.SASL_JAAS_PASSWORD, "secret");

		assertDoesNotThrow(
				() -> configurator.configure(content -> Stream.of(), null, applicationEventPublisher, new ComponentProperties("pipelineName", NAME, config)));
	}

	private Map<String, String> getBasicConfig() {
		Map<String, String> config = new HashMap<>();
		config.put(KafkaInConfigKeys.BOOTSTRAP_SERVERS, embeddedKafka.getEmbeddedKafka().getBrokersAsString());
		config.put(KafkaInConfigKeys.TOPICS, TOPIC);
		config.put(ORCHESTRATOR_NAME, "orchestrator.name");
		return config;
	}

}
