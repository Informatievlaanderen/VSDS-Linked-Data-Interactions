package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiComponent;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioKafkaOut;
import be.vlaanderen.informatievlaanderen.ldes.ldio.configurator.LdioConfigurator;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.EmptyKafkaKeyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.KafkaKeyExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.keyextractor.KafkaKeyPropertyPathExtractor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutAuthStrategy.NO_AUTH;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutAuthStrategy.SASL_SSL_PLAIN;
import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.KafkaOutConfigKeys.*;

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

		var authStrategy = KafkaOutAuthStrategy.from(config.getOptionalProperty(SECURITY_PROTOCOL)
				.orElse(NO_AUTH.name())).orElseThrow(this::securityProtocolNotSupported);
		if (SASL_SSL_PLAIN.equals(authStrategy)) {
			configProps.putAll(new SaslSslPlainConfigProvider().createSaslSslPlainConfig(config));
		}

		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
	}

	private IllegalArgumentException securityProtocolNotSupported() {
		return new IllegalArgumentException("Invalid '%s', the supported protocols are: %s".formatted(
				SECURITY_PROTOCOL,
				Arrays.stream(KafkaOutAuthStrategy.values()).map(Enum::name).toList()));
	}

}
