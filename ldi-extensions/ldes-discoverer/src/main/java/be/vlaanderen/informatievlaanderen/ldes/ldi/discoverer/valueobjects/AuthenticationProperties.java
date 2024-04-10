package be.vlaanderen.informatievlaanderen.ldes.ldi.discoverer.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import org.springframework.boot.ApplicationArguments;

import java.util.Optional;

public class AuthenticationProperties {
	private static final String AUTH_TYPE = "auth-type";
	private static final String API_KEY = "api-key";
	private static final String API_KEY_HEADER = "api-key-header";
	private static final String DEFAULT_API_KEY_HEADER = "X-API-KEY";
	private static final String CLIENT_ID = "client-id";
	private static final String CLIENT_SECRET = "client-secret";
	private static final String TOKEN_ENDPOINT = "token-endpoint";
	private static final String SCOPE = "scope";
	private final Arguments arguments;

	public AuthenticationProperties(ApplicationArguments arguments) {
		this.arguments = new Arguments(arguments);
	}


	public AuthStrategy getAuthStrategy() {
		return arguments.getArgumentValues(AUTH_TYPE).stream().findFirst()
				.map(AuthStrategy::from)
				.map(authStrategy -> authStrategy.orElseThrow(() -> new UnsupportedOperationException("Requested authentication not available.")))
				.orElse(AuthStrategy.NO_AUTH);
	}

	public String getApiKeyHeader() {
		return arguments.getArgumentValues(API_KEY_HEADER).stream().findFirst().orElse(DEFAULT_API_KEY_HEADER);
	}

	public String getApiKey() {
		return arguments.getRequiredValue(API_KEY);
	}

	public String getClientId() {
		return arguments.getRequiredValue(CLIENT_ID);
	}

	public String getClientSecret() {
		return arguments.getRequiredValue(CLIENT_SECRET);
	}

	public String getTokenEndpoint() {
		return arguments.getRequiredValue(TOKEN_ENDPOINT);
	}

	public Optional<String> getAuthScope() {
		return arguments.getArgumentValues(SCOPE).stream().findFirst();
	}

}
