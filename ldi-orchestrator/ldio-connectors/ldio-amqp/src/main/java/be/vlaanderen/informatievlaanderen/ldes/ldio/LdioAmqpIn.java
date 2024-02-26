package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.JmsConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioAmqpInRegistrator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.InvalidAmqpMessageException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.AmqpConfig.CONTENT_TYPE_HEADER;

public class LdioAmqpIn extends LdioInput implements MessageListener {
	public static final String NAME = "Ldio:AmqpIn";
	private static final Logger log = LoggerFactory.getLogger(LdioAmqpIn.class);
	private final LdioAmqpInRegistrator ldioAmqpInRegistrator;
	private final String listenerId;
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
	public LdioAmqpIn(String pipelineName, ComponentExecutor executor, LdiAdapter adapter,
						 String defaultContentType, JmsConfig jmsConfig, LdioAmqpInRegistrator jmsInRegistrator,
						 ObservationRegistry observationRegistry, ApplicationEventPublisher applicationEventPublisher) {
		super(NAME, pipelineName, executor, adapter, observationRegistry, applicationEventPublisher);
		this.defaultContentType = defaultContentType;
		SimpleJmsListenerEndpoint endpoint = listenerEndpoint(jmsConfig.queue());
		ldioAmqpInRegistrator = jmsInRegistrator;
		listenerId = endpoint.getId();
		jmsInRegistrator.registerListener(jmsConfig, endpoint);
	}

	@Override
	public void onMessage(Message message) {
		final LdiAdapter.Content content;
		try {
			String contentTypeProperty = message.getStringProperty(CONTENT_TYPE_HEADER);
			String contentType = contentTypeProperty != null ? contentTypeProperty : defaultContentType;
			content = LdiAdapter.Content.of(message.getBody(String.class), contentType);
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

	@Override
	public void shutdown() {
		ldioAmqpInRegistrator.stopListener(listenerId);
	}

	@Override
	protected void resume() {
		ldioAmqpInRegistrator.startListener(listenerId);
	}

	@Override
	protected void pause() {
		ldioAmqpInRegistrator.stopListener(listenerId);
	}
}
