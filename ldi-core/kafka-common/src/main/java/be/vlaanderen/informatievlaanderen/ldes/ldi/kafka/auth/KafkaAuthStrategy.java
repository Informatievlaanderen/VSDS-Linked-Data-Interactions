package be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public enum KafkaAuthStrategy {

	NO_AUTH, SASL_SSL_PLAIN;

	public static Optional<KafkaAuthStrategy> from(String s) {
		return Stream.of(values()).filter(val -> val.name().equals(StringUtils.upperCase(s))).findFirst();
	}

}
