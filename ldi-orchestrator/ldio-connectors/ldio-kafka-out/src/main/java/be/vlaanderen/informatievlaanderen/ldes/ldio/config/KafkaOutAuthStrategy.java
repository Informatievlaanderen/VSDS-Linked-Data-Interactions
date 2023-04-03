package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public enum KafkaOutAuthStrategy {

	NO_AUTH, SASL_SSL_PLAIN;

	public static Optional<KafkaOutAuthStrategy> from(String s) {
		return Stream.of(values()).filter(val -> val.name().equals(StringUtils.upperCase(s))).findFirst();
	}

}
