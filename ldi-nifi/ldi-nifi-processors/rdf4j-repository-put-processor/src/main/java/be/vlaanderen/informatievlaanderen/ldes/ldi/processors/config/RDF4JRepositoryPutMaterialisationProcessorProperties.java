package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.util.StandardValidators;

public class RDF4JRepositoryPutMaterialisationProcessorProperties {

	private RDF4JRepositoryPutMaterialisationProcessorProperties() {

	}

	public static final PropertyDescriptor SPARQL_HOST = new PropertyDescriptor.Builder()
			.name("REF4J remote repository location").description("The hostname and port of the server.")
			.defaultValue("http://graphdb:7200").required(true).addValidator(StandardValidators.URL_VALIDATOR).build();

	public static final PropertyDescriptor REPOSITORY_ID = new PropertyDescriptor.Builder().name("Repository ID")
			.description("The repository to connect to.").required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR).build();

	public static final PropertyDescriptor NAMED_GRAPH = new PropertyDescriptor.Builder().name("Named graph")
			.description("If set, the named graph the triples will be written to.").required(false)
			.addValidator(StandardValidators.URI_VALIDATOR).build();
}
