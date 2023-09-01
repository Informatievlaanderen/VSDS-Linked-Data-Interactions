package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;

public class ModelSplitProperties {

	private ModelSplitProperties() {
	}

	public static final Relationship PROCESSED_INPUT_FILE = new Relationship.Builder()
			.name("PROCESSED_INPUT_FILE")
			.description("The input file that was processed")
			.build();

	public static final PropertyDescriptor SUBJECT_TYPE = new PropertyDescriptor.Builder()
			.name("SUBJECT_TYPE")
			.displayName("The property type of the models that need to be extracted.")
			.required(true)
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static final PropertyDescriptor DATA_SOURCE_FORMAT = new PropertyDescriptor.Builder()
			.name("DATA_SOURCE_FORMAT")
			.displayName("Data source format")
			.required(true)
			.defaultValue(Lang.NQUADS.getHeaderString())
			.addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
			.build();

	public static String getSubjectType(final ProcessContext context) {
		return context.getProperty(SUBJECT_TYPE).getValue();
	}

	public static Lang getDataSourceFormat(final ProcessContext context) {
		return RDFLanguages.nameToLang(context.getProperty(DATA_SOURCE_FORMAT).getValue());
	}

}
