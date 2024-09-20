package be.vlaanderen.informatievlaanderen.ldes.ldi.valueobjects;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public enum StatePersistenceStrategy {

	H2;

	public static Optional<StatePersistenceStrategy> from(String s) {
		return Stream.of(values()).filter(val -> val.name().equals(StringUtils.upperCase(s))).findFirst();
	}

}
