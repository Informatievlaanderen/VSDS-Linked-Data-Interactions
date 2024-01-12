package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.validators.RDFLanguageValidator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistenceStrategy;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
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
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

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
					Arrays.stream(StatePersistenceStrategy.values()).map(Enum::name).collect(Collectors.toSet()))
			.defaultValue(StatePersistenceStrategy.MEMORY.name())
			.build();

	public static final PropertyDescriptor POSTGRES_URL = new PropertyDescriptor.Builder()
			.name("POSTGRES_URL")
			.displayName("Postgres database url")
			.description("Postgres database url formatted as \"jdbc:postgresql://localhost:5432/postgres\"")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor POSTGRES_USERNAME = new PropertyDescriptor.Builder()
			.name("POSTGRES_USERNAME")
			.displayName("Postgres database username")
			.description("Username used to connect to the postgres database")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();

	public static final PropertyDescriptor POSTGRES_PASSWORD = new PropertyDescriptor.Builder()
			.name("POSTGRES_PASSWORD")
			.displayName("Postgres database password")
			.description("Password used to connect to the postgres database")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.build();
	public static final PropertyDescriptor KEEP_STATE = new PropertyDescriptor.Builder()
			.name("KEEP_STATE")
			.displayName("Keep state when the processor is removed from the flow")
			.required(false)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(FALSE.toString())
			.build();

	public static final PropertyDescriptor TIMESTAMP_PATH = new PropertyDescriptor.Builder()
			.name("TIMESTAMP_PATH")
			.displayName("Property path determining the timestamp used to order the members within a fragment")
			.required(true)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
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
			.required(false)
			.defaultValue(TRUE.toString())
			.allowableValues(FALSE.toString(), TRUE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static final PropertyDescriptor MAX_RETRIES = new PropertyDescriptor.Builder()
			.name("MAX_RETRIES")
			.displayName("Indicates max number of retries when retries are enabled.")
			.required(false)
			.defaultValue(String.valueOf(5))
			.addValidator(StandardValidators.POSITIVE_INTEGER_VALIDATOR)
			.build();

	public static final PropertyDescriptor STATUSES_TO_RETRY = new PropertyDescriptor.Builder()
			.name("STATUSES_TO_RETRY")
			.displayName(
					"Custom comma seperated list of http status codes that can trigger a retry in the http client.")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
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

	public static Property getTimestampPath(final ProcessContext context) {
		return createProperty(context.getProperty(DATA_DESTINATION_FORMAT).getValue());
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

	public static AuthStrategy getAuthorizationStrategy(final ProcessContext context) {
		final String authValue = context.getProperty(AUTHORIZATION_STRATEGY).getValue();
		return AuthStrategy
				.from(authValue)
				.orElseThrow(() -> new IllegalArgumentException("Unsupported authorization strategy: " + authValue));
	}

	public static StatePersistenceStrategy getStatePersistenceStrategy(final ProcessContext context) {
		return StatePersistenceStrategy.valueOf(context.getProperty(STATE_PERSISTENCE_STRATEGY).getValue());
	}

	public static boolean stateKept(final ProcessContext context) {
		return TRUE.equals(context.getProperty(KEEP_STATE).asBoolean());
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

	public static String getPostgresUrl(final ProcessContext context) {
		return context.getProperty(POSTGRES_URL).getValue();
	}

	public static String getPostgresUsername(final ProcessContext context) {
		return context.getProperty(POSTGRES_USERNAME).getValue();
	}

	public static String getPostgresPassword(final ProcessContext context) {
		return context.getProperty(POSTGRES_PASSWORD).getValue();
	}

}
