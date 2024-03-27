package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriterProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.KafkaKeyExtractor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.rdf.formatter.LdiRdfWriter.getRdfWriter;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.CONTENT_TYPE;

public class LdioKafkaOut implements LdiOutput {
	public static final String NAME = "Ldio:KafkaOut";
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final Lang lang;
	private final String topic;
	private final KafkaKeyExtractor keyExtractor;
	private final LdiRdfWriter ldiRdfWriter;

	public LdioKafkaOut(KafkaTemplate<String, String> kafkaTemplate, String topic,
			LdiRdfWriterProperties writerProperties, KafkaKeyExtractor keyExtractor) {
		this.kafkaTemplate = kafkaTemplate;
		this.topic = topic;
		this.lang = writerProperties.getLang();
		this.keyExtractor = keyExtractor;
		this.ldiRdfWriter = getRdfWriter(writerProperties);
	}

	@Override
	public void accept(Model model) {
		kafkaTemplate.send(createProducerRecord(lang, topic, model));
	}

	private ProducerRecord<String, String> createProducerRecord(Lang lang, String topic, Model model) {
		final String value = ldiRdfWriter.write(model);
		final String key = keyExtractor.getKey(model);
		final var headers = new RecordHeaders().add(CONTENT_TYPE, lang.getHeaderString().getBytes());
		return new ProducerRecord<>(topic, null, key, value, headers);
	}

}
