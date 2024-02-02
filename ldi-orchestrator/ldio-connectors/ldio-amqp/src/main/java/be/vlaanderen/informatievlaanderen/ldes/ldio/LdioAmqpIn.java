package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.JmsConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.InvalidAmqpMessageException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

public class LdioAmqpIn extends LdioInput implements MessageListener {
	public static final String NAME = "be.vlaanderen.informatievlaanderen.ldes.ldio.LdioAmqpIn";
	private static final Logger log = LoggerFactory.getLogger(LdioAmqpIn.class);
	private final String defaultContentType;

	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param pipelineName        Unique identifier for the pipeline.
	 * @param executor            Instance of the Component Executor. Allows the LDI Input to pass
	 *                            data on the pipeline
	 * @param adapter             Instance of the LDI Adapter. Facilitates transforming the input
	 *                            data to a linked data model (RDF).
	 * @param jmsConfig           Configuration class containing the necessary info to spin up a listener
	 * @param jmsInRegistrator    Global service to maintain JMS listeners.
	 * @param observationRegistry
	 */
	protected LdioAmqpIn(String pipelineName, ComponentExecutor executor, LdiAdapter adapter,
	                     String defaultContentType, JmsConfig jmsConfig, LdioAmqpInRegistrator jmsInRegistrator, ObservationRegistry observationRegistry) {
		super(NAME, pipelineName, executor, adapter, observationRegistry);
		this.defaultContentType = defaultContentType;
		jmsInRegistrator.registerListener(jmsConfig, listenerEndpoint(jmsConfig.queue()));
	}

	@Override
	public void onMessage(Message message) {
		final LdiAdapter.Content content;
		try {
			content = LdiAdapter.Content.of(message.getBody(String.class), defaultContentType);
		} catch (JMSException e) {
			throw new InvalidAmqpMessageException(e);
		}
		log.atDebug().log("Incoming jms message: {}", content);
		processInput(content);
	}

	private SimpleJmsListenerEndpoint listenerEndpoint(String queue) {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId(pipelineName);
		endpoint.setDestination(queue);
		endpoint.setMessageListener(this);
		return endpoint;
	}
}
