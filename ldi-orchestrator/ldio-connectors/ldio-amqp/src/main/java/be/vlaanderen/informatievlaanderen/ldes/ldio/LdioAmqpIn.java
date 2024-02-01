package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;
import jakarta.jms.JMSException;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;

public class LdioAmqpIn extends LdioInput {

	private static final Logger log = LoggerFactory.getLogger(LdioAmqpIn.class);
	private final LdioAmqpInRegistrator jmsInRegistrator;
	private final String defaultContentType;

	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param componentName
	 * @param pipelineName
	 * @param executor            Instance of the Component Executor. Allows the LDI Input to pass
	 *                            data on the pipeline
	 * @param adapter             Instance of the LDI Adapter. Facilitates transforming the input
	 *                            data to a linked data model (RDF).
	 * @param observationRegistry
	 * @param jmsInRegistrator
	 */
	protected LdioAmqpIn(String componentName, String pipelineName, ComponentExecutor executor, LdiAdapter adapter,
	                     ObservationRegistry observationRegistry, String defaultContentType, LdioAmqpInRegistrator jmsInRegistrator) {
		super(componentName, pipelineName, executor, adapter, observationRegistry);
		this.jmsInRegistrator = jmsInRegistrator;
		this.defaultContentType = defaultContentType;
	}

	public MessageListener onMessage() {
		return message -> {
			final LdiAdapter.Content content;
			try {
				content = LdiAdapter.Content.of(message.getBody(String.class), defaultContentType);
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
			log.atDebug().log("Incoming jms message: {}", content);
			processInput(content);
		};
	}


	public String registerListener(String username, String password, String remoteUrl, String topic) {
		return jmsInRegistrator.registerListener(username, password, remoteUrl, listenerEndpoint(topic));
	}

	public String registerListener(String remoteUrl, String topic) {
		return jmsInRegistrator.registerListener(remoteUrl, listenerEndpoint(topic));
	}

	private SimpleJmsListenerEndpoint listenerEndpoint(String topic) {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId(pipelineName);
		endpoint.setDestination(topic);
		endpoint.setMessageListener(onMessage());
		return endpoint;
	}
}
