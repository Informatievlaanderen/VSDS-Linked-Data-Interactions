package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.validators.RDFLanguageValidator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.client.LdesClientDefaults.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public final class LdesProcessorProperties {

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

	public static final PropertyDescriptor FRAGMENT_EXPIRATION_INTERVAL = new PropertyDescriptor.Builder()
			.name("FRAGMENT_EXPIRATION_INTERVAL")
			.displayName("Fragment expiration interval")
			.description(
					"The number of seconds to expire a mutable fragment when the Cache-control header contains no max-age value")
			.required(false)
			.addValidator(StandardValidators.POSITIVE_LONG_VALIDATOR)
			.defaultValue(DEFAULT_FRAGMENT_EXPIRATION_INTERVAL.toString())
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
			.defaultValue("")
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

	public static Long getFragmentExpirationInterval(final ProcessContext context) {
		return Long.valueOf(context.getProperty(FRAGMENT_EXPIRATION_INTERVAL).getValue());
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

}
