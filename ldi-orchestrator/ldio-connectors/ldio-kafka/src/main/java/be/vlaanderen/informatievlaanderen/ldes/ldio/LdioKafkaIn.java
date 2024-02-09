package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;

public class LdioKafkaIn extends LdioInput implements MessageListener<String, String> {

	private static final Logger log = LoggerFactory.getLogger(LdioKafkaIn.class);
	private final String defaultContentType;

	/**
	 * Creates a Kafka Listener with its Component Executor and LDI Adapter
	 *
	 * @param componentName References the Fully Qualified name of the LDIO component
	 * @param pipelineName  Name of the LDIO pipeline to which the LDI Input belongs
	 * @param executor      Instance of the Component Executor. Allows the LDI Input to pass
	 *                      data on the pipeline
	 * @param adapter       Instance of the LDI Adapter. Facilitates transforming the input
	 *                      data to a linked data model (RDF).
	 */
	public LdioKafkaIn(String componentName, String pipelineName, ComponentExecutor executor, LdiAdapter adapter, ObservationRegistry observationRegistry, String defaultContentType) {
		super(componentName, pipelineName, executor, adapter, observationRegistry);
		this.defaultContentType = defaultContentType;
	}

	@Override
	public void onMessage(ConsumerRecord<String, String> data) {
		final String contentType = determineContentType(data.headers());
		final var content = LdiAdapter.Content.of(data.value(), contentType);
		log.atDebug().log("Incoming kafka message: {}", content);
		processInput(content);
	}

	private String determineContentType(Headers headers) {
		final Header contentTypeOnHeader = headers.lastHeader("content-type");
		return contentTypeOnHeader == null ? defaultContentType : new String(contentTypeOnHeader.value());
	}

}
