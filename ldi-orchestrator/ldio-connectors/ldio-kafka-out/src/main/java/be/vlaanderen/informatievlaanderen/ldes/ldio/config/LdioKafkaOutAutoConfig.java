package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.config.ComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldi.config.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiOutput;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFWriter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;

@Configuration
public class LdioKafkaOutAutoConfig {

	@Bean("be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut")
	public LdioConfigurator ldiKafkaOutConfigurator() {
		return config -> {
			final Lang lang = getLang(config);
			final String topic = config.getProperty("topic");
			final var kafkaTemplate = createKafkaTemplate(config.getProperty("bootstrap-servers"));
			return (LdiOutput) model -> kafkaTemplate.send(topic, toString(lang, model));
		};
	}

	private String toString(Lang lang, Model model) {
		return RDFWriter.source(model).lang(lang).build().asString();
	}

	private Lang getLang(ComponentProperties config) {
		return config
				.getOptionalProperty("content-type")
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
