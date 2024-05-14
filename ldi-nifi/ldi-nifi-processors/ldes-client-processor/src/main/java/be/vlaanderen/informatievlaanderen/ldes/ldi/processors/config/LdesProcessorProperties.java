package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.validators.RDFLanguageValidator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.repository.valueobjects.StatePersistenceStrategy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.AuthStrategy;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public static final PropertyDescriptor DATA_SOURCE_URLS = new PropertyDescriptor.Builder()
			.name("DATA_SOURCE_URLS")
			.displayName("Data source urls")
			.description("Comma separated list of ldes endpoints. Must be part of same view of an LDES.")
			.required(true)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
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

	public static final PropertyDescriptor SQLITE_DIRECTORY = new PropertyDescriptor.Builder()
			.name("SQLITE_DIRECTORY")
			.displayName("Sqlite database directory")
			.description("Sqlite database directory where the '.db' file can be stored")
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
			.required(false)
			.addValidator(Validator.VALID)
			.defaultValue("http://www.w3.org/ns/prov#generatedAtTime")
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

	public static final PropertyDescriptor OAUTH_SCOPE = new PropertyDescriptor.Builder()
			.name("OAUTH_SCOPE")
			.displayName("Scope used for Oauth2 client credentials flow.")
			.required(false)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
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

	public static final PropertyDescriptor USE_VERSION_MATERIALISATION = new PropertyDescriptor.Builder()
			.name("USE_VERSION_MATERIALISATION")
			.displayName("Indicates of retries are enabled when the http request fails.")
			.required(false)
			.defaultValue(FALSE.toString())
			.allowableValues(FALSE.toString(), TRUE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static final PropertyDescriptor RESTRICT_TO_MEMBERS = new PropertyDescriptor.Builder()
			.name("RESTRICT_TO_MEMBERS")
			.displayName("Restrict output to members")
			.description("When enabled, only the member and the blank nodes references are included.")
			.required(false)
			.defaultValue(FALSE.toString())
			.allowableValues(FALSE.toString(), TRUE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static final PropertyDescriptor VERSION_OF_PROPERTY = new PropertyDescriptor.Builder()
			.name("VERSION_OF_PROPERTY")
			.required(true)
			.defaultValue("http://purl.org/dc/terms/isVersionOf")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.addValidator(StandardValidators.URI_VALIDATOR)
			.build();

	public static final PropertyDescriptor USE_EXACTLY_ONCE_FILTER = new PropertyDescriptor.Builder()
			.name("USE_EXACTLY_ONCE_FILTER")
			.displayName("Use filter so members are outputted exactly once")
			.required(false)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(TRUE.toString())
			.build();

	public static final PropertyDescriptor USE_LATEST_STATE_FILTER = new PropertyDescriptor.Builder()
			.name("USE_LATEST_STATE_FILTER")
			.displayName("Use filter to only process the latest state and so all older versions are ignored")
			.required(false)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(TRUE.toString())
			.build();

	public static List<String> getDataSourceUrl(final ProcessContext context) {
		var urls = Arrays.stream(context.getProperty(DATA_SOURCE_URLS).getValue().split(","))
				.map(String::trim)
				.toList();

		if (urls.stream().allMatch(LdesProcessorProperties::isValidUrl)) {
			return urls;
		} else {
			throw new IllegalArgumentException("Not a (valid list of) datasource url(s)");
		}
	}

	public static Lang getDataSourceFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_SOURCE_FORMAT).getValue());
	}

	public static Lang getDataDestinationFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_DESTINATION_FORMAT).getValue());
	}

	public static String getTimestampPath(final ProcessContext context) {
		return context.getProperty(TIMESTAMP_PATH).getValue();
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

	public static String getOauthScope(final ProcessContext context) {
		return context.getProperty(OAUTH_SCOPE).getValue();
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

	public static String getSqliteDirectory(final ProcessContext context) {
		final String sqliteDirectory = context.getProperty(SQLITE_DIRECTORY).getValue();
		return sqliteDirectory == null ? "ldes-client" : sqliteDirectory;
	}

	public static boolean useVersionMaterialisation(final ProcessContext context) {
		return TRUE.equals(context.getProperty(USE_VERSION_MATERIALISATION).asBoolean());
	}

	public static boolean useExactlyOnceFilter(final ProcessContext context) {
		return TRUE.equals(context.getProperty(USE_EXACTLY_ONCE_FILTER).asBoolean()) && !useVersionMaterialisation(context);
	}

	public static boolean useLatestStateFilter(final ProcessContext context) {
		return TRUE.equals(context.getProperty(USE_LATEST_STATE_FILTER).asBoolean());
	}

	public static boolean restrictToMembers(final ProcessContext context) {
		return TRUE.equals(context.getProperty(RESTRICT_TO_MEMBERS).asBoolean());
	}

	public static String getVersionOfProperty(final ProcessContext context) {
		return context.getProperty(VERSION_OF_PROPERTY).getValue();
	}

	private static boolean isValidUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

}
