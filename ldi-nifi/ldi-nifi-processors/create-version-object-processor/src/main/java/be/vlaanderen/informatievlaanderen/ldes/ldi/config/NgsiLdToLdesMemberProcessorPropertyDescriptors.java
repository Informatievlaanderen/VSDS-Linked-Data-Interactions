package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.validators.RDFLanguageValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.Validator;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public final class NgsiLdToLdesMemberProcessorPropertyDescriptors {
	private static final Model initModel = ModelFactory.createDefaultModel();

	private static final String DEFAULT_DATE_OBSERVED_VALUE_JSON_PATH = "$.dateObserved.value['@value']";
	private static final String DEFAULT_ID_JSON_PATH = "$.id";
	private static final String DEFAULT_DELIMITER = "/";
	private static final String DEFAULT_VERSION_OF_KEY = "http://purl.org/dc/terms/isVersionOf";
	private static final String DEFAULT_DATA_DESTINATION_FORMAT = "n-quads";
	private static final String DEFAULT_PROV_GENERATED_AT_TIME = "http://www.w3.org/ns/prov#generatedAtTime";

	private NgsiLdToLdesMemberProcessorPropertyDescriptors() {
	}

	public static final PropertyDescriptor MEMBER_RDF_SYNTAX_TYPE = new PropertyDescriptor.Builder()
			.name("MEMBER_RDF_SYNTAX_TYPE")
			.displayName("IRI to member RDF syntax type")
			.description("IRI that declares a http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
			.required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor DELIMITER = new PropertyDescriptor.Builder()
			.name("DELIMITER")
			.displayName("Delimiter between entity ID and timestamp value")
			.description("Delimiter between entity ID and timestamp value")
			.required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.defaultValue(DEFAULT_DELIMITER)
			.build();

	public static final PropertyDescriptor DATE_OBSERVED_VALUE_JSON_PATH = new PropertyDescriptor.Builder()
			.name("DATE_OBSERVED_VALUE_JSON_PATH")
			.displayName("JSON path to a timestamp value")
			.description(
					"JSON path to a timestamp value (for object version ID), e.g. "
							+ DEFAULT_DATE_OBSERVED_VALUE_JSON_PATH)
			.required(false)
			.addValidator(Validator.VALID)
			.defaultValue(DEFAULT_DATE_OBSERVED_VALUE_JSON_PATH)
			.build();

	public static final PropertyDescriptor VERSION_OF_KEY = new PropertyDescriptor.Builder()
			.name("VERSION_OF_KEY")
			.displayName("VersionOf Property")
			.description("VersionOf Property, e.g. " + DEFAULT_VERSION_OF_KEY)
			.required(false)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.defaultValue(DEFAULT_VERSION_OF_KEY)
			.build();

	public static final PropertyDescriptor DATA_DESTINATION_FORMAT = new PropertyDescriptor.Builder()
			.name("DATA_DESTINATION_FORMAT")
			.displayName("Data destination format")
			.description("RDF format identifier of the data destination")
			.required(false)
			.addValidator(new RDFLanguageValidator())
			.defaultValue(DEFAULT_DATA_DESTINATION_FORMAT)
			.build();

	public static final PropertyDescriptor GENERATED_AT_TIME_PROPERTY = new PropertyDescriptor.Builder()
			.name("GENERATED_AT_TIME_PROPERTY")
			.displayName("GeneratedAtTime property")
			.description("GeneratedAtTime property")
			.required(false)
			.addValidator(Validator.VALID)
			.defaultValue(DEFAULT_PROV_GENERATED_AT_TIME)
			.build();

	public static Property getDateObservedValueJsonPath(ProcessContext context) {
		return initModel.createProperty(context.getProperty(DATE_OBSERVED_VALUE_JSON_PATH).getValue());
	}

	public static Resource getMemberRdfSyntaxType(ProcessContext context) {
		return initModel.createResource(context.getProperty(MEMBER_RDF_SYNTAX_TYPE).getValue());
	}

	public static String getDelimiter(ProcessContext context) {
		return context.getProperty(DELIMITER).getValue();
	}

	public static Property getVersionOfKey(ProcessContext context) {
		return initModel.createProperty(context.getProperty(VERSION_OF_KEY).getValue());
	}

	public static Property getGeneratedAtTimeProperty(ProcessContext context) {
		String generatedAtTimeProperty = context.getProperty(GENERATED_AT_TIME_PROPERTY).getValue();
		if (generatedAtTimeProperty.equals("")) {
			return null;
		}
		else {
			return initModel.createProperty(generatedAtTimeProperty);
		}

	}
	public static Lang getDataDestinationFormat(ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_DESTINATION_FORMAT).getValue());
	}
}
