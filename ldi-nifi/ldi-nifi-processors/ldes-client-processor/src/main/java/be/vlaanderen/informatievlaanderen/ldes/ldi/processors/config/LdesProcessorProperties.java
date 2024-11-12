package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.validators.RDFLanguageValidator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config.CommonProperties.DATA_DESTINATION_FORMAT;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public final class LdesProcessorProperties {
	/**
	 * The expected RDF format of the LDES data source
	 */
	public static final Lang DEFAULT_DATA_SOURCE_FORMAT = Lang.JSONLD;

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

	public static final PropertyDescriptor STREAM_TIMESTAMP_PATH_PROPERTY = new PropertyDescriptor.Builder()
			.name("STREAM_TIMESTAMP_PATH_PROPERTY")
			.displayName("Stream timestamp path property")
			.description("Stream TimestampPath property to FlowFile")
			.required(true)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(TRUE.toString())
			.build();

	public static final PropertyDescriptor STREAM_VERSION_OF_PROPERTY = new PropertyDescriptor.Builder()
			.name("STREAM_VERSION_OF_PROPERTY")
			.displayName("Stream versionOf property")
			.description("Stream versionOf property to FlowFile")
			.required(true)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(TRUE.toString())
			.build();

	public static final PropertyDescriptor STREAM_SHAPE_PROPERTY = new PropertyDescriptor.Builder()
			.name("STREAM_SHAPE_PROPERTY")
			.displayName("Stream shape property")
			.description("Stream shape property to FlowFile")
			.required(true)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(FALSE.toString())
			.build();

	public static final PropertyDescriptor USE_VERSION_MATERIALISATION = new PropertyDescriptor.Builder()
			.name("USE_VERSION_MATERIALISATION")
			.displayName("Use version materialisation")
			.description("Indicates if the client should return state-objects when enabled or version-objects when disabled")
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

	public static final PropertyDescriptor USE_EXACTLY_ONCE_FILTER = new PropertyDescriptor.Builder()
			.name("USE_EXACTLY_ONCE_FILTER")
			.displayName("Use exactly once filter")
			.description("Use filter so members are outputted exactly once")
			.required(false)
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.defaultValue(TRUE.toString())
			.build();

	public static final PropertyDescriptor USE_LATEST_STATE_FILTER = new PropertyDescriptor.Builder()
			.name("USE_LATEST_STATE_FILTER")
			.displayName("Use latest state filter")
			.description("Use filter to only process the latest state and so all older versions are ignored, only when 'Use version materialisation' is set to true")
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

	public static boolean streamTimestampPathProperty(final ProcessContext context) {
		return TRUE.equals(context.getProperty(STREAM_TIMESTAMP_PATH_PROPERTY).asBoolean());
	}

	public static boolean streamVersionOfProperty(final ProcessContext context) {
		return TRUE.equals(context.getProperty(STREAM_VERSION_OF_PROPERTY).asBoolean());
	}

	public static boolean streamShapeProperty(final ProcessContext context) {
		return TRUE.equals(context.getProperty(STREAM_SHAPE_PROPERTY).asBoolean());
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

	private static boolean isValidUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

}
