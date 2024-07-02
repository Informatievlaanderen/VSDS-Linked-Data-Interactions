package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.creation.ComponentProperties;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpIn.NAME;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.OrchestratorConfig.ORCHESTRATOR_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LdioAmqpInAutoConfigTest {
	private ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);

	@Test
	void shouldThrowExceptionWhenInvalidUrlConfig() {
		var configurator = new LdioAmqpInAutoConfig.LdioJmsInConfigurator(
				new LdioAmqpInRegistrator(null), ObservationRegistry.create());

		Map<String, String> config = getBasicConfig();
		config.put(AmqpConfig.REMOTE_URL, "localhost:61616");
		ComponentProperties componentProperties = new ComponentProperties("pipelineName", NAME, config);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> configurator.configure(content -> Stream.of(), null, applicationEventPublisher, componentProperties));

		assertEquals("Property remote-url is not in format of either " +
				"'amqp[s]://hostname:port[?option=value[&option2=value...]]' or " +
				"'amqpws[s]://hostname:port[/path][?option=value[&option2=value...]]", exception.getMessage());
	}

	@Test
	void shouldNotThrowExceptionWhenNoUrlConfig() {
		var configurator = new LdioAmqpInAutoConfig.LdioJmsInConfigurator(
				new LdioAmqpInRegistrator(null), ObservationRegistry.create());

		Map<String, String> config = getBasicConfig();

		assertDoesNotThrow(
				() -> configurator.configure(content -> Stream.of(), null, applicationEventPublisher, new ComponentProperties("pipelineName", NAME, config)));
	}

	private Map<String, String> getBasicConfig() {
		Map<String, String> config = new HashMap<>();
		config.put(AmqpConfig.REMOTE_URL, "amqp://localhost:61616");
		config.put(AmqpConfig.USERNAME, "user");
		config.put(AmqpConfig.PASSWORD, "pass");
		config.put(AmqpConfig.QUEUE, "queue");
		config.put(ORCHESTRATOR_NAME, "orchestrator.name");
		return config;
	}
}
