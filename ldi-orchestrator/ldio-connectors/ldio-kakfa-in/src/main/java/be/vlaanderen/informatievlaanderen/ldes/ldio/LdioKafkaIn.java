package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class LdioKafkaIn extends LdiInput implements MessageListener<String, String> {
	private final String contentType;

	/**
	 * Creates a LdiInput with its Component Executor and LDI Adapter
	 *
	 * @param executor
	 *            Instance of the Component Executor. Allows the LDI Input to pass
	 *            data on the pipeline
	 * @param adapter
	 *            Instance of the LDI Adapter. Facilitates transforming the input
	 *            data to a linked data model (RDF).
	 */
	public LdioKafkaIn(ComponentExecutor executor, LdiAdapter adapter, String contentType) {
		super(executor, adapter);
		this.contentType = contentType;
	}

	@Override
	public void onMessage(ConsumerRecord<String, String> data) {
		getAdapter().apply(LdiAdapter.Content.of(data.value(), contentType))
				.forEach(getExecutor()::transformLinkedData);
	}
}
