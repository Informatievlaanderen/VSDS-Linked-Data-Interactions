package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.util.StandardValidators;

public final class RDF4JRepositoryPutMaterialisationProcessorProperties {

	public static final int SIMULTANEOUS_FLOWFILE_COUNT = 50;

	public static final PropertyDescriptor SPARQL_HOST = new PropertyDescriptor.Builder()
			.name("RDF4J remote repository location")
			.description("The hostname and port of the server.")
			.defaultValue("http://graphdb:7200")
			.required(true)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static final PropertyDescriptor REPOSITORY_ID = new PropertyDescriptor.Builder()
			.name("Repository ID")
			.description("The repository to connect to.")
			.required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor NAMED_GRAPH = new PropertyDescriptor.Builder()
			.name("Named graph")
			.description("If set, the named graph the triples will be written to.")
			.required(false)
			.addValidator(StandardValidators.URI_VALIDATOR)
			.build();

	public static final PropertyDescriptor SIMULTANEOUS_FLOWFILES_TO_PROCESS = new PropertyDescriptor.Builder()
			.name("Flowfiles to process simultaneously")
			.description(
					"An integer denoting the number of flowfiles to processs per transaction. (Fine-tune to find the ideal count)")
			.required(false)
			.defaultValue(SIMULTANEOUS_FLOWFILE_COUNT + "")
			.addValidator(StandardValidators.NUMBER_VALIDATOR)
			.build();

	private RDF4JRepositoryPutMaterialisationProcessorProperties() {

	}
}
