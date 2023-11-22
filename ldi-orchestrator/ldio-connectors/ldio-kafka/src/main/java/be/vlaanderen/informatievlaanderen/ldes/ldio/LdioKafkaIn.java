package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;

public class LdioKafkaIn extends LdiInput implements MessageListener<String, String> {

	private static final Logger log = LoggerFactory.getLogger(LdioKafkaIn.class);
	private final String defaultContentType;

	/**
	 * Creates a Kafka Listener with its Component Executor and LDI Adapter
	 *
	 * @param executor
	 *            Instance of the Component Executor. Allows the LDI Input to pass
	 *            data on the pipeline
	 * @param adapter
	 *            Instance of the LDI Adapter. Facilitates transforming the input
	 *            data to a linked data model (RDF).
	 */
	public LdioKafkaIn(ComponentExecutor executor, LdiAdapter adapter, String defaultContentType) {
		super(executor, adapter);
		this.defaultContentType = defaultContentType;
	}

	@Override
	public void onMessage(ConsumerRecord<String, String> data) {
		final String contentType = determineContentType(data.headers());
		final var content = LdiAdapter.Content.of(data.value(), contentType);
		log.atDebug().log("Incoming kafka message: {}", content);
		getAdapter().apply(content).forEach(getExecutor()::transformLinkedData);
	}

	private String determineContentType(Headers headers) {
		final Header contentTypeOnHeader = headers.lastHeader("content-type");
		return contentTypeOnHeader == null ? defaultContentType : new String(contentTypeOnHeader.value());
	}

}
