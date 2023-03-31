package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFWriter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.BOOTSTRAP_SERVERS;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.CONTENT_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.TOPIC;

@Configuration
public class LdioKafkaOutAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut")
	public LdioConfigurator ldiKafkaOutConfigurator() {
		return config -> {
			final Lang lang = getLang(config);
			final String topic = config.getProperty(TOPIC);
			final var kafkaTemplate = createKafkaTemplate(config.getProperty(BOOTSTRAP_SERVERS));
			return (LdiOutput) model -> kafkaTemplate.send(createProducerRecord(lang, topic, model));
		};
	}

	private ProducerRecord<String, String> createProducerRecord(Lang lang, String topic, Model model) {
		final String message = toString(lang, model);
		final var headers = new RecordHeaders().add(CONTENT_TYPE, lang.getHeaderString().getBytes());
		return new ProducerRecord<>(topic, null, (String) null, message, headers);
	}

	private String toString(Lang lang, Model model) {
		return RDFWriter.source(model).lang(lang).build().asString();
	}

	private Lang getLang(ComponentProperties config) {
		return config
				.getOptionalProperty(CONTENT_TYPE)
				.map(RDFLanguages::contentTypeToLang)
				.orElse(Lang.NQUADS);
	}

	private KafkaTemplate<String, String> createKafkaTemplate(String bootstrapServer) {
		final var configProps = new HashMap<String, Object>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
	}

}
