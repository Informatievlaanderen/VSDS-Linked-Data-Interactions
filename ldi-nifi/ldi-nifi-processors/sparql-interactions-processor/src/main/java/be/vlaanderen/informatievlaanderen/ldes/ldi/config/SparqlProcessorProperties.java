package be.vlaanderen.informatievlaanderen.ldes.ldi.config;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public class SparqlProcessorProperties {

	private SparqlProcessorProperties() {
	}

	public static final PropertyDescriptor DATA_SOURCE_FORMAT = new PropertyDescriptor.Builder()
			.name("DATA_SOURCE_FORMAT")
			.displayName("Data source format")
			.required(true)
			.defaultValue(Lang.NQUADS.getHeaderString())
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static Lang getDataSourceFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_SOURCE_FORMAT).getValue());
	}

	public static final PropertyDescriptor SPARQL_SELECT_QUERY = new PropertyDescriptor.Builder()
			.name("SPARQL_SELECT_QUERY")
			.displayName("SPARQL Select Query")
			.required(true)
			.defaultValue("SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final AllowableValue REPLACE_ALL = new AllowableValue(
			"replace",
			"Replace mode",
			"In replace mode, the result of the CONSTRUCT query will replace the contents of the flowfile.");

	public static final PropertyDescriptor INCLUDE_ORIGINAL = new PropertyDescriptor.Builder().name("INFERENCE_MODE")
			.displayName("Include original model")
			.required(true)
			.defaultValue(Boolean.FALSE.toString())
			.addValidator(StandardValidators.BOOLEAN_VALIDATOR)
			.build();

	public static final PropertyDescriptor SPARQL_CONSTRUCT_QUERY = new PropertyDescriptor.Builder()
			.name("SPARQL_CONSTRUCT_QUERY")
			.displayName("SPARQL Construct Query")
			.required(true)
			.defaultValue("CONSTRUCT ?s ?p ?o WHERE {?s ?p ?o}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

}
