package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.util.StandardValidators;

public class SparqlProcessorProperties {

	private SparqlProcessorProperties() {
	}

	public static final PropertyDescriptor SPARQL_SELECT_QUERY = new PropertyDescriptor.Builder()
			.name("SPARQL_SELECT_QUERY")
			.displayName("SPARQL Select Query")
			.required(true)
			.defaultValue("SELECT ?subject ?predicate ?object WHERE {?subject ?predicate ?object}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor INCLUDE_ORIGINAL = new PropertyDescriptor.Builder()
			.name("INCLUDE_ORIGINAL")
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

	public static final PropertyDescriptor SPARQL_ASK_QUERY = new PropertyDescriptor.Builder()
			.name("SPARQL_ASK_QUERY")
			.displayName("SPARQL Ask Query")
			.required(true)
			.defaultValue("ASK WHERE {?s ?p ?o}")
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();
}
