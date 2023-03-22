package ldes.client.requestexecutor.domain.valueobjects;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public enum AuthStrategy {

	NO_AUTH, API_KEY, OAUTH2_CLIENT_CREDENTIALS;

	public static Optional<AuthStrategy> from(String s) {
		return Stream.of(values()).filter(val -> val.name().equals(StringUtils.upperCase(s))).findFirst();
	}

}
