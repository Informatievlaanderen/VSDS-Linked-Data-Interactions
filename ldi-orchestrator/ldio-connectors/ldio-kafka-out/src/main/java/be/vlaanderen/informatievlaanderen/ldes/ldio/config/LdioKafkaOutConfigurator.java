package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.KafkaAuthStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.SaslSslPlainConfigProvider;
import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.exceptions.SecurityProtocolNotSupportedException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.EmptyKafkaKeyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.KafkaKeyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.KafkaKeyPropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.KafkaAuthStrategy.NO_AUTH;
import static be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.KafkaAuthStrategy.SASL_SSL_PLAIN;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.BOOTSTRAP_SERVERS;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.CONTENT_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.KEY_PROPERTY_PATH;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.SASL_JAAS_PASSWORD;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.SASL_JAAS_USER;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.SECURITY_PROTOCOL;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.TOPIC;

public class LdioKafkaOutConfigurator implements LdioConfigurator {

	@Override
	public LdiComponent configure(ComponentProperties config) {
		final Lang lang = getLang(config);
		final String topic = config.getProperty(TOPIC);
		final var kafkaTemplate = createKafkaTemplate(config);
		final var kafkaKeyExtractor = determineKafkaKeyExtractor(config);
		return new LdioKafkaOut(kafkaTemplate, lang, topic, kafkaKeyExtractor);
	}

	private KafkaKeyExtractor determineKafkaKeyExtractor(ComponentProperties config) {
		final String propertyPath = config.getOptionalProperty(KEY_PROPERTY_PATH).orElse(null);
		if (propertyPath != null) {
			return new KafkaKeyPropertyPathExtractor(propertyPath);
		} else {
			return new EmptyKafkaKeyExtractor();
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
