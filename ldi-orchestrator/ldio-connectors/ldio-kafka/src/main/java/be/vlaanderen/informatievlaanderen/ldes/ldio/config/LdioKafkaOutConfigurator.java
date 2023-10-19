package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.extractor.PropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldio.auth.SaslSslPlainConfigProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.exceptions.SecurityProtocolNotSupportedException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.KafkaKeyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.auth.KafkaAuthStrategy.NO_AUTH;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.auth.KafkaAuthStrategy.SASL_SSL_PLAIN;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.*;

public class LdioKafkaOutConfigurator implements LdioConfigurator {

	@Override
	public LdiComponent configure(ComponentProperties config) {
		final Lang lang = getLang(config);
		final String topic = config.getProperty(TOPIC);
		final var kafkaTemplate = createKafkaTemplate(config);
		final var kafkaKeyExtractor = determineKafkaKeyExtractor(config);
		final String frameType = config.getOptionalProperty("frame-type").orElse(null);
		return new LdioKafkaOut(kafkaTemplate, lang, topic, frameType, kafkaKeyExtractor);
	}

	private KafkaKeyExtractor determineKafkaKeyExtractor(ComponentProperties config) {
		final String propertyPath = config.getOptionalProperty(KEY_PROPERTY_PATH).orElse(null);
		if (propertyPath != null) {
			PropertyPathExtractor propertyPathExtractor = PropertyPathExtractor.from(propertyPath);

			return model -> propertyPathExtractor
					.getProperties(model)
					.stream()
					.findFirst()
					.map(RDFNode::toString)
					.orElse(null);
		} else {
			return model -> null;
		}
	}

	private Lang getLang(ComponentProperties config) {
		return config
				.getOptionalProperty(CONTENT_TYPE)
				.map(RDFLanguages::contentTypeToLang)
				.orElse(Lang.NQUADS);
	}

	private KafkaTemplate<String, String> createKafkaTemplate(ComponentProperties config) {
		final var configProps = new HashMap<String, Object>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getProperty(BOOTSTRAP_SERVERS));
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		var authStrategy = KafkaAuthStrategy.from(config.getOptionalProperty(SECURITY_PROTOCOL)
				.orElse(NO_AUTH.name()))
				.orElseThrow(() -> new SecurityProtocolNotSupportedException(SECURITY_PROTOCOL));

		if (SASL_SSL_PLAIN.equals(authStrategy)) {
			final String user = config.getProperty(SASL_JAAS_USER);
			final String password = config.getProperty(SASL_JAAS_PASSWORD);
			configProps.putAll(new SaslSslPlainConfigProvider().createSaslSslPlainConfig(user, password));
		}

		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
	}

}
