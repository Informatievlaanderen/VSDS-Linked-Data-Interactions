package be.vlaanderen.informatievlaanderen.ldes.ldio.listener;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;

import java.util.function.Consumer;

public class LdioKafkaInListener implements MessageListener<String, String> {
	private static final Logger log = LoggerFactory.getLogger(LdioKafkaInListener.class);
	private final Consumer<LdiAdapter.Content> messageCallback;
	private final String defaultContentType;

	public LdioKafkaInListener(String defaultContentType, Consumer<LdiAdapter.Content> callback) {
		this.defaultContentType = defaultContentType;
		this.messageCallback = callback;
	}

	@Override
	public void onMessage(ConsumerRecord<String, String> data) {
		final String contentType = determineContentType(data.headers());
		final var content = LdiAdapter.Content.of(data.value(), contentType);
		log.atDebug().log("Incoming kafka message: {}", content);
		messageCallback.accept(content);
	}

	private String determineContentType(Headers headers) {
		final Header contentTypeOnHeader = headers.lastHeader("content-type");
		return contentTypeOnHeader == null ? defaultContentType : new String(contentTypeOnHeader.value());
	}
}
