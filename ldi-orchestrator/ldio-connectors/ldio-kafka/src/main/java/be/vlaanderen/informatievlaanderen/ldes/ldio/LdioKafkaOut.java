package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.KafkaKeyExtractor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.kafka.core.KafkaTemplate;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.CONTENT_TYPE;

public class LdioKafkaOut implements LdiOutput {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final Lang lang;
	private final String topic;
	private final KafkaKeyExtractor keyExtractor;

	public LdioKafkaOut(KafkaTemplate<String, String> kafkaTemplate, Lang lang, String topic,
			KafkaKeyExtractor keyExtractor) {
		this.kafkaTemplate = kafkaTemplate;
		this.lang = lang;
		this.topic = topic;
		this.keyExtractor = keyExtractor;
	}

	@Override
	public void accept(Model model) {
		kafkaTemplate.send(createProducerRecord(lang, topic, model));
	}

	private ProducerRecord<String, String> createProducerRecord(Lang lang, String topic, Model model) {
		final String value = mapModelToString(lang, model);
		final String key = keyExtractor.getKey(model);
		final var headers = new RecordHeaders().add(CONTENT_TYPE, lang.getHeaderString().getBytes());
		return new ProducerRecord<>(topic, null, key, value, headers);
	}

	private String mapModelToString(Lang lang, Model model) {
		return RDFWriter.source(model).lang(lang).build().asString();
	}

}
