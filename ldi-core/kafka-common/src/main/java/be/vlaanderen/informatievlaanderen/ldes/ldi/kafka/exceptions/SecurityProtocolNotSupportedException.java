package be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.exceptions;

import be.vlaanderen.informatievlaanderen.ldes.ldi.kafka.auth.KafkaAuthStrategy;

import java.util.Arrays;

public class SecurityProtocolNotSupportedException extends IllegalArgumentException {

	public SecurityProtocolNotSupportedException(String securityProtocolKey) {
		super(new IllegalArgumentException("Invalid '%s', the supported protocols are: %s".formatted(
				securityProtocolKey,
				Arrays.stream(KafkaAuthStrategy.values()).map(Enum::name).toList())));
	}

}
