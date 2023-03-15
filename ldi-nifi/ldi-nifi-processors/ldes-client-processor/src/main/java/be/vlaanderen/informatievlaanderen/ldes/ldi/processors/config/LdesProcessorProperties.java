package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.validators.RDFLanguageValidator;
import ldes.client.requestexecutor.domain.valueobjects.AuthStrategy;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistanceStrategy;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public final class LdesProcessorProperties {
	/**
	 * The expected RDF format of the LDES data source
	 */
	public static final Lang DEFAULT_DATA_SOURCE_FORMAT = Lang.JSONLD;
	/**
	 * The desired RDF format for output
	 */
	public static final Lang DEFAULT_DATA_DESTINATION_FORMAT = Lang.NQUADS;

	private LdesProcessorProperties() {
	}

	public static final PropertyDescriptor DATA_SOURCE_URL = new PropertyDescriptor.Builder()
			.name("DATA_SOURCE_URL")
			.displayName("Data source url")
			.description("Url to data source")
			.required(true)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static final PropertyDescriptor DATA_SOURCE_FORMAT = new PropertyDescriptor.Builder()
			.name("DATA_SOURCE_FORMAT")
			.displayName("Data source format")
			.description("RDF format identifier of the data source")
			.required(false)
			.addValidator(new RDFLanguageValidator())
			.defaultValue(DEFAULT_DATA_SOURCE_FORMAT.getHeaderString())
			.build();

	public static final PropertyDescriptor DATA_DESTINATION_FORMAT = new PropertyDescriptor.Builder()
			.name("DATA_DESTINATION_FORMAT")
			.displayName("Data destination format")
			.description("RDF format identifier of the data destination")
			.required(false)
			.addValidator(new RDFLanguageValidator())
			.defaultValue(DEFAULT_DATA_DESTINATION_FORMAT.getHeaderString())
			.build();

	public static final PropertyDescriptor STATE_PERSISTENCE_STRATEGY = new PropertyDescriptor.Builder()
			.name("STATE_PERSISTENCE_STRATEGY")
			.displayName("How state is persisted (note that memory is volatile).")
			.required(false)
			.allowableValues(
					Arrays.stream(StatePersistanceStrategy.values()).map(Enum::name).collect(Collectors.toSet()))
			.defaultValue(StatePersistanceStrategy.MEMORY.name())
			.build();
	public static final PropertyDescriptor KEEP_STATE = new PropertyDescriptor.Builder()
			.name("KEEP_STATE")
			.displayName("Keep state when the processor is removed from the flow")
			.required(false)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(FALSE.toString())
			.build();

	public static final PropertyDescriptor STREAM_TIMESTAMP_PATH_PROPERTY = new PropertyDescriptor.Builder()
			.name("STREAM_TIMESTAMP_PATH_PROPERTY")
			.displayName("Stream TimestampPath property to FlowFile")
			.required(true)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(TRUE.toString())
			.build();

	public static final PropertyDescriptor STREAM_VERSION_OF_PROPERTY = new PropertyDescriptor.Builder()
			.name("STREAM_VERSION_OF_PROPERTY")
			.displayName("Stream VersionOf property to FlowFile")
			.required(true)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(TRUE.toString())
			.build();

	public static final PropertyDescriptor STREAM_SHAPE_PROPERTY = new PropertyDescriptor.Builder()
			.name("STREAM_SHAPE_PROPERTY")
			.displayName("Stream shape property to FlowFile")
			.required(true)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(FALSE.toString())
			.build();

	public static final PropertyDescriptor API_KEY_HEADER_PROPERTY = new PropertyDescriptor.Builder()
			.name("API_KEY_HEADER_PROPERTY")
			.displayName("API header that should be used for the API key")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.defaultValue("X-API-KEY")
			.build();

	public static final PropertyDescriptor API_KEY_PROPERTY = new PropertyDescriptor.Builder()
			.name("API_KEY_PROPERTY")
			.displayName("API key that should be used to access the API.")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor OAUTH_CLIENT_ID = new PropertyDescriptor.Builder()
			.name("OAUTH_CLIENT_ID")
			.displayName("Client id used for Oauth2 client credentials flow")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor OAUTH_CLIENT_SECRET = new PropertyDescriptor.Builder()
			.name("OAUTH_CLIENT_SECRET")
			.displayName("Client secret used for Oauth2 client credentials flow")
			.sensitive(true)
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor OAUTH_TOKEN_ENDPOINT = new PropertyDescriptor.Builder()
			.name("OAUTH_TOKEN_ENDPOINT")
			.displayName("Token endpoint used for Oauth2 client credentials flow.")
			.required(false)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static final PropertyDescriptor AUTHORIZATION_STRATEGY = new PropertyDescriptor.Builder()
			.name("AUTHORIZATION_STRATEGY")
			.displayName("Authorization strategy for the internal http client.")
			.required(true)
			.defaultValue(AuthStrategy.NO_AUTH.name())
			.allowableValues(Arrays.stream(AuthStrategy.values()).map(Enum::name).collect(Collectors.toSet()))
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor RETRIES_ENABLED = new PropertyDescriptor.Builder()
			.name("RETRIES_ENABLED")
			.displayName("Indicates of retries are enabled when the http request fails.")
			.required(true)
			.defaultValue(FALSE.toString())
			.allowableValues(FALSE.toString(), TRUE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static final PropertyDescriptor MAX_RETRIES = new PropertyDescriptor.Builder()
			.name("MAX_RETRIES")
			.displayName("Indicates max number of retries when retries are enabled.")
			.required(false)
			.defaultValue(String.valueOf(Integer.MAX_VALUE))
			.addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
			.build();

	public static String getDataSourceUrl(final ProcessContext context) {
		return context.getProperty(DATA_SOURCE_URL).getValue();
	}

	public static Lang getDataSourceFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_SOURCE_FORMAT).getValue());
	}

	public static Lang getDataDestinationFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_DESTINATION_FORMAT).getValue());
	}

	public static boolean streamTimestampPathProperty(final ProcessContext context) {
		return TRUE.equals(context.getProperty(STREAM_TIMESTAMP_PATH_PROPERTY).asBoolean());
	}

	public static boolean streamVersionOfProperty(final ProcessContext context) {
		return TRUE.equals(context.getProperty(STREAM_VERSION_OF_PROPERTY).asBoolean());
	}

	public static boolean streamShapeProperty(final ProcessContext context) {
		return TRUE.equals(context.getProperty(STREAM_SHAPE_PROPERTY).asBoolean());
	}

	public static String getApiKeyHeader(final ProcessContext context) {
		return Objects.requireNonNullElse(context.getProperty(API_KEY_HEADER_PROPERTY).getValue(), "");
	}

	public static String getApiKey(final ProcessContext context) {
		return Objects.requireNonNullElse(context.getProperty(API_KEY_PROPERTY).getValue(), "");
	}

	public static String getOauthClientId(final ProcessContext context) {
		return Objects.requireNonNullElse(context.getProperty(OAUTH_CLIENT_ID).getValue(), "");
	}

	public static String getOauthClientSecret(final ProcessContext context) {
		return Objects.requireNonNullElse(context.getProperty(OAUTH_CLIENT_SECRET).getValue(), "");
	}

	public static String getOauthTokenEndpoint(final ProcessContext context) {
		return Objects.requireNonNullElse(context.getProperty(OAUTH_TOKEN_ENDPOINT).getValue(), "");
	}

	public static AuthStrategy getAuthorizationStrategy(final ProcessContext context) {
		final String authValue = context.getProperty(AUTHORIZATION_STRATEGY).getValue();
		return AuthStrategy
				.from(authValue)
				.orElseThrow(() -> new IllegalArgumentException("Unsupported authorization strategy: " + authValue));
	}

	public static StatePersistanceStrategy getStatePersistanceStrategy(final ProcessContext context) {
		return StatePersistanceStrategy.valueOf(context.getProperty(STATE_PERSISTENCE_STRATEGY).getValue());
	}

	public static boolean stateKept(final ProcessContext context) {
		return TRUE.equals(context.getProperty(KEEP_STATE).asBoolean());
	}

	public static boolean retriesEnabled(final ProcessContext context) {
		return TRUE.equals(context.getProperty(RETRIES_ENABLED).asBoolean());
	}

	public static int getMaxRetries(final ProcessContext context) {
		return context.getProperty(MAX_RETRIES).asInteger();
	}

}
