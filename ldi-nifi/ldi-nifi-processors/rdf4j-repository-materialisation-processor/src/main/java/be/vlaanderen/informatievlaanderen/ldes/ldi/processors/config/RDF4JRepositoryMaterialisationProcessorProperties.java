package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import be.vlaanderen.informatievlaanderen.ldes.ldi.processors.validators.RDFLanguageValidator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public final class RDF4JRepositoryMaterialisationProcessorProperties {

	private RDF4JRepositoryMaterialisationProcessorProperties() {
	}

	public static final int SIMULTANEOUS_FLOWFILE_COUNT = 50;

	public static final PropertyDescriptor SPARQL_HOST = new PropertyDescriptor.Builder()
			.name("SPARQL_HOST")
			.displayName("SPARQL host")
			.description("The hostname and port of the RDF4J remote repository server")
			.defaultValue("http://graphdb:7200")
			.required(true)
			.addValidator(StandardValidators.URL_VALIDATOR)
			.build();

	public static final PropertyDescriptor REPOSITORY_ID = new PropertyDescriptor.Builder()
			.name("REPOSITORY_ID")
			.displayName("Repository ID")
			.description("The repository to connect to.")
			.required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor NAMED_GRAPH = new PropertyDescriptor.Builder()
			.name("NAMED_GRAPH")
			.displayName("Named graph")
			.description("If set, the named graph the triples will be written to.")
			.required(false)
			.addValidator(StandardValidators.URI_VALIDATOR)
			.build();

	public static final PropertyDescriptor SIMULTANEOUS_FLOWFILES_TO_PROCESS = new PropertyDescriptor.Builder()
			.name("SIMULTANEOUS_FLOWFILES_TO_PROCESS")
			.displayName("Flowfiles to process simultaneously")
			.description(
					"An integer denoting the number of flowfiles to processs per transaction. (Fine-tune to find the ideal count)")
			.required(false)
			.defaultValue(SIMULTANEOUS_FLOWFILE_COUNT + "")
			.addValidator(StandardValidators.NUMBER_VALIDATOR)
			.build();

	public static final PropertyDescriptor DATA_SOURCE_FORMAT = new PropertyDescriptor.Builder()
			.name("DATA_SOURCE_FORMAT")
			.displayName("Data source format")
			.required(true)
			.defaultValue(Lang.NQUADS.getHeaderString())
			.addValidator(new RDFLanguageValidator())
			.build();

	public static Lang getDataSourceFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_SOURCE_FORMAT).getValue());
	}

}
