package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
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

	public LdioKafkaOut(KafkaTemplate<String, String> kafkaTemplate, Lang lang, String topic) {
		this.kafkaTemplate = kafkaTemplate;
		this.lang = lang;
		this.topic = topic;
	}

	@Override
	public void accept(Model model) {
		kafkaTemplate.send(createProducerRecord(lang, topic, model));
	}

	private ProducerRecord<String, String> createProducerRecord(Lang lang, String topic, Model model) {
		final String message = toString(lang, model);
		final var headers = new RecordHeaders().add(CONTENT_TYPE, lang.getHeaderString().getBytes());
		return new ProducerRecord<>(topic, null, (String) null, message, headers);
	}

	private String toString(Lang lang, Model model) {
		return RDFWriter.source(model).lang(lang).build().asString();
	}

}
