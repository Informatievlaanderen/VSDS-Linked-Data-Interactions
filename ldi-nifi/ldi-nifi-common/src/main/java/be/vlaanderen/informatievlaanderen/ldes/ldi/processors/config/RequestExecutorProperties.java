package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class RequestExecutorProperties {
	private RequestExecutorProperties() {
	}

	public static final PropertyDescriptor API_KEY_HEADER_PROPERTY = new PropertyDescriptor.Builder()
			.name("API_KEY_HEADER_PROPERTY")
			.displayName("API-KEY header property")
			.description("API header that should be used for the API key")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.defaultValue("X-API-KEY")
			.build();

	public static final PropertyDescriptor API_KEY_PROPERTY = new PropertyDescriptor.Builder()
			.name("API_KEY_PROPERTY")
			.displayName("API-KEY property")
			.description("API key that should be used to access the API.")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor OAUTH_CLIENT_ID = new PropertyDescriptor.Builder()
			.name("OAUTH_CLIENT_ID")
			.displayName("OAUTH client ID")
			.description("Client id used for Oauth2 client credentials flow")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor OAUTH_CLIENT_SECRET = new PropertyDescriptor.Builder()
			.name("OAUTH_CLIENT_SECRET")
			.displayName("OAUTH client secret")
			.description("Client secret used for Oauth2 client credentials flow")
			.sensitive(true)
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor OAUTH_TOKEN_ENDPOINT = new PropertyDescriptor.Builder()
			.name("OAUTH_TOKEN_ENDPOINT")
			.displayName("OAUTH token endpoint")
			.description("Token endpoint used for Oauth2 client credentials flow.")
			.required(false)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static final PropertyDescriptor OAUTH_SCOPE = new PropertyDescriptor.Builder()
			.name("OAUTH_SCOPE")
			.displayName("OAUTH scope")
			.description("Scope used for Oauth2 client credentials flow.")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor AUTHORIZATION_STRATEGY = new PropertyDescriptor.Builder()
			.name("AUTHORIZATION_STRATEGY")
			.displayName("Authorization strategy")
			.description("Authorization strategy for the internal http client.")
			.required(true)
			.defaultValue(AuthStrategy.NO_AUTH.name())
			.allowableValues(Arrays.stream(AuthStrategy.values()).map(Enum::name).collect(Collectors.toSet()))
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor RETRIES_ENABLED = new PropertyDescriptor.Builder()
			.name("RETRIES_ENABLED")
			.displayName("Retries enabled")
			.description("Indicates of retries are enabled when the http request fails.")
			.required(false)
			.defaultValue(TRUE.toString())
			.allowableValues(FALSE.toString(), TRUE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static final PropertyDescriptor MAX_RETRIES = new PropertyDescriptor.Builder()
			.name("MAX_RETRIES")
			.displayName("Max retries")
			.description("Indicates max number of retries when retries are enabled.")
			.required(false)
			.defaultValue(String.valueOf(5))
			.addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
			.build();

	public static final PropertyDescriptor STATUSES_TO_RETRY = new PropertyDescriptor.Builder()
			.name("STATUSES_TO_RETRY")
			.displayName("Statuses to retry")
			.description("Custom comma seperated list of http status codes that can trigger a retry in the http client.")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static String getApiKeyHeader(final ProcessContext context) {
		return context.getProperty(API_KEY_HEADER_PROPERTY).getValue();
	}

	public static String getApiKey(final ProcessContext context) {
		return context.getProperty(API_KEY_PROPERTY).getValue();
	}

	public static String getOauthClientId(final ProcessContext context) {
		return context.getProperty(OAUTH_CLIENT_ID).getValue();
	}

	public static String getOauthClientSecret(final ProcessContext context) {
		return context.getProperty(OAUTH_CLIENT_SECRET).getValue();
	}

	public static String getOauthTokenEndpoint(final ProcessContext context) {
		return context.getProperty(OAUTH_TOKEN_ENDPOINT).getValue();
	}

	public static String getOauthScope(final ProcessContext context) {
		return context.getProperty(OAUTH_SCOPE).getValue();
	}

	public static AuthStrategy getAuthorizationStrategy(final ProcessContext context) {
		final String authValue = context.getProperty(AUTHORIZATION_STRATEGY).getValue();
		return AuthStrategy
				.from(authValue)
				.orElseThrow(() -> new IllegalArgumentException("Unsupported authorization strategy: " + authValue));
	}

	public static boolean retriesEnabled(final ProcessContext context) {
		return !FALSE.equals(context.getProperty(RETRIES_ENABLED).asBoolean());
	}

	public static int getMaxRetries(final ProcessContext context) {
		return context.getProperty(MAX_RETRIES).asInteger();
	}

	public static List<Integer> getStatusesToRetry(final ProcessContext context) {
		String commaSeperatedValues = context.getProperty(STATUSES_TO_RETRY).getValue();
		if (commaSeperatedValues != null) {
			return Stream.of(commaSeperatedValues.split(",")).map(String::trim).map(Integer::parseInt).toList();
		} else {
			return new ArrayList<>();
		}
	}
}
