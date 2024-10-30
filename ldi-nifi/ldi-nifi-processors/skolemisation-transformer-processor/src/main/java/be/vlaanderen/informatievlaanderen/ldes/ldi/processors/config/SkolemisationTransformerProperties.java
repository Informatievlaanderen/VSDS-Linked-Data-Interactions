package be.vlaanderen.informatievlaanderen.ldes.ldi.processors.config;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;

public class SkolemisationTransformerProperties {
	private SkolemisationTransformerProperties() {
	}

	public static final PropertyDescriptor SKOLEM_DOMAIN = new PropertyDescriptor.Builder()
			.name("SKOLEM_DOMAIN")
			.displayName("Skolem domain")
			.description("Skolemization domain that will be used for transforming blank nodes")
			.required(true)
			.addValidator(StandardValidators.NON_BLANK_VALIDATOR)
			.addValidator(StandardValidators.URI_VALIDATOR)
			.build();

	public static String getSkolemDomain(ProcessContext context) {
		return context.getProperty(SKOLEM_DOMAIN).getValue();
	}
}
